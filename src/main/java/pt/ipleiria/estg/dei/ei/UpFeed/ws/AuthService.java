package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.AuthDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.dtos.PersonDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.JwtBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;
import pt.ipleiria.estg.dei.ei.UpFeed.jwt.Jwt;
import pt.ipleiria.estg.dei.ei.UpFeed.dtos.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/auth")
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class AuthService {

    private static final Logger log =
            Logger.getLogger(AuthService.class.getName());

    @EJB
    JwtBean jwtBean;

    @EJB
    PersonBean personBean;

    @POST
    @Path("/login")
    public Response authenticateUser(AuthDTO authDTO) {
        try {
            Person user = personBean.authenticate(authDTO.getEmail(), authDTO.getPassword());
            System.out.println("Login Service");
            if (user != null) {
                if (user.getId() > 0) {
                    log.info("Generating JWT for user " + user.getId());
                }
                String token = jwtBean.createJwt(String.valueOf(user.getId()), new
                        String[]{user.getClass().getSimpleName()});
                return Response.ok(new Jwt("Bearer",token)).build();
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/user")
    @RolesAllowed({"Administrator", "Teacher", "Student"})
    public Response demonstrateClaims(@HeaderParam("Authorization") String auth) {
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                Person person = personBean.getPersonByAuthToken(auth);
                return Response.ok(toDTO(person)).build();
            } catch (ParseException e) {
                log.warning(e.toString());
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        return Response.status(Response.Status.NO_CONTENT).build(); //no jwt means no claims to extract
    }

    @PATCH
    @Path("/updatepassword")
    @RolesAllowed({"Administrator", "Teacher", "Student"})
    public Response selfUpdatePasswordWS(@HeaderParam("Authorization") String auth, NewPasswordDTO newPasswordDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        personBean.updatePassword(
                person.getId(),
                newPasswordDTO.getOldPassword(),
                newPasswordDTO.getNewPassword());

        return Response.status(Response.Status.OK)
                .build();
    }

    @PUT
    @Path("/update")
    @RolesAllowed({"Administrator", "Teacher", "Student"})
    public Response selfUpdateWS(@HeaderParam("Authorization") String auth, PersonDTO personDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        personBean.update(
                person.getId(),
                personDTO.getEmail(),
                personDTO.getName());

        person = personBean.findPerson(person.getId());

        return Response.status(Response.Status.OK)
                .entity(toDTO(person))
                .build();
    }

    private PersonDTO toDTO(Person person) {
        switch (person.getClass().getSimpleName()){
            case "Administrator":
                return new PersonDTO(
                        person.getId(),
                        person.getEmail(),
                        person.getName(),
                        person.getClass().getSimpleName());
            case "Student":
            case "Teacher":
                return new PersonDTO(
                        person.getId(),
                        person.getEmail(),
                        person.getName(),
                        person.getClass().getSimpleName(),
                        channelToDTOs(((Student)person).getChannels()));
        }
        return new PersonDTO();
    }

    private List<ChannelDTO> channelToDTOs(List<Channel> channels) {
        return channels.stream().map(this::channelToDTO).collect(Collectors.toList());
    }

    private ChannelDTO channelToDTO(Channel channel) {
        return new ChannelDTO(
                channel.getId(),
                userToDTO(channel.getOwner()),
                channel.getTitle(),
                channel.getDescription(),
                channel.getType(),
                channel.getWeight());
    }

    private UserDTO userToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getName());
    }
}

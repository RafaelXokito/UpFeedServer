package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.*;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.ChannelBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("channels") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@RolesAllowed({"Student","Teacher"})
public class ChannelService {
    @EJB
    private ChannelBean channelBean;

    @EJB
    private PersonBean personBean;

    @Context
    private SecurityContext securityContext;

    private static final Logger logger = Logger.getLogger("ws.ChannelService");

    @GET
    @Path("/")
    public Response getAllChannelsWS(@HeaderParam("Authorization") String auth) throws Exception{
        Person person = personBean.getPersonByAuthToken(auth);

        return Response.status(Response.Status.OK)
                .entity(toDTOs(channelBean.getAllChannelsByUser(person.getId())))
                .build();
    }

    @GET
    @Path("{id}")
    public Response getChannelWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Channel channel = channelBean.findChannel(id);
        for (User index :channel.getUsers()) {
            if (person.getId().equals(index.getId()))
                return Response.status(Response.Status.OK)
                        .entity(toDTO(channel))
                        .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This channel dont belong to you")
                .build();
    }

    @POST
    @Path("/")
    public Response createChannelWS(@HeaderParam("Authorization") String auth, ChannelDTO channelDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        long id = channelBean.create(
                person.getId(),
                channelDTO.getTitle(),
                channelDTO.getDescription(),
                channelDTO.getType(),
                channelDTO.getWeight());

        Channel channel = channelBean.findChannel(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(channel))
                .build();
    }

    @POST
    @Path("/{id}/students")
    public Response associateStudentToChannelWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id, ChannelDTO channelDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Channel checkChannel = channelBean.findChannel(id);
        if (!checkChannel.getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This channel dont belong to you")
                    .build();

        for (UserDTO userDTO : channelDTO.getUsers()) {
            try {
                channelBean.addUserToChannel(id, userDTO.getEmail());
            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        Channel channel = channelBean.findChannel(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(channel))
                .build();
    }

    @DELETE
    @Path("/{id}/students")
    public Response desassociateStudentToChannelRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id, ChannelDTO channelDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Channel checkChannel = channelBean.findChannel(id);
        if (!checkChannel.getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This channel dont belong to you")
                    .build();

        for (UserDTO userDTO : channelDTO.getUsers()) {
            try {
                channelBean.removeUserToChannel(id, userDTO.getEmail());
            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        Channel channel = channelBean.findChannel(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(channel))
                .build();
    }

    @PUT
    @Path("{id}")
    public Response updateChannelWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id,ChannelDTO channelDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Channel checkChannel = channelBean.findChannel(id);
        if (!checkChannel.getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This channel dont belong to you")
                    .build();

        channelBean.update(
                id,
                channelDTO.getTitle(),
                channelDTO.getDescription(),
                channelDTO.getWeight());

        Channel channel = channelBean.findChannel(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(channel))
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteChannelWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Channel checkChannel = channelBean.findChannel(id);
        if (!checkChannel.getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This channel dont belong to you")
                    .build();

        if (channelBean.delete(id))
            return Response.status(Response.Status.OK)
                    .entity("Channel "+id+" deleted!")
                    .build();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }


    private List<ChannelDTO> toDTOs(List<Channel> channels) {
        return channels.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ChannelDTO toDTO(Channel channel) {
        return new ChannelDTO(
                channel.getId(),
                userToDTO(channel.getOwner()),
                channel.getTitle(),
                channel.getDescription(),
                channel.getType(),
                channel.getWeight(),
                usersToDTOs(channel.getUsers()));
    }

    private List<UserDTO> usersToDTOs(List<User> users) {
        return users.stream().map(this::userToDTO).collect(Collectors.toList());
    }

    private UserDTO userToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getName());
    }
}

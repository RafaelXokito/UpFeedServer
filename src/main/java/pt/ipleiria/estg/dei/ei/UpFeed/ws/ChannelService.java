package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.ChannelDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.dtos.TeacherDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.dtos.UserDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.ChannelBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Channel;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Teacher;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.User;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("channels") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class ChannelService {
    @EJB
    private ChannelBean channelBean;

    private static final Logger logger = Logger.getLogger("ws.ChannelService");

    @GET
    @Path("/")
    public Response getAllChannelsWS() {
        return Response.status(Response.Status.OK)
                .entity(toDTOs(channelBean.getAllChannels()))
                .build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"Channel"})
    public Response getChannelWS(@PathParam("id") long id) throws Exception {
        Channel channel = channelBean.findChannel(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(channel))
                .build();
    }

    @POST
    @Path("/")
    public Response createChannelWS(ChannelDTO channelDTO) throws Exception {
        long id = channelBean.create(
                2L, //TODO  - GETS THE AUTHENTICATED USER ID
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
    public Response associateStudentToChannelWS(@PathParam("id") long id, ChannelDTO channelDTO) throws Exception {
        System.out.println(channelDTO.getUsers().size());
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

    @PUT
    @Path("{id}")
    @RolesAllowed({"Channel"})
    public Response updateChannelWS(@PathParam("id") long id,ChannelDTO channelDTO) throws Exception {
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
    @RolesAllowed({"Channel"})
    public Response deleteChannelWS(@PathParam("id") long id) throws Exception {
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

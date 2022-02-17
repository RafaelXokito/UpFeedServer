package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.*;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.StudyRoomBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("studyrooms") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class StudyRoomService {
    @EJB
    private StudyRoomBean studyRoomBean;

    private static final Logger logger = Logger.getLogger("ws.StudyRoomService");

    @GET
    @Path("/")
    public Response getAllStudyRoomsWS() {
        return Response.status(Response.Status.OK)
                .entity(toDTOs(studyRoomBean.getAllStudyRooms()))
                .build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"StudyRoom"})
    public Response getStudyRoomWS(@PathParam("id") long id) throws Exception {
        StudyRoom studyRoom = studyRoomBean.findStudyRoom(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(studyRoom))
                .build();
    }

    @POST
    @Path("/")
    public Response createStudyRoomWS(StudyRoomDTO studyRoomDTO) throws Exception {
        long id = studyRoomBean.create(
                studyRoomDTO.getChannel().getId(),
                studyRoomDTO.getTitle(),
                studyRoomDTO.getDescription());

        StudyRoom studyRoom = studyRoomBean.findStudyRoom(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(studyRoom))
                .build();
    }

    @POST
    @Path("/{id}/students")
    public Response associateStudentToStudyRoomWS(@PathParam("id") long id, StudyRoomDTO studyRoomDTO) throws Exception {

        for (StudentDTO studentDTO : studyRoomDTO.getStudents()) {
            try {
                studyRoomBean.addStudentToStudyRoom(id, studentDTO.getEmail());
            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        StudyRoom studyRoom = studyRoomBean.findStudyRoom(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(studyRoom))
                .build();
    }

    @DELETE
    @Path("/{id}/students")
    public Response desassociateStudentToStudyRoomWS(@PathParam("id") long id, StudyRoomDTO studyRoomDTO) throws Exception {

        for (StudentDTO studentDTO : studyRoomDTO.getStudents()) {
            try {
                studyRoomBean.removeStudentToStudyRoom(id, studentDTO.getEmail());
            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        StudyRoom studyRoom = studyRoomBean.findStudyRoom(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(studyRoom))
                .build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"StudyRoom"})
    public Response updateStudyRoomWS(@PathParam("id") long id,StudyRoomDTO studyRoomDTO) throws Exception {
        studyRoomBean.update(
                id,
                studyRoomDTO.getTitle(),
                studyRoomDTO.getDescription());

        StudyRoom studyRoom = studyRoomBean.findStudyRoom(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(studyRoom))
                .build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"StudyRoom"})
    public Response deleteStudyRoomWS(@PathParam("id") long id) throws Exception {
        if (studyRoomBean.delete(id))
            return Response.status(Response.Status.OK)
                    .entity("StudyRoom "+id+" deleted!")
                    .build();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }


    private List<StudyRoomDTO> toDTOs(List<StudyRoom> studyRooms) {
        return studyRooms.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private StudyRoomDTO toDTO(StudyRoom studyRoom) {
        return new StudyRoomDTO(
                studyRoom.getId(),
                studyRoom.getTitle(),
                studyRoom.getDescription(),
                studentsToDTOs(studyRoom.getStudents()),
                channelToDTO(studyRoom.getChannel()));
    }

    private ChannelDTO channelToDTO(Channel channel) {
        return new ChannelDTO(
                channel.getId(),
                userToDTO(channel.getOwner()),
                channel.getTitle(),
                channel.getDescription(),
                channel.getType(),
                channel.getWeight(),
                usersToDTOs(channel.getUsers()));
    }

    private List<StudentDTO> studentsToDTOs(List<Student> students) {
        return students.stream().map(this::studentToDTO).collect(Collectors.toList());
    }

    private StudentDTO studentToDTO(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getEmail(),
                student.getName());
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

package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.*;
import pt.ipleiria.estg.dei.ei.UpFeed.dtos.StudentDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.SubjectRoomBean;
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

@Path("subjectRooms") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class SubjectRoomService {
    @EJB
    private SubjectRoomBean subjectRoomBean;

    private static final Logger logger = Logger.getLogger("ws.SubjectRoomService");

    @GET
    @Path("/")
    public Response getAllSubjectRoomsWS() {
        return Response.status(Response.Status.OK)
                .entity(toDTOs(subjectRoomBean.getAllSubjectRooms()))
                .build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"SubjectRoom"})
    public Response getSubjectRoomWS(@PathParam("id") long id) throws Exception {
        SubjectRoom subjectRoom = subjectRoomBean.findSubjectRoom(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(subjectRoom))
                .build();
    }

    @POST
    @Path("/")
    public Response createSubjectRoomWS(SubjectRoomDTO subjectRoomDTO) throws Exception {
        long id = subjectRoomBean.create(
                subjectRoomDTO.getTeacher().getEmail(),
                subjectRoomDTO.getChannel().getId(),
                subjectRoomDTO.getTitle(),
                subjectRoomDTO.getDescription(),
                subjectRoomDTO.getWeight());

        SubjectRoom subjectRoom = subjectRoomBean.findSubjectRoom(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(subjectRoom))
                .build();
    }

    @POST
    @Path("/{id}/students")
    public Response associateStudentToSubjectRoomWS(@PathParam("id") long id, SubjectRoomDTO subjectRoomDTO) throws Exception {

        for (StudentDTO studentDTO : subjectRoomDTO.getStudents()) {
            try {
                subjectRoomBean.addStudentToSubjectRoom(id, studentDTO.getEmail());
            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        SubjectRoom subjectRoom = subjectRoomBean.findSubjectRoom(id);

        return Response.status(Response.Status.CREATED)
                .entity(studentsToDTOs(subjectRoom.getStudents()))
                .build();
    }

    @DELETE
    @Path("/{id}/students")
    public Response desassociateStudentToSubjectRoomWS(@PathParam("id") long id, SubjectRoomDTO subjectRoomDTO) throws Exception {

        for (StudentDTO studentDTO : subjectRoomDTO.getStudents()) {
            try {
                subjectRoomBean.removeStudentToSubjectRoom(id, studentDTO.getEmail());
            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        SubjectRoom subjectRoom = subjectRoomBean.findSubjectRoom(id);

        return Response.status(Response.Status.CREATED)
                .entity(studentsToDTOs(subjectRoom.getStudents()))
                .build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"SubjectRoom"})
    public Response updateSubjectRoomWS(@PathParam("id") long id,SubjectRoomDTO subjectRoomDTO) throws Exception {
        subjectRoomBean.update(
                id,
                subjectRoomDTO.getTitle(),
                subjectRoomDTO.getDescription(),
                subjectRoomDTO.getWeight());

        SubjectRoom subjectRoom = subjectRoomBean.findSubjectRoom(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(subjectRoom))
                .build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"SubjectRoom"})
    public Response deleteSubjectRoomWS(@PathParam("id") long id) throws Exception {
        if (subjectRoomBean.delete(id))
            return Response.status(Response.Status.OK)
                    .entity("SubjectRoom "+id+" deleted!")
                    .build();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }


    private List<SubjectRoomDTO> toDTOs(List<SubjectRoom> subjectRooms) {
        return subjectRooms.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private SubjectRoomDTO toDTO(SubjectRoom subjectRoom) {
        return new SubjectRoomDTO(
                subjectRoom.getId(),
                subjectRoom.getTitle(),
                subjectRoom.getDescription(),
                studentsToDTOs(subjectRoom.getStudents()),
                channelToDTO(subjectRoom.getChannel()),
                subjectRoom.getWeight(),
                teacherToDTO(subjectRoom.getTeacher()));
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
                channel.getWeight(),
                usersToDTOs(channel.getUsers()));
    }

    private List<TeacherDTO> teachersToDTOs(List<Teacher> teachers) {
        return teachers.stream().map(this::teacherToDTO).collect(Collectors.toList());
    }

    private TeacherDTO teacherToDTO(Teacher teacher) {
        return new TeacherDTO(
                teacher.getId(),
                teacher.getEmail(),
                teacher.getName());
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

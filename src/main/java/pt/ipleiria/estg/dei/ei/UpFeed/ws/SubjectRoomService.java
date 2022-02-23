package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.*;
import pt.ipleiria.estg.dei.ei.UpFeed.dtos.StudentDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.ChannelBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.SubjectRoomBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("subjectrooms") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@RolesAllowed({"Student","Teacher"})
public class SubjectRoomService {
    @EJB
    private SubjectRoomBean subjectRoomBean;

    @EJB
    private ChannelBean channelBean;

    @EJB
    private PersonBean personBean;

    @Context
    private SecurityContext securityContext;

    private static final Logger logger = Logger.getLogger("ws.SubjectRoomService");

    @GET
    @Path("/")
    public Response getAllSubjectRoomsWS(@HeaderParam("Authorization") String auth) throws Exception{
        Person person = personBean.getPersonByAuthToken(auth);

        return Response.status(Response.Status.OK)
                .entity(toDTOs(subjectRoomBean.getAllSubjectRoomsByUser(person.getId())))
                .build();
    }

    @GET
    @Path("{id}")
    public Response getSubjectRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);
        
        SubjectRoom subjectRoom = subjectRoomBean.findSubjectRoom(id);
        for (Student index :subjectRoom.getStudents()) {
            if (person.getId().equals(index.getId()))
                return Response.status(Response.Status.OK)
                        .entity(toDTO(subjectRoom))
                        .build();
        }
        if (subjectRoom.getTeacher().getId().equals(person.getId()))
            return Response.status(Response.Status.OK)
                    .entity(toDTO(subjectRoom))
                    .build();
        if (subjectRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.OK)
                    .entity(toDTO(subjectRoom))
                    .build();
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This Subject Room dont belong to you")
                .build();
    }

    @POST
    @Path("/")
    public Response createSubjectRoomWS(@HeaderParam("Authorization") String auth, SubjectRoomDTO subjectRoomDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Channel channel = channelBean.findChannel(subjectRoomDTO.getChannel().getId());
        if (channel.getOwner().getId().equals(person.getId())) {
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
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This Subject Room dont belong to you")
                .build();
    }

    @POST
    @Path("/{id}/students")
    public Response associateStudentToSubjectRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id, SubjectRoomDTO subjectRoomDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        SubjectRoom checkStudyRoom = subjectRoomBean.findSubjectRoom(id);
        if (!checkStudyRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This Subject Room dont belong to you")
                    .build();
        
        for (StudentDTO studentDTO : subjectRoomDTO.getStudents()) {
            try {
                subjectRoomBean.addStudentToSubjectRoom(id, studentDTO.getEmail());
            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        SubjectRoom subjectRoom = subjectRoomBean.findSubjectRoom(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(subjectRoom))
                .build();
    }

    @DELETE
    @Path("/{id}/students")
    public Response desassociateStudentToSubjectRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id, SubjectRoomDTO subjectRoomDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        SubjectRoom checkStudyRoom = subjectRoomBean.findSubjectRoom(id);
        if (!checkStudyRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This Subject Room dont belong to you")
                    .build();
        
        for (StudentDTO studentDTO : subjectRoomDTO.getStudents()) {
            try {
                subjectRoomBean.removeStudentToSubjectRoom(id, studentDTO.getEmail());
            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }

        SubjectRoom subjectRoom = subjectRoomBean.findSubjectRoom(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(subjectRoom))
                .build();
    }

    @PUT
    @Path("{id}")
    public Response updateSubjectRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id,SubjectRoomDTO subjectRoomDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        SubjectRoom checkStudyRoom = subjectRoomBean.findSubjectRoom(id);
        if (!checkStudyRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This Subject Room dont belong to you")
                    .build();
        
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
    public Response deleteSubjectRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        SubjectRoom checkStudyRoom = subjectRoomBean.findSubjectRoom(id);
        if (!checkStudyRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This Subject Room dont belong to you")
                    .build();
        
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

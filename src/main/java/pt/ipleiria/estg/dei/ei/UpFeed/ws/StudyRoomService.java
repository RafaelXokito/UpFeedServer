package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.*;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.ChannelBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.StudyRoomBean;
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

@Path("studyrooms") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@RolesAllowed({"Student"})
public class StudyRoomService {
    @EJB
    private StudyRoomBean studyRoomBean;

    @EJB
    private ChannelBean channelBean;

    @EJB
    private PersonBean personBean;

    @Context
    private SecurityContext securityContext;

    private static final Logger logger = Logger.getLogger("ws.StudyRoomService");

    @GET
    @Path("/")
    public Response getAllStudyRoomsWS(@HeaderParam("Authorization") String auth) throws Exception{
        Person person = personBean.getPersonByAuthToken(auth);

        return Response.status(Response.Status.OK)
                .entity(toDTOs(studyRoomBean.getAllStudyRoomsByStudent(person.getId())))
                .build();
    }

    @GET
    @Path("{id}")
    public Response getStudyRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        StudyRoom studyRoom = studyRoomBean.findStudyRoom(id);
        for (Student index :studyRoom.getStudents()) {
            if (person.getId().equals(index.getId()))
                return Response.status(Response.Status.OK)
                        .entity(toDTO(studyRoom))
                        .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This Study Room dont belong to you")
                .build();
    }

    @POST
    @Path("/")
    public Response createStudyRoomWS(@HeaderParam("Authorization") String auth, StudyRoomDTO studyRoomDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Channel channel = channelBean.findChannel(studyRoomDTO.getChannel().getId());
        if (channel.getOwner().getId().equals(person.getId())) {
            long id = studyRoomBean.create(
                    studyRoomDTO.getChannel().getId(),
                    studyRoomDTO.getTitle(),
                    studyRoomDTO.getDescription());

            StudyRoom studyRoom = studyRoomBean.findStudyRoom(id);

            return Response.status(Response.Status.CREATED)
                    .entity(toDTO(studyRoom))
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This Study Room dont belong to you")
                .build();
    }

    @POST
    @Path("/{id}/students")
    public Response associateStudentToStudyRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id, StudyRoomDTO studyRoomDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        StudyRoom checkStudyRoom = studyRoomBean.findStudyRoom(id);
        if (!checkStudyRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This Study Room dont belong to you")
                    .build();

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
    public Response desassociateStudentToStudyRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id, StudyRoomDTO studyRoomDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        StudyRoom checkStudyRoom = studyRoomBean.findStudyRoom(id);
        if (!checkStudyRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This Study Room dont belong to you")
                    .build();

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
    public Response updateStudyRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id,StudyRoomDTO studyRoomDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        StudyRoom checkStudyRoom = studyRoomBean.findStudyRoom(id);
        if (!checkStudyRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This Study Room dont belong to you")
                    .build();

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
    public Response deleteStudyRoomWS(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        StudyRoom checkStudyRoom = studyRoomBean.findStudyRoom(id);
        if (!checkStudyRoom.getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This Study Room dont belong to you")
                    .build();

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

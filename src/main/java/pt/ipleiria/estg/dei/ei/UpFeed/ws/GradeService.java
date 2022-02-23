package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.GradeDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.GradeBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Grade;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Person;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("grades") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@RolesAllowed({"Student","Teacher"})
public class GradeService {

    @EJB
    private GradeBean gradeBean;

    @EJB
    private PersonBean personBean;

    @Context
    private SecurityContext securityContext;


    @GET
    @Path("/")
    public Response getAllGradesWS(@HeaderParam("Authorization") String auth) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        if (securityContext.isUserInRole("Teacher")) {
            return Response.status(Response.Status.OK)
                    .entity(toDTOs(gradeBean.getAllGradesClassByTeacher(person.getId())))
                    .build();
        }

        return Response.status(Response.Status.OK)
                .entity(toDTOs(gradeBean.getAllGradesClassByStudent(person.getId())))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getGradeDetails(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Grade grade = gradeBean.find(id);
        if (grade.getStudent().getId().equals(person.getId()) ||
                grade.getSubjectRoom().getTeacher().getId().equals(person.getId()) ||
                grade.getSubjectRoom().getChannel().getOwner().getId().equals(person.getId()))
            return Response.status(Response.Status.OK)
                    .entity(toDTO(grade))
                    .build();

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This grade dont belong to you")
                .build();
    }

    @POST
    @Path("/")
    @RolesAllowed({"Teacher"})
    public Response createGrade(@HeaderParam("Authorization") String auth, GradeDTO gradeDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        if (gradeBean.findSubjectRoom(gradeDTO.getSubjectRoomId()).getTeacher().getId().equals(person.getId())) {
            long id = gradeBean.create(gradeDTO.getValue(), gradeDTO.getStudentEmail(), gradeDTO.getSubjectRoomId(), gradeDTO.getObservations());
            Grade grade = gradeBean.find(id);
            return Response.status(Response.Status.OK)
                    .entity(toDTO(grade))
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("The Subject Room dont belong to you")
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"Teacher"})
    public Response updateGrade(@HeaderParam("Authorization") String auth, @PathParam("id") long id, GradeDTO gradeDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Grade checkGrade = gradeBean.find(id);
        if (checkGrade.getSubjectRoom().getTeacher().getId().equals(person.getId())) {
            gradeBean.update(id, gradeDTO.getValue(), gradeDTO.getObservations());
            Grade grade = gradeBean.find(id);
            return Response.status(Response.Status.OK)
                    .entity(toDTO(grade))
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This Grade cant be updated by you")
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"Teacher"})
    public Response deleteGrade(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Grade grade = gradeBean.find(id);
        if (grade.getSubjectRoom().getTeacher().getId().equals(person.getId())) {
            if (gradeBean.delete(id)) {
                return Response.status(Response.Status.OK)
                        .entity(toDTO(grade))
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(toDTO(grade))
                    .build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This Grade cant be updated by you")
                .build();
    }

    private List<GradeDTO> toDTOs(List<Grade> grades){
        return grades.stream().map(this::toDTO).collect(Collectors.toList());
    }
    private GradeDTO toDTO(Grade grade){
        return new GradeDTO(
                grade.getId(),
                grade.getValue(),
                grade.getStudent().getEmail(),
                grade.getSubjectRoom().getId(),
                grade.getObservations()
        );
    }
}

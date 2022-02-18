package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.GradeDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.GradeBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Grade;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("grades") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class GradeService {

    @EJB
    private GradeBean gradeBean;

    @GET
    @Path("/")
    public Response getAllGradesWS() {
        return Response.status(Response.Status.OK)
                .entity(toDTOs(gradeBean.getAllGrades()))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getGradeDetails(@PathParam("id") long id) throws Exception {
        Grade grade = gradeBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(grade))
                .build();
    }

    @POST
    @Path("/")
    public Response createGrade(GradeDTO gradeDTO) throws Exception {
        long id = gradeBean.create(gradeDTO.getValue(), gradeDTO.getStudentEmail(), gradeDTO.getSubjectRoomId(), gradeDTO.getObservations());
        Grade grade = gradeBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(grade))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateGrade(@PathParam("id") long id, GradeDTO gradeDTO) throws Exception {
        gradeBean.update(gradeDTO.getId(), gradeDTO.getValue(), gradeDTO.getObservations());
        Grade grade = gradeBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(grade))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteGrade(@PathParam("id") long id) throws Exception {
        Grade grade = gradeBean.find(id);
        if(gradeBean.delete(id)){
            return Response.status(Response.Status.OK)
                    .entity(toDTO(grade))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(toDTO(grade))
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

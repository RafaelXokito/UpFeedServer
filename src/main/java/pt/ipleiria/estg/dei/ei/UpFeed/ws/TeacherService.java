package pt.ipleiria.estg.dei.ei.UpFeed.ws;


import pt.ipleiria.estg.dei.ei.UpFeed.dtos.TeacherDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.TeacherBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Teacher;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("teachers") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class TeacherService {

    @EJB
    private TeacherBean teacherBean;

    @GET
    @Path("/")
    public Response getAllTeachersWS() {
        return Response.status(Response.Status.OK)
                .entity(toDTOs(teacherBean.getAllTeachers()))
                .build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"Teacher"})
    public Response getTeacherWS(@PathParam("id") long id) throws Exception {
        Teacher teacher = teacherBean.findTeacher(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(teacher))
                .build();
    }

    @POST
    @Path("/")
    public Response createTeacherWS(TeacherDTO teacherDTO) throws Exception {
        long id = teacherBean.create(
                teacherDTO.getEmail(),
                teacherDTO.getPassword(),
                teacherDTO.getName());

        Teacher teacher = teacherBean.findTeacher(id);

        return Response.status(Response.Status.CREATED)
                .entity(toDTO(teacher))
                .build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"Teacher"})
    public Response updateTeacherWS(@PathParam("id") long id,TeacherDTO teacherDTO) throws Exception {
        teacherBean.update(
                id,
                teacherDTO.getEmail(),
                teacherDTO.getName());

        Teacher teacher = teacherBean.findTeacher(id);

        return Response.status(Response.Status.OK)
                .entity(toDTO(teacher))
                .build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed({"Teacher"})
    public Response deleteTeacherWS(@PathParam("id") long id) throws Exception {
        if (teacherBean.delete(id))
            return Response.status(Response.Status.OK)
                    .entity("Teacher "+id+" deleted!")
                    .build();

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }


    private List<TeacherDTO> toDTOs(List<Teacher> teachers) {
        return teachers.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private TeacherDTO toDTO(Teacher teacher) {
        return new TeacherDTO(
                teacher.getId(),
                teacher.getEmail(),
                teacher.getName());
    }
}

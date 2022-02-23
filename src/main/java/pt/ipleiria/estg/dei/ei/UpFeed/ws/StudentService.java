package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.StudentDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.StudentBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Person;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Student;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

@Path("students") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class StudentService {
    @EJB
    private StudentBean studentBean;

    @EJB
    private PersonBean personBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    @RolesAllowed({"Administrator"})
    public Response getAllStudentsWS(){
        return Response.status(Response.Status.OK)
                .entity(toDTOs(studentBean.getAllStudents()))
                .build();
    }


    @GET
    @Path("/{id}")
    @RolesAllowed({"Administrator"})
    public Response getStudentDetails(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Student student = studentBean.findStudent(id);
        if (securityContext.isUserInRole("Administrator") ||
                student.getId().equals(person.getId()))
            return Response.status(Response.Status.OK)
                    .entity(toDTO(studentBean.findStudent(id)))
                    .build();

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("You are not allowed to see this student details")
                .build();
    }

    @POST
    @Path("/")
    public Response createStudent(StudentDTO studentDTO) throws Exception {
        long id = studentBean.create(studentDTO.getName(),studentDTO.getEmail(),studentDTO.getPassword());

        Student student = studentBean.findStudent(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(student))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"Administrator"})
    public Response updateStudent(@PathParam("id") long id, StudentDTO studentDTO) throws Exception {
        studentBean.update(id,studentDTO.getName(),studentDTO.getEmail());
        Student student = studentBean.findStudent(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(student))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"Administrator"})
    public Response deleteStudent(@PathParam("id") long id) throws Exception {
        Student student = studentBean.findStudent(id);
        if(studentBean.delete(id)){
            return Response.status(Response.Status.OK)
                    .entity(toDTO(student))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }

    private List<StudentDTO> toDTOs(List<Student> students) {
        return students.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private StudentDTO toDTO(Student student) {
        return new StudentDTO(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getPassword());
    }
}

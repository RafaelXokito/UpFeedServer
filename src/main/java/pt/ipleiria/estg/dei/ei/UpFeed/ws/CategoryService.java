package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.CategoryDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.CategoryBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Category;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Person;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyUnauthorizedException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Path("categories") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@RolesAllowed({"Student","Teacher"})
public class CategoryService {

    @EJB
    private CategoryBean categoryBean;

    @EJB
    private PersonBean personBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    public Response getAllCategoriesWS(@HeaderParam("Authorization") String auth) throws Exception{
        Person person = personBean.getPersonByAuthToken(auth);

        return Response.status(Response.Status.OK)
                .entity(toDTOs(categoryBean.getAllCategoriesByUser(person.getId())))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getCategoryDetails(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Category category = categoryBean.find(id);
        if (Objects.equals(category.getOwner().getId(), person.getId()))
            return Response.status(Response.Status.OK)
                    .entity(toDTO(category))
                    .build();
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This category dont belong to you")
                .build();
    }

    @POST
    @Path("/")
    public Response createCategory(CategoryDTO categoryDTO) throws Exception {
        long id = categoryBean.create(categoryDTO.getEmailOwner(), categoryDTO.getName());

        Category category = categoryBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(category))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCategory(@HeaderParam("Authorization") String auth, @PathParam("id") long id, CategoryDTO categoryDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Category checkCategory = categoryBean.find(id);
        if (!Objects.equals(checkCategory.getOwner().getId(), person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This category dont belong to you")
                    .build();

        categoryBean.update(id, categoryDTO.getName());
        Category category = categoryBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(category))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Category checkCategory = categoryBean.find(id);
        if (!Objects.equals(checkCategory.getOwner().getId(), person.getId()))
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("This category dont belong to you")
                    .build();

        Category category = categoryBean.find(id);
        if(categoryBean.delete(id)){
            return Response.status(Response.Status.OK)
                    .entity(toDTO(category))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
    }

    private List<CategoryDTO> toDTOs(List<Category> categories){
        return categories.stream().map(this::toDTO).collect(Collectors.toList());
    }
    private CategoryDTO toDTO(Category category){
        return new CategoryDTO(
                category.getId(),
                category.getOwner().getEmail(),
                category.getName());
    }
}

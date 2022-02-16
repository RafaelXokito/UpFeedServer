package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.CategoryDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.CategoryBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Category;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("categories") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class CategoryService {

    @EJB
    private CategoryBean categoryBean;

    @GET
    @Path("/")
    public Response getAllCategoriesWS(){
        return Response.status(Response.Status.OK)
                .entity(toDTOs(categoryBean.getAllCategories()))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getCategoryDetails(@PathParam("id") long id) throws MyEntityNotFoundException {
        Category category = categoryBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(category))
                .build();
    }

    @POST
    @Path("/")
    public Response createCategory(CategoryDTO categoryDTO) throws MyEntityNotFoundException, MyIllegalArgumentException {
        long id = categoryBean.create(categoryDTO.getEmailOwner(), categoryDTO.getName());

        Category category = categoryBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(category))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") long id, CategoryDTO categoryDTO) throws MyEntityNotFoundException {
        categoryBean.update(id, categoryDTO.getName());
        Category category = categoryBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(category))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") long id) throws MyEntityNotFoundException {
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

package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.PostDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PostBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Note;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Person;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Post;
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

@Path("posts") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@RolesAllowed({"Student","Teacher"})
public class PostService {

    @EJB
    private PostBean postBean;

    @EJB
    private PersonBean personBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("/")
    public Response getAllPostsWS(@HeaderParam("Authorization") String auth) throws Exception{
        Person person = personBean.getPersonByAuthToken(auth);

        return Response.status(Response.Status.OK)
                .entity(toDTOs(postBean.getAllPostsByUser(person.getId())))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getPostDetails(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Post post = postBean.find(id);
        if (Objects.equals(post.getOwner().getId(), person.getId()))
            return Response.status(Response.Status.OK)
                    .entity(toDTO(post))
                    .build();

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This post dont belong to you")
                .build();
    }

    @POST
    @Path("/")
    public Response createPost(PostDTO postDTO) throws Exception {
        long id = postBean.create(postDTO.getEmailOwner(), postDTO.getTitle(), postDTO.getDescription(), postDTO.isType(), postDTO.getRoomId());
        Post post = postBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(post))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePost(@HeaderParam("Authorization") String auth, @PathParam("id") long id, PostDTO postDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Post checkPost = postBean.find(id);
        if (Objects.equals(checkPost.getOwner().getId(), person.getId())) {
            postBean.update(id, postDTO.getTitle(), postDTO.getDescription());
            Post post = postBean.find(id);
            return Response.status(Response.Status.OK)
                    .entity(toDTO(post))
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This post dont belong to you")
                .build();
    }


    @DELETE
    @Path("/{id}")
    public Response deletePost(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Post post = postBean.find(id);
        if (Objects.equals(post.getOwner().getId(), person.getId())) {
            if (postBean.delete(id)) {
                return Response.status(Response.Status.OK)
                        .entity(toDTO(post))
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This post dont belong to you")
                .build();
    }

    private List<PostDTO> toDTOs(List<Post> posts){
        return posts.stream().map(this::toDTO).collect(Collectors.toList());
    }
    private PostDTO toDTO(Post post){
        return new PostDTO(
                post.getId(),
                post.getOwner().getEmail(),
                post.getTitle(),
                post.getDescription(),
                post.getType(),
                post.getRoom().getId(),
                post.getDate()
        );
    }
}

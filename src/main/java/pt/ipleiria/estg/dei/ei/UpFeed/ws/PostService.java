package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.PostDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PostBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Post;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyUnauthorizedException;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("posts") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class PostService {

    @EJB
    private PostBean postBean;

    @GET
    @Path("/")
    public Response getAllPostsWS(){
        return Response.status(Response.Status.OK)
                .entity(toDTOs(postBean.getAllPosts()))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getPostDetails(@PathParam("id") long id) throws Exception {
        Post post = postBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(post))
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
    public Response updatePost(@PathParam("id") long id, PostDTO postDTO) throws Exception {
        postBean.update(id, postDTO.getTitle(), postDTO.getDescription());
        Post post = postBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(post))
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePost(@PathParam("id") long id) throws Exception {
        Post post = postBean.find(id);
        if(postBean.delete(id)){
            return Response.status(Response.Status.OK)
                    .entity(toDTO(post))
                    .build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
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

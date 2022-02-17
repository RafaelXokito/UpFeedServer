package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.NoteDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.NoteBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Note;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyUnauthorizedException;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("notes") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
public class NoteService {

    @EJB
    private NoteBean noteBean;

    @GET
    @Path("")
    public Response getAllNotesWS(){
        return Response.status(Response.Status.OK)
                .entity(toDTOs(noteBean.getAllNotes()))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getNoteDetails(@PathParam("id") long id) throws Exception {
        Note note = noteBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(note))
                .build();
    }

    @POST
    @Path("/")
    public Response createNote(NoteDTO noteDTO) throws Exception {
        long id = noteBean.create(noteDTO.getEmailOwner(), noteDTO.getTitle(), noteDTO.getDescription(), noteDTO.getStatus(), noteDTO.getCategoryId());

        Note note = noteBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(note))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateNote(@PathParam("id") long id, NoteDTO noteDTO) throws Exception {
        noteBean.update(id, noteDTO.getTitle(), noteDTO.getDescription());
        Note note = noteBean.find(id);
        return Response.status(Response.Status.OK)
                .entity(toDTO(note))
                .build();
    }


    @DELETE
    @Path("/{id}")
    public Response deleteNote(@PathParam("id") long id) throws Exception {
        Note note = noteBean.find(id);
        if(noteBean.delete(id)){
            return Response.status(Response.Status.OK)
                    .entity(toDTO(note))
                    .build();
        }else{

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .build();
        }
    }

    private List<NoteDTO> toDTOs(List<Note> notes){
        return notes.stream().map(this::toDTO).collect(Collectors.toList());
    }
    private NoteDTO toDTO(Note note){
        return new NoteDTO(
                note.getId(),
                note.getOwner().getEmail(),
                note.getTitle(),
                note.getDescription(),
                note.getStatus(),
                note.getCategory().getId()
                );
    }
}

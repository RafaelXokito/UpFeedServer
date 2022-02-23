package pt.ipleiria.estg.dei.ei.UpFeed.ws;

import pt.ipleiria.estg.dei.ei.UpFeed.dtos.NoteDTO;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.NoteBean;
import pt.ipleiria.estg.dei.ei.UpFeed.ejbs.PersonBean;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Note;
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

@Path("notes") // relative url web path for this service
@Produces({MediaType.APPLICATION_JSON}) // injects header “Content-Type: application/json”
@Consumes({MediaType.APPLICATION_JSON}) // injects header “Accept: application/json”
@RolesAllowed({"Student","Teacher"})
public class NoteService {

    @EJB
    private NoteBean noteBean;

    @EJB
    private PersonBean personBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Path("")
    public Response getAllNotesWS(@HeaderParam("Authorization") String auth) throws Exception{
        Person person = personBean.getPersonByAuthToken(auth);

        return Response.status(Response.Status.OK)
                .entity(toDTOs(noteBean.getAllNotesByUser(person.getId())))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getNoteDetails(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Note note = noteBean.find(id);
        if (Objects.equals(note.getOwner().getId(), person.getId()))
            return Response.status(Response.Status.OK)
                    .entity(toDTO(note))
                    .build();
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This note dont belong to you")
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
    public Response updateNote(@HeaderParam("Authorization") String auth, @PathParam("id") long id, NoteDTO noteDTO) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Note checkNote = noteBean.find(id);
        if (Objects.equals(checkNote.getOwner().getId(), person.getId())) {
            noteBean.update(id, noteDTO.getTitle(), noteDTO.getDescription());
            Note note = noteBean.find(id);
            return Response.status(Response.Status.OK)
                    .entity(toDTO(note))
                    .build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This note dont belong to you")
                .build();
    }


    @DELETE
    @Path("/{id}")
    public Response deleteNote(@HeaderParam("Authorization") String auth, @PathParam("id") long id) throws Exception {
        Person person = personBean.getPersonByAuthToken(auth);

        Note note = noteBean.find(id);
        if (Objects.equals(note.getOwner().getId(), person.getId())) {
            if (noteBean.delete(id)) {
                return Response.status(Response.Status.OK)
                        .entity(toDTO(note))
                        .build();
            } else {

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .build();
            }
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("This note dont belong to you")
                .build();
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

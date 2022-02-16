package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Category;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Note;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.User;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyUnauthorizedException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class NoteBean {

    @PersistenceContext
    private EntityManager entityManager;

    public long create(String emailOwner, String title, String description, boolean status, long categoryId) throws MyEntityNotFoundException, MyUnauthorizedException, MyIllegalArgumentException {

        if(emailOwner == null || emailOwner.equals("")){
            throw new MyIllegalArgumentException("Email is empty");
        }
        if(title == null || title.equals("")){
            throw new MyIllegalArgumentException("Title is empty");
        }

        Category category = entityManager.find(Category.class, categoryId);
        if(category == null){
            throw new MyEntityNotFoundException("There is no category with the id: " + categoryId);
        }
        User user = findUser(emailOwner);
        if(user == null){
            throw new MyEntityNotFoundException("There is no user with the email: " + emailOwner);
        }
        if(!category.getOwner().getId().equals(user.getId())){
            throw new MyUnauthorizedException("The category is invalid - current user does not own the category of this note");
        }
        Note note = new Note(title,user,description,status,category);
        entityManager.persist(note);
        entityManager.flush();
        return note.getId();
    }
    private User findUser(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = '" + email + "'", User.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    public List<Note> getAllNotes(){
        return (List<Note>) entityManager.createNamedQuery("getAllNotes").getResultList();
    }

    public Note find(long id) throws MyEntityNotFoundException {
        Note note = entityManager.find(Note.class, id);
        if (note == null){
            throw new MyEntityNotFoundException("There is no note with the id: " + id);
        }
    return note;
    }

    public void update(long id, String title,String description) throws MyEntityNotFoundException {
        Note note = find(id);
        entityManager.lock(note, LockModeType.PESSIMISTIC_READ);
        if(title != null && !title.equals("") && !title.equals(note.getTitle())){
            note.setTitle(title);
        }
        if(description != null && !description.equals("") && !description.equals(note.getDescription())){
            note.setDescription(description);
        }
    }

    public void toogleStatus(long id, boolean status) throws MyEntityNotFoundException {
        Note note = find(id);
        if(status != note.getStatus()){
            note.setStatus(status);
        }
    }
    public boolean delete(long id) throws MyEntityNotFoundException {
        Note note = find(id);
        entityManager.lock(note, LockModeType.PESSIMISTIC_READ);
        note.getOwner().removeNote(note);
        note.getCategory().removeNote(note);
        entityManager.remove(note);
        return entityManager.find(Note.class,id) == null;
    }
}
package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyUnauthorizedException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Stateless
public class PostBean {

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Creates a new Post
     * @param emailOwner
     * @param title
     * @param description
     * @param type
     * @param roomId
     * @return the id of the created post
     * @throws MyIllegalArgumentException
     * @throws MyEntityNotFoundException
     * @throws MyUnauthorizedException
     */
    public long create(String emailOwner, String title, String description, boolean type, long roomId) throws Exception {
        if(title == null || title.equals("")){
            throw new MyIllegalArgumentException("Title is empty");
        }
        if(description == null || description.equals("")){
            throw new MyIllegalArgumentException("Description is empty");
        }
        User user = findUser(emailOwner);
        if(user == null){
            throw new MyEntityNotFoundException("There is no user with the email: " + emailOwner);
        }
        Room room = entityManager.find(Room.class, roomId);
        if(room == null){
            throw new MyEntityNotFoundException("There is no room with the id: " + roomId);
        }

        if(room.getChannel().getType() == Channel.TeacherChannel && (!emailOwner.equals(room.getChannel().getOwner().getEmail()) || emailOwner.equals(findSubjectRoom(room.getId()).getTeacher().getEmail()))){
            throw new MyUnauthorizedException("User is not allowed to post in this room");
        }
        if(room.getChannel().getType() == Channel.StudentChannel){
            boolean studentBelongsToList = false;
            List<Student> students = room.getStudents();
            for (Student student:students) {
                if(student.getEmail().equals(emailOwner)){
                    studentBelongsToList = true;
                }
            }
            if(!studentBelongsToList){
                throw new MyUnauthorizedException("User does not belong to this room");
            }
        }
        Post post = new Post(user, room, title, description, type);
        entityManager.persist(post);
        entityManager.flush();
        return post.getId();
    }

    /**
     *
     * @param email
     * @return the user with this email or null if no user is found
     */
    private User findUser(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = '" + email + "'", User.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }
    /***
     * Find SubjectRoom by given @Id:id
     * @param id @Id to find SubjectRoom
     * @return founded SubjectRoom or Null if dont
     */
    public SubjectRoom findSubjectRoom(long id) throws Exception {
        SubjectRoom subjectRoom = entityManager.find(SubjectRoom.class, id);
        if (subjectRoom == null)
            throw new MyEntityNotFoundException("SubjectRoom \"" + id + "\" does not exist");
        return subjectRoom;
    }

    /**
     * Retrieves the Post with this id
     * @param id
     * @return the post found
     */
    public Post find(long id) throws Exception {
        Post post = entityManager.find(Post.class,id);
        if(post == null){
            throw new MyEntityNotFoundException("There is no Post with the id: " +id);
        }
        return post;
    }

    /**
     * Retrieves all posts
     * @return the list of posts in the posts table
     */
    public List<Post> getAllPosts(){
        return (List<Post>) entityManager.createNamedQuery("getAllPosts").getResultList();
    }

    /**
     * Retrieves all posts
     * @param id user id
     * @return the list of Posts in the Posts table
     */
    public List<Post> getAllPostsByUser(Long id){
        return (List<Post>) entityManager.createNamedQuery("getAllPostsByUser").setParameter("id", id).getResultList();
    }

    /**
     * Updates the title and description of the post
     * @param id
     * @param title
     * @param description
     * @throws MyEntityNotFoundException
     */
    public void update(long id, String title, String description) throws Exception {
        Post post = find(id);
        entityManager.lock(post, LockModeType.PESSIMISTIC_READ);
        if(title != null && !title.equals("") && !post.getTitle().equals(title)){
            post.setTitle(title);
        }
        if(description != null && !description.equals("") && !post.getDescription().equals(description)){
            post.setDescription(description);
        }
        post.setDate(new Date());
    }

    /**
     * Deletes the post
     * @param id
     * @return true if deleted, false otherwise
     * @throws MyEntityNotFoundException
     */
    public boolean delete(long id) throws Exception {
        Post post = find(id);
        entityManager.remove(post);
        post.getOwner().removePost(post);
        post.getRoom().removePost(post);
        return entityManager.find(Post.class,id) == null;
    }
}

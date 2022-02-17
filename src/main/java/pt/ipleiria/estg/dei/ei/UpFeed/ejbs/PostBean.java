package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Post;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Room;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.User;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

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
     *
     * @param emailOwner
     * @param title
     * @param description
     * @param type
     * @param roomId
     * @return the id of the created post
     * @throws MyIllegalArgumentException
     * @throws MyEntityNotFoundException
     */
    public long create(String emailOwner, String title, String description, boolean type, long roomId) throws MyIllegalArgumentException, MyEntityNotFoundException {
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
        //TODO - Check the type of channel and then if its a subject room - dont allow students to post on it

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

    /**
     * Retrieves the Post with this id
     * @param id
     * @return the post found
     */
    public Post find(long id) throws MyEntityNotFoundException {
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
     * Updates the title and description of the post
     * @param id
     * @param title
     * @param description
     * @throws MyEntityNotFoundException
     */
    public void update(long id, String title, String description) throws MyEntityNotFoundException {
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
    public boolean delete(long id) throws MyEntityNotFoundException {
        Post post = find(id);
        entityManager.remove(post);
        post.getOwner().removePost(post);
        //TODO when rooms get ready -> post.getRoom().removePost(post);
        return entityManager.find(Post.class,id) == null;
    }
}

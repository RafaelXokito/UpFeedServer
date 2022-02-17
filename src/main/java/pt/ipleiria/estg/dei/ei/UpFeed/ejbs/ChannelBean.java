package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Channel;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Channel;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class ChannelBean {
    @PersistenceContext
    private EntityManager entityManager;

    /***
     * Find Channel by given @Id:id
     * @param id @Id to find Channel
     * @return founded Channel or Null if dont
     */
    public Channel findChannel(long id) throws Exception {
        Channel channel = entityManager.find(Channel.class, id);
        if (channel == null)
            throw new MyEntityNotFoundException("Channel \"" + id + "\" does not exist");
        return channel;
    }

    /***
     * Find Channel Users by given @Id:id
     * @param id @Id to find Channel
     * @return founded Users from Channel or Null if dont
     */
    public List<User> findChannelUsers(long id) throws Exception {
        Channel channel = entityManager.find(Channel.class, id);
        if (channel == null)
            throw new MyEntityNotFoundException("Channel \"" + id + "\" does not exist");
        return channel.getUsers();
    }

    /***
     * Find Person by given @Unique:Email
     * @param email @Id to find Person
     * @return founded Person or Null if dont
     */
    public Person findPerson(String email) {
        TypedQuery<Person> query = entityManager.createQuery("SELECT p FROM Person p WHERE p.email = '" + email + "'", Person.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    /**
     * Find user based on given email
     * @param email email to find user
     * @return User object founded or null if dont
     */
    public User findUser(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT t FROM User t WHERE t.email = '" + email + "'", User.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    /***
     * Execute Channel query getAllChannels getting all Channels Class
     * @return a list of All Channels
     */
    public List<Channel> getAllChannels() {
        return entityManager.createNamedQuery("getAllChannels", Channel.class).setLockMode(LockModeType.OPTIMISTIC).getResultList();
    }

    /***
     * Creating a Channel by Authenticated User
     * @param ownerId @Id:id of channel owner, its @Id from authenticated User, we already know who they are
     * @param title of channel
     * @param description of channel
     * @param type of channel (Teacher or Student Channel)
     * @param weight , if its a Teacher room, they can give a weight (ECT's)
     * @return @Id:id from just created Channel
     * @throws Exception
     */
    public long create(Long ownerId, String title, String description, Boolean type, Integer weight) throws Exception {

        //REQUIRED VALIDATION
        if (ownerId == null || ownerId <= 0)
            throw new MyIllegalArgumentException("Field \"owner Email\" is required");
        if (title == null || title.trim().isEmpty())
            throw new MyIllegalArgumentException("Field \"title\" is required");
        if (type == null)
            throw new MyIllegalArgumentException("Field \"type\" is required");

        //CHECK VALUES
        User user = entityManager.find(User.class, ownerId);

        Channel newChannel = new Channel(title.trim(), user, description, type, weight);

        try {
            entityManager.persist(newChannel);
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error persisting your data");
        }

        //Owner will be added as a user
        newChannel.addUsers(user);
        user.addChannel(newChannel);
        entityManager.flush();

        return newChannel.getId();
    }

    /***
     * Creating a Channel by Authenticated User
     * @param title of channel
     * @param description of channel
     * @param weight , if its a Teacher room, they can give a weight (ECT's)
     * @return @Id:id from just created Channel
     * @throws Exception
     */
    public long update(Long id, String title, String description, Integer weight) throws Exception {
        Channel channel = findChannel(id);
        
        //REQUIRED VALIDATION
        if (title == null || title.trim().isEmpty())
            throw new MyIllegalArgumentException("Field \"title\" is required");

        entityManager.lock(channel, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        channel.setTitle(title.trim());
        channel.setDescription(description.trim());
        channel.setWeight(weight);

        try {
            entityManager.flush();
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error updating your data");
        }

        return channel.getId();
    }

    /***
     * Delete a Channel by given @Id:id
     * @param id @Id to find the proposal delete Channel
     */
    public boolean delete(long id) throws Exception{
        Channel channel = findChannel(id);

        for (Room room:channel.getRooms()) {
            for (Student student:room.getStudents()) {
                student.removeRoom(room);
            }
            //we dont need to delete rooms because the CascadeType = REMOVE
        }

        for (User user:channel.getUsers()) {
            user.removeChannel(channel);
        }

        //This is just a double check, when all users are removed from this channel
        //means that the owner is removed too
        channel.getOwner().removeChannel(channel);

        entityManager.remove(channel);
        entityManager.flush();

        return entityManager.find(Channel.class, id) == null;
    }

    /**
     * Add a User to a Channel
     * @param channelId to add the user
     * @param userEmail to be added to channel
     * @return true if user was added successfully to channel, false if not
     * @throws Exception
     */
    public boolean addUserToChannel(long channelId, String userEmail) throws Exception{
        Channel channel = findChannel(channelId);

        User user = findUser(userEmail);
        if (user == null)
            throw new MyEntityNotFoundException("User with this email \"" + userEmail + "\" does not exist");

        channel.addUsers(user);
        user.addChannel(channel);

        return channel.getUsers().contains(user);
    }

    /**
     * Remove a User from a Channel
     * @param channelId to remove the user
     * @param userEmail to be removed to channel
     * @return true if user was removed successfully to channel, false if not
     * @throws Exception
     */
    public boolean removeUserToChannel(long channelId, String userEmail) throws Exception{
        Channel channel = findChannel(channelId);

        User user = findUser(userEmail);
        if (user == null)
            throw new MyEntityNotFoundException("User with this email \"" + userEmail + "\" does not exist");

        channel.removeUsers(user);
        user.removeChannel(channel);

        return !channel.getUsers().contains(user);
    }


}

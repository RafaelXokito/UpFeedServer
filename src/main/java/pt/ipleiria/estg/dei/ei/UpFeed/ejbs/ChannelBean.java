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
     * Find Person by given @Unique:Email
     * @param email @Id to find Person
     * @return founded Person or Null if dont
     */
    public Person findPerson(String email) {
        TypedQuery<Person> query = entityManager.createQuery("SELECT p FROM Person p WHERE p.email = '" + email + "'", Person.class);
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
            entityManager.flush();
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error persisting your data");
        }

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

        entityManager.remove(channel);
        return entityManager.find(Channel.class, id) == null;
    }
}

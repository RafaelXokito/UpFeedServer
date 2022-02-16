package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class SubjectRoomBean {
    @PersistenceContext
    private EntityManager entityManager;

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

    /***
     * Execute SubjectRoom query getAllSubjectRooms getting all SubjectRooms Class
     * @return a list of All SubjectRooms
     */
    public List<SubjectRoom> getAllSubjectRooms() {
        return entityManager.createNamedQuery("getAllSubjectRooms", SubjectRoom.class).setLockMode(LockModeType.OPTIMISTIC).getResultList();
    }

    /**
     * Find teacher based on given email
     * @param email email to find teacher
     * @return Teacher object founded or null if dont
     */
    public Teacher findTeacher(String email) {
        TypedQuery<Teacher> query = entityManager.createQuery("SELECT t FROM Teacher t WHERE t.email = '" + email + "'", Teacher.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    /***
     * Creating a SubjectRoom
     * @param teacherEmail @Unique:email of subjectRoom owner
     * @param title of subjectRoom
     * @param description of subjectRoom
     * @param weight , if its a Teacher room, they can give a weight (ECT's)
     * @return @Id:id from just created SubjectRoom
     * @throws Exception
     */
    public long create(String teacherEmail, Long channelId, String title, String description, Integer weight) throws Exception {

        //REQUIRED VALIDATION
        if (teacherEmail == null || teacherEmail.trim().isEmpty())
            throw new MyIllegalArgumentException("Field \"teacher email\" is required");
        if (channelId == null || channelId <= 0)
            throw new MyIllegalArgumentException("Field \"channel\" is required");
        if (title == null || title.trim().isEmpty())
            throw new MyIllegalArgumentException("Field \"title\" is required");
        
        Teacher teacher = findTeacher(teacherEmail);

        Channel channel = entityManager.find(Channel.class, channelId);
        if (channel.getType() != Channel.TeacherChannel)
            throw new MyIllegalArgumentException("Invalid \"channel\", must be a teacher channel");
        if (!channel.getUsers().contains(teacher))
            throw new MyIllegalArgumentException("Invalid \"teacher\", this teacher");


        SubjectRoom newSubjectRoom = new SubjectRoom(title.trim(), description, channel, weight, teacher);
        try {
            entityManager.persist(newSubjectRoom);
            entityManager.flush();
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error persisting your data");
        }

        return newSubjectRoom.getId();
    }

    /***
     * Updating a SubjectRoom
     * @param title of subjectRoom
     * @param description of subjectRoom
     * @param weight , if its a Teacher room, they can give a weight (ECT's)
     * @return @Id:id from just updated SubjectRoom
     * @throws Exception
     */
    public long update(Long id, String title, String description, Integer weight) throws Exception {
        SubjectRoom subjectRoom = findSubjectRoom(id);

        //REQUIRED VALIDATION
        if (title == null || title.trim().isEmpty())
            throw new MyIllegalArgumentException("Field \"title\" is required");

        entityManager.lock(subjectRoom, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        subjectRoom.setTitle(title.trim());
        subjectRoom.setDescription(description.trim());
        subjectRoom.setWeight(weight);
        try {
            entityManager.flush();
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error updating your data");
        }

        return subjectRoom.getId();
    }

    /***
     * Updating teacher of SubjectRoom
     * @param id SubjectRoom to be update
     * @param teacherEmail teacher email to update
     * @return @Id:id from just updated SubjectRoom
     * @throws Exception
     */
    public long updateTeacher(Long id, String teacherEmail) throws Exception {
        SubjectRoom subjectRoom = findSubjectRoom(id);

        //REQUIRED VALIDATION
        Teacher teacher = findTeacher(teacherEmail);

        entityManager.lock(subjectRoom, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        subjectRoom.setTeacher(teacher);
        try {
            entityManager.flush();
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error updating your data");
        }

        return subjectRoom.getId();
    }


    /***
     * Updating teacher of SubjectRoom
     * @param id SubjectRoom to be update
     * @param teacherId teacher id to update
     * @return @Id:id from just updated SubjectRoom
     * @throws Exception
     */
    public long updateTeacher(Long id, Long teacherId) throws Exception {
        SubjectRoom subjectRoom = findSubjectRoom(id);

        Teacher teacher = entityManager.find(Teacher.class, teacherId);

        entityManager.lock(subjectRoom, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        subjectRoom.setTeacher(teacher);
        try {
            entityManager.flush();
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error updating your data");
        }

        return subjectRoom.getId();
    }

    /***
     * Delete a SubjectRoom by given @Id:id
     * @param id @Id to find the proposal delete SubjectRoom
     */
    public boolean delete(long id) throws Exception{
        SubjectRoom subjectRoom = findSubjectRoom(id);

        subjectRoom.getTeacher().removeSubjectRoom(subjectRoom);
        entityManager.remove(subjectRoom);

        entityManager.flush();
        return entityManager.find(SubjectRoom.class, id) == null;
    }

}

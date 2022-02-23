package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Channel;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Student;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.StudyRoom;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Teacher;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class StudyRoomBean {
    @PersistenceContext
    private EntityManager entityManager;

    /***
     * Find StudyRoom by given @Id:id
     * @param id @Id to find StudyRoom
     * @return founded StudyRoom or Null if dont
     */
    public StudyRoom findStudyRoom(long id) throws Exception {
        StudyRoom studyRoom = entityManager.find(StudyRoom.class, id);
        if (studyRoom == null)
            throw new MyEntityNotFoundException("StudyRoom \"" + id + "\" does not exist");
        return studyRoom;
    }

    /***
     * Execute StudyRoom query getAllStudyRooms getting all StudyRooms Class
     * @return a list of All StudyRooms
     */
    public List<StudyRoom> getAllStudyRooms() {
        return entityManager.createNamedQuery("getAllStudyRooms", StudyRoom.class).setLockMode(LockModeType.OPTIMISTIC).getResultList();
    }

    /***
     * Execute StudyRoom query getAllStudyRoomsByStudent getting all StudyRooms Class
     * @return a list of All StudyRooms
     */
    public List<StudyRoom> getAllStudyRoomsByStudent(Long id) {
        return entityManager.createNamedQuery("getAllStudyRoomsByStudent", StudyRoom.class).setLockMode(LockModeType.OPTIMISTIC).getResultList();
    }

    /**
     * Find student based on given email
     * @param email email to find student
     * @return Student object founded or null if dont
     */
    public Student findStudent(String email) {
        TypedQuery<Student> query = entityManager.createQuery("SELECT s FROM Student s WHERE s.email = '" + email + "'", Student.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    /***
     * Creating a StudyRoom
     * @param channelId that studyRoom belongs
     * @param title of studyRoom
     * @param description of studyRoom
     * @return @Id:id from just created StudyRoom
     * @throws Exception
     */
    public long create(Long channelId, String title, String description) throws Exception {

        //REQUIRED VALIDATION
        if (channelId == null || channelId <= 0)
            throw new MyIllegalArgumentException("Field \"channel\" is required");
        if (title == null || title.trim().isEmpty())
            throw new MyIllegalArgumentException("Field \"title\" is required");
        
        Channel channel = entityManager.find(Channel.class, channelId);
        /*
        Uma Teacher Channel pode conter StudyRooms, why not?
        if (channel.getType() != Channel.StudentChannel)
            throw new MyIllegalArgumentException("Invalid \"channel\", must be a student channel");
         */

        StudyRoom newStudyRoom = new StudyRoom(title.trim(), description, channel);
        try {
            entityManager.persist(newStudyRoom);
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error persisting your data");
        }


        //Owner will be added as a user
        newStudyRoom.addStudents((Student) channel.getOwner());
        ((Student) channel.getOwner()).addRoom(newStudyRoom);

        channel.addRooms(newStudyRoom);
        entityManager.flush();
        
        return newStudyRoom.getId();
    }

    /***
     * Updating a StudyRoom
     * @param id to find the studyRoom to be updated
     * @param title of studyRoom
     * @param description of studyRoom
     * @return @Id:id from just updated StudyRoom
     * @throws Exception
     */
    public long update(Long id, String title, String description) throws Exception {
        StudyRoom studyRoom = findStudyRoom(id);

        //REQUIRED VALIDATION
        if (title == null || title.trim().isEmpty())
            throw new MyIllegalArgumentException("Field \"title\" is required");

        entityManager.lock(studyRoom, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        studyRoom.setTitle(title.trim());
        studyRoom.setDescription(description.trim());
        try {
            entityManager.flush();
        }catch (Exception ex){
            throw new MyIllegalArgumentException("Error updating your data");
        }

        return studyRoom.getId();
    }

    /***
     * Delete a StudyRoom by given @Id:id
     * @param id @Id to find the proposal delete StudyRoom
     */
    public boolean delete(long id) throws Exception{
        StudyRoom studyRoom = findStudyRoom(id);

        for (Student student :
                studyRoom.getStudents()) {
            student.removeRoom(studyRoom);
        }
        studyRoom.getChannel().removeRooms(studyRoom);
        entityManager.remove(studyRoom);

        entityManager.flush();
        return entityManager.find(StudyRoom.class, id) == null;
    }


    /**
     * Add a Student to a StudyRoom
     * @param studyRoomId to add the student
     * @param studentEmail to be added to studyRoom
     * @return true if student was added successfully to studyRoom, false if not
     * @throws Exception
     */
    public boolean addStudentToStudyRoom(long studyRoomId, String studentEmail) throws Exception{
        StudyRoom studyRoom = findStudyRoom(studyRoomId);

        Student student = findStudent(studentEmail);
        if (student == null)
            throw new MyEntityNotFoundException("Student with this email \"" + studentEmail + "\" does not exist");
        if (!studyRoom.getChannel().getUsers().contains(student))
            throw new MyIllegalArgumentException("Invalid \"student\", this student dont belongs to the parent channel");

        studyRoom.addStudents(student);
        student.addRoom(studyRoom);

        entityManager.flush();

        return studyRoom.getStudents().contains(student);
    }

    /**
     * Remove a Student from a StudyRoom
     * @param studyRoomId to remove the student
     * @param studentEmail to be removed to studyRoom
     * @return true if student was removed successfully to studyRoom, false if not
     * @throws Exception
     */
    public boolean removeStudentToStudyRoom(long studyRoomId, String studentEmail) throws Exception{
        StudyRoom studyRoom = findStudyRoom(studyRoomId);

        Student student = findStudent(studentEmail);
        if (student == null)
            throw new MyEntityNotFoundException("Student with this email \"" + studentEmail + "\" does not exist");
        if (!studyRoom.getChannel().getUsers().contains(student))
            throw new MyIllegalArgumentException("Invalid \"student\", this student dont belongs to the parent channel");

        studyRoom.removeStudents(student);
        student.removeRoom(studyRoom);

        entityManager.flush();

        return !studyRoom.getStudents().contains(student);
    }

}

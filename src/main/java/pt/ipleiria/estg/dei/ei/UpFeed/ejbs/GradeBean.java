package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Channel;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Grade;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Student;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.SubjectRoom;
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
public class GradeBean {
    @PersistenceContext
    private EntityManager entityManager;

    /***
     * Execute Grade query getAllGradesClassByTeacher getting all Grade Class by Teacher id
     * @param id Teacher id
     * @return a list of All Grades
     */
    public List<Grade> getAllGradesClassByTeacher(Long id) {
        return entityManager.createNamedQuery("getAllGradesClassByTeacher", Grade.class).setParameter("id", id).setLockMode(LockModeType.OPTIMISTIC).getResultList();
    }

    /***
     * Execute Grade query getAllGradesClassByStudent getting all Grade Class by Student id
     * @param id Student id
     * @return a list of All Grades
     */
    public List<Grade> getAllGradesClassByStudent(Long id) {
        return entityManager.createNamedQuery("getAllGradesClassByStudent", Grade.class).setParameter("id", id).setLockMode(LockModeType.OPTIMISTIC).getResultList();
    }

    /**
     * Creates a new Grade
     * @param value
     * @param studentEmail
     * @param subjectRoomId
     * @param observations
     * @return the id of the created grade
     * @throws Exception
     */
    public long create(float value, String studentEmail, long subjectRoomId, String observations) throws Exception{
        if(value < 0 || value > 20){
            throw new MyIllegalArgumentException("The value is invalid - value ranges from 0 to 20");
        }
        Student student = findStudent(studentEmail);
        if(student == null){
            throw new MyEntityNotFoundException("There is no student with the email: " + studentEmail);
        }
        SubjectRoom subjectRoom = entityManager.find(SubjectRoom.class, subjectRoomId);
        if(subjectRoom == null){
            throw new MyEntityNotFoundException("There is no subject room with the id: " + subjectRoomId);
        }
        if(!subjectRoom.getStudents().contains(student)){
            throw new MyUnauthorizedException("The student with the email: " +studentEmail +" is not registered in the subject room identified by the id: " + subjectRoomId);
        }

        Grade grade = new Grade(value,student,subjectRoom,observations);
        entityManager.persist(grade);
        entityManager.flush();
        return grade.getId();
    }
    /**
     *
     * @param email
     * @return the student with this email or null if no user is found
     */
    private Student findStudent(String email) {
        TypedQuery<Student> query = entityManager.createQuery("SELECT s FROM Student s WHERE s.email = '" + email + "'", Student.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    /**
     * Finds the grade with the id
     * @param id
     * @return the grade that was found or null
     * @throws Exception
     */
    public Grade find(long id) throws Exception{
        Grade grade = entityManager.find(Grade.class, id);
        if(grade == null){
            throw new MyEntityNotFoundException("There is no grade with the id: " +id);
        }
        return grade;
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

    public List<Grade> getAllGrades(){
        return (List<Grade>) entityManager.createNamedQuery("getAllGrades").getResultList();
    }

    /**
     * Updates the grade's value and observations
     * @param id
     * @param value
     * @param observations
     * @throws Exception
     */
    public void update(long id, Float value, String observations) throws Exception {
        Grade grade = find(id);
        if(value != null && value >= 0 && value <= 20 && !grade.getValue().equals(value)){
            grade.setValue(value);
        }
        if(observations!=null && !observations.equals("") && !grade.getObservations().equals(observations)){
            grade.setObservations(observations);
        }
    }

    /**
     * Removes the grade with the id
     * @param id
     * @return true if deleted, false otherwise
     * @throws Exception
     */
    public boolean delete(long id) throws Exception {
        Grade grade = find(id);
        entityManager.remove(grade);
        return entityManager.find(Grade.class,id) == null;
    }

}

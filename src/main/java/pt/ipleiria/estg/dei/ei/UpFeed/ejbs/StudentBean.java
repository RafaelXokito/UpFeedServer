package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Student;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import java.util.List;

@Stateless
public class StudentBean {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates a new Student
     * @param name
     * @param email
     * @param password
     * @return the id of the created student
     * @throws MyConstraintViolationException
     * @throws MyIllegalArgumentException
     */
    public long create(String name, String email, String password) throws MyConstraintViolationException, MyIllegalArgumentException {
        if(name == null || name.equals("")){
            throw new MyIllegalArgumentException("Name is invalid");
        }
        if(email == null || email.equals("")){
            throw new IllegalArgumentException("Email is invalid");
        }
        if(password == null || password.equals("")){
            throw new IllegalArgumentException("Password is invalid");
        }
        Student student;
       try{
           student = new Student(name,email,password);
           entityManager.persist(student);
           entityManager.flush();
        }catch (ConstraintViolationException e){
            throw new MyConstraintViolationException(e);
        }
       return student.getId();
    }

    /**
     * Finds the student with this id
     * @param id
     * @return the student found
     * @throws MyEntityNotFoundException
     */
    public Student findStudent(long id) throws MyEntityNotFoundException {
        Student student = entityManager.find(Student.class, id);
        if (student == null) throw new MyEntityNotFoundException("There is no Student with the id: " + id);
        return student;
    }

    /**
     * Retrieves all the Students from the table
     * @return the list of students in the students table
     */
    public List<Student> getAllStudents() {
        return (List<Student>) entityManager.createNamedQuery("getAllStudents").getResultList();
    }

    /**
     * Updates the student's name and email
     * @param id
     * @param name
     * @param email
     * @throws MyEntityNotFoundException
     */
    public void update(long id, String name, String email) throws MyEntityNotFoundException {
        Student student = findStudent(id);
        entityManager.lock(student, LockModeType.PESSIMISTIC_READ);
        if(name != null && !name.equals("") && !student.getName().equals(name)){
            student.setName(name);
        }
        if(email != null && !email.equals("") && !student.getEmail().equals(email)){
            student.setEmail(email);
        }
    }

    /**
     * Deletes the student with this id
     * @param id
     * @return true if deleted, false otherwise
     * @throws MyEntityNotFoundException
     */
    public boolean delete(long id) throws MyEntityNotFoundException {
        Student student = findStudent(id);
        entityManager.lock(student, LockModeType.PESSIMISTIC_READ);
        entityManager.remove(student);
        return entityManager.find(Student.class,id) == null;
    }

}

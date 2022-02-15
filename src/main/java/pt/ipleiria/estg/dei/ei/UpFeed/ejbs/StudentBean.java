package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Student;

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

    public void create(String name, String email, String password){
        if(name == null || name.equals("")){
            throw new IllegalArgumentException("Name is invalid");
        }
        if(email == null || email.equals("")){
            throw new IllegalArgumentException("Email is invalid");
        }
        if(password == null || password.equals("")){
            throw new IllegalArgumentException("Password is invalid");
        }
       try{
            Student student = new Student(name,email,password);
           entityManager.persist(student);
           entityManager.flush();
        }catch (ConstraintViolationException e){
            throw new IllegalArgumentException();//MyConstraintViolationException(e);
        }
    }

    public Student findStudent(long id) {
        Student student = entityManager.find(Student.class, id);
        if (student == null) throw new IllegalArgumentException("There is no Student with the id: " + id);
        return student;
    }

    public List<Student> getAllStudents() {
        return (List<Student>) entityManager.createNamedQuery("getAllStudents").getResultList();
    }

    public void update(long id, String name, String email){
        Student student = findStudent(id);
        entityManager.lock(student, LockModeType.PESSIMISTIC_READ);
        if(name != null && !name.equals("") && !student.getName().equals(name)){
            student.setName(name);
        }
        if(email != null && !email.equals("") && !student.getEmail().equals(email)){
            student.setEmail(email);
        }
        entityManager.merge(student);
    }

    public void delete(long id){
        Student student = findStudent(id);
        entityManager.lock(student, LockModeType.PESSIMISTIC_READ);
        entityManager.remove(student);
    }

}

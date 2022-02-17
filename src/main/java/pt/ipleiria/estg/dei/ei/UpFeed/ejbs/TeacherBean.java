package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Teacher;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Teacher;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

@Stateless
public class TeacherBean {
    @PersistenceContext
    private EntityManager entityManager;

    /***
     * Find Teacher by given @Id:id
     * @param id @Id to find Teacher
     * @return founded Teacher or Null if dont
     */
    public Teacher findTeacher(long id) throws Exception {
        Teacher teacher = entityManager.find(Teacher.class, id);
        if (teacher == null)
            throw new MyEntityNotFoundException("Teacher \"" + id + "\" does not exist");
        return teacher;
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
     * Execute Teacher query getAllTeachers getting all Teachers Class
     * @return a list of All Teachers
     */
    public List<Teacher> getAllTeachers() {
        return entityManager.createNamedQuery("getAllTeachers", Teacher.class).setLockMode(LockModeType.OPTIMISTIC).getResultList();
    }

    /***
     * Creating a Teacher Account
     * @param email of teacher acc
     * @param password of teacher acc
     * @param name of teacher acc
     */
    public long create(String email, String password, String name) throws Exception {

        //REQUIRED VALIDATION
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Field \"email\" is required");
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("Field \"password\" is required");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Field \"name\" is required");

        //CHECK VALUES
        Person person = findPerson(email);
        if (person != null)
            throw new IllegalArgumentException("Person with email of \"" + email + "\" already exist");
        if (password.trim().length() < 4)
            throw new IllegalArgumentException("Field \"password\" must have at least 4 characters");
        if (name.trim().length() < 6)
            throw new IllegalArgumentException("Field \"name\" must have at least 6 characters");

        Teacher newTeacher = new Teacher(name.trim(), email.trim(), password.trim());
        try {
            entityManager.persist(newTeacher);
            entityManager.flush();
        }catch (Exception ex){
            throw new IllegalArgumentException("Error persisting your data");
        }

        return newTeacher.getId();
    }


    /***
     * Delete a Teacher by given @Id:id
     * @param id @Id to find the proposal delete Teacher
     */
    public boolean delete(long id) throws Exception{
        Teacher teacher = findTeacher(id);

        entityManager.remove(teacher);
        //TODO - WHEN SOFTDELETED DONE WE NEED TO SOLVE THE REALTIONS

        return entityManager.find(Teacher.class, id) == null;
    }

    /***
     * Update a Teacher by given @Id:username
     * @param email @Id to find the proposal update Teacher
     * @param name to update Teacher
     */
    public void update(long id, String email, String name) throws Exception {
        Teacher teacher = findTeacher(id);

        //REQUIRED VALIDATION
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Field \"email\" is required");
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Field \"name\" is required");

        //CHECK VALUES
        Person person = findPerson(email);
        if (person != null && person.getId() != id)
            throw new IllegalArgumentException("Person with email of \"" + email + "\" already exist");
        if (name.trim().length() < 6)
            throw new IllegalArgumentException("Field \"name\" must have at least 6 characters");

        entityManager.lock(teacher, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
        teacher.setEmail(email.trim());
        teacher.setName(name.trim());

        try {
            entityManager.flush();
        }catch (Exception ex){
            throw new IllegalArgumentException("Error updating Teacher");
        }
    }
}

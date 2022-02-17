package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Category;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Note;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.User;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class CategoryBean {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Creates a new Category
     * @param emailOwner
     * @param name
     * @return the id of the created Category
     * @throws MyIllegalArgumentException
     * @throws MyEntityNotFoundException
     */
    public long create(String emailOwner, String name) throws MyIllegalArgumentException, MyEntityNotFoundException {
        if(emailOwner == null || emailOwner.equals("")){
            throw new MyIllegalArgumentException("Email is invalid");
        }
        if(name == null || name.equals("")){
            throw new MyIllegalArgumentException("Name is invalid");
        }
        User user = findUser(emailOwner);
        if(user == null){
            throw new MyEntityNotFoundException("There is no User with the email: "+emailOwner);
        }
        Category category = new Category(name,user);
        entityManager.persist(category);
        entityManager.flush();
        return category.getId();
    }

    /**
     * Finds the user with this id
     * @param email
     * @return the user with this email or null if no user is found
     */
    private User findUser(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = '" + email + "'", User.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    /**
     * Retrieves the Category with this id
     * @param id
     * @return the category with the id
     * @throws MyEntityNotFoundException
     */
    public Category find(long id) throws MyEntityNotFoundException {
        Category category = entityManager.find(Category.class,id);
        if (category == null) {
             throw new MyEntityNotFoundException("There is no Category with the id: " + id);
        }
        return category;
    }

    /**
     * Retrieves all Categories
     * @return the list of the categories in the categories table
     */
    public List<Category> getAllCategories(){
        return (List<Category>) entityManager.createNamedQuery("getAllCategories").getResultList();
    }

    /**
     * Updates the Category's name
     * @param id
     * @param name
     * @throws MyEntityNotFoundException
     */
    public void update(long id, String name) throws MyEntityNotFoundException {
        Category category = find(id);
        entityManager.lock(category, LockModeType.PESSIMISTIC_READ);
        if(name != null && !name.equals("") && !category.getName().equals(name)){
            category.setName(name);
        }
    }

    /**
     * Deletes the category
     * @param id
     * @return true if deleted, false otherwise
     * @throws MyEntityNotFoundException
     */
    public boolean delete(long id) throws MyEntityNotFoundException {
        Category category = find(id);
        entityManager.lock(category, LockModeType.PESSIMISTIC_READ);
        entityManager.remove(category);//remove the categories from the notes
        return entityManager.find(Category.class,id) == null;
    }
}

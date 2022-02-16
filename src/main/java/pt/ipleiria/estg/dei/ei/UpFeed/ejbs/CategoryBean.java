package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Category;
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
    EntityManager entityManager;

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

    public User findUser(String email) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = '" + email + "'", User.class);
        query.setLockMode(LockModeType.OPTIMISTIC);
        return query.getResultList().size() > 0 ? query.getSingleResult() : null;
    }

    public Category find(long id) throws MyEntityNotFoundException {
        Category category = entityManager.find(Category.class,id);
        if (category == null) {
             throw new MyEntityNotFoundException("There is no Category with the id: " + id);
        }
        return category;
    }

    public List<Category> getAllCategories(){
        return (List<Category>) entityManager.createNamedQuery("getAllCategories").getResultList();
    }

    public void update(long id, String name) throws MyEntityNotFoundException {
        Category category = find(id);
        entityManager.lock(category, LockModeType.PESSIMISTIC_READ);
        if(name != null && !name.equals("") && !category.getName().equals(name)){
            category.setName(name);
        }
        entityManager.merge(category);
    }

    public boolean delete(long id) throws MyEntityNotFoundException {
        Category category = find(id);
        entityManager.lock(category, LockModeType.PESSIMISTIC_READ);
        entityManager.remove(category);
        return entityManager.find(Category.class,id) == null;
    }
}

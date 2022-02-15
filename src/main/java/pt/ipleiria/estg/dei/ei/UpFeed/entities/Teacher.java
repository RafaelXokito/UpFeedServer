package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "getAllTeachers",
                query = "SELECT tea FROM Teacher tea ORDER BY tea.name"
        )
})

//TODO Relations and Inheritance
@Table(name = "TEACHERS")
@Entity
public class Teacher extends User implements Serializable {

    public Teacher() {
        super();
    }
    public Teacher(Long id, String name, String email, String password) {
        super(id,name,email,password);
    }
}

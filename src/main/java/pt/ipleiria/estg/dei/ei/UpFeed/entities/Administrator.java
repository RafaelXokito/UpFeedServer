package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "getAllAdministrators",
                query = "SELECT admin FROM Administrator admin ORDER BY admin.name"
        )
})
@Table(name = "ADMINISTRATORS")
@Entity
public class Administrator extends Person implements Serializable {

    public Administrator() {
        super();
    }
    public Administrator(String name, String email, String password) {
        super(name,email,password);
    }
}

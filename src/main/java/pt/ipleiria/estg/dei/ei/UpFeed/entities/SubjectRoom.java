package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Table(name = "SUBJECTROOMS")
@Entity
public class SubjectRoom extends Room implements Serializable {
    public SubjectRoom() {
    }

    public SubjectRoom(String title, String description, List<Student> students, Channel channel) {
        super(title, description, students, channel);
    }
}

package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Table(name = "STUDYROOMS")
@Entity
public class StudyRoom extends Room implements Serializable {
    public StudyRoom() {
    }

    public StudyRoom(String title, String description, List<Student> students, Channel channel) {
        super(title, description, students, channel);
    }
}

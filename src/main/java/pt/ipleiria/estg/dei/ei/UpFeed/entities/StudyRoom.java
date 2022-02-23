package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = "getAllStudyRooms",
                query = "SELECT room FROM StudyRoom room ORDER BY room.title"
        ),
        @NamedQuery(
                name = "getAllStudyRoomsByStudent",
                query = "SELECT r FROM StudyRoom r JOIN r.students s WHERE s.id = :id ORDER BY r.title"
        )
})
@Table(name = "STUDYROOMS")
@Entity
public class StudyRoom extends Room implements Serializable {
    public StudyRoom() {
    }

    public StudyRoom(String title, String description, Channel channel) {
        super(title, description, new ArrayList<>(), channel);
    }

    public StudyRoom(String title, String description, List<Student> students, Channel channel) {
        super(title, description, students, channel);
    }
}

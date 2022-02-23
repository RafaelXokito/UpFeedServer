package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@NamedQueries({
        @NamedQuery(
                name = "getAllSubjectRooms",
                query = "SELECT room FROM SubjectRoom room ORDER BY room.title"
        ),
        @NamedQuery(
                name = "getAllSubjectRoomsByUser",
                query = "SELECT r FROM SubjectRoom r JOIN r.students s WHERE s.id = :id OR r.teacher.id = :id OR r.channel.owner.id = :id ORDER BY r.title"
        )
})
@Table(name = "SUBJECTROOMS")
@Entity
public class SubjectRoom extends Room implements Serializable {
    private Integer weight;
    @ManyToOne
    private Teacher teacher;

    public SubjectRoom() {
    }

    public SubjectRoom(String title, String description, List<Student> students, Channel channel, Integer weight, Teacher teacher) {
        super(title, description, students, channel);
        this.weight = weight;
        this.teacher = teacher;
    }

    public SubjectRoom(String title, String description, Channel channel, Integer weight, Teacher teacher) {
        super(title, description, new ArrayList<>(), channel);
        this.weight = weight;
        this.teacher = teacher;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}

package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@NamedQueries({
        @NamedQuery(
                name = "getAllGrades",
                query = "SELECT g FROM Grade g ORDER BY g.id"
        ),
        @NamedQuery(
                name = "getAllGradesClassByTeacher",
                query = "SELECT g FROM Grade g WHERE g.subjectRoom.id IN (SELECT s FROM SubjectRoom s WHERE s.teacher = :id ) OR g.subjectRoom.channel.owner.id = :id ORDER BY g.id"
        ),
        @NamedQuery(
                name = "getAllGradesClassByStudent",
                query = "SELECT g FROM Grade g WHERE g.student.id = :id ORDER BY g.id"
        ),
})

@Table(name = "GRADES")
@Entity
public class Grade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Float value;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "subjectRoomId")
    private SubjectRoom subjectRoom;
    @NotNull
    private String observations;

    public Grade() {
    }
    public Grade(Float value, Student student, SubjectRoom subjectRoom, String observations) {
        this.value = value;
        this.student = student;
        this.subjectRoom = subjectRoom;
        this.observations = observations;
    }

    public Grade(Long id, Float value, Student student, SubjectRoom subjectRoom,String observations) {
        this(value, student, subjectRoom,observations);
        this.id = id;
    }

    public SubjectRoom getSubjectRoom() {
        return subjectRoom;
    }

    public void setSubjectRoom(SubjectRoom subjectRoom) {
        this.subjectRoom = subjectRoom;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getValue() {
        return this.value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}

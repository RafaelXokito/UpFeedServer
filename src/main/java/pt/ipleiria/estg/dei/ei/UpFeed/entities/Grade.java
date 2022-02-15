package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
    //private SubjectRoom subjectRoom;
    @NotNull
    private String observations;

    public Grade() {
    }
    public Grade(Float value, Student student, String observations) {
        this.value = value;
        this.student = student;
        this.observations = observations;
    }

    public Grade(Long id, Float value, Student student, String observations) {
        this(value, student, observations);
        this.id = id;
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

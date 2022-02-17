package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.REMOVE)
    private List<SubjectRoom> subjectRooms;

    public Teacher() {
        super();
        this.subjectRooms = new ArrayList<>();
    }
    public Teacher(String name, String email, String password) {
        super(name,email,password);
        this.subjectRooms = new ArrayList<>();
    }

    public void addSubjectRoom(SubjectRoom subjectRoom){
        if(subjectRoom == null || subjectRooms.contains(subjectRoom)){
            return;
        }
        subjectRooms.add(subjectRoom);
    }

    public void removeSubjectRoom(SubjectRoom subjectRoom){
        if(subjectRoom == null || !subjectRooms.contains(subjectRoom)){
            return;
        }
        subjectRooms.remove(subjectRoom);
    }

    public List<SubjectRoom> getSubjectRooms() {
        return subjectRooms;
    }

    public void setSubjectRooms(List<SubjectRoom> subjectRooms) {
        this.subjectRooms = subjectRooms;
    }
}

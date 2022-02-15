package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = "getAllStudents",
                query = "SELECT stu FROM Student stu ORDER BY stu.name"
        )
})

//TODO Relations and Inheritance
@Table(name = "STUDENTS")
@Entity
public class Student extends User implements Serializable {

    @OneToMany(mappedBy = "student", cascade = CascadeType.REMOVE)
    private List<Grade> grades;

    @ManyToMany
    @JoinTable(name = "students_rooms",
            joinColumns = @JoinColumn(name = "studentId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roomId", referencedColumnName =
                    "id"))
    private List<Room> rooms;

    public Student() {
        super();
        this.grades = new LinkedList<>();
        this.rooms = new LinkedList<>();
    }
    public Student(Long id, String name, String email, String password) {
        super(id,name,email,password);
        this.grades = new LinkedList<>();
        this.rooms = new LinkedList<>();
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public void addGrade(Grade grade){
        if(grade == null || grades.contains(grade)){
            return;
        }
        grades.add(grade);
    }

    public void removeGrade(Grade grade){
        if(grade == null || !grades.contains(grade)){
            return;
        }
        grades.remove(grade);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void addRoom(Room room){
        if(room == null || rooms.contains(room)){
            return;
        }
        rooms.add(room);
    }

    public void removeRoom(Room room){
        if(room == null || !rooms.contains(room)){
            return;
        }
        rooms.remove(room);
    }
}

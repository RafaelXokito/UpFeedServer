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


    public Student() {
        super();
        this.grades = new LinkedList<>();
    }
    public Student(String name, String email, String password) {
        super(name,email,password);
        this.grades = new LinkedList<>();
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
}

package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Student;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.List;

@Startup
@Singleton
public class ConfigBean {

    @EJB
    StudentBean studentBean;

    @PostConstruct
    public void populateDB() {
        System.out.println("Starting Point!");

        studentBean.create("Carla Mendes", "1112@my.ipleiria.pt", "1234");
        studentBean.create("Rafael Pereira", "1113@my.ipleiria.pt", "1234");
        studentBean.create("Bruna Leitão", "1114@my.ipleiria.pt", "1234");
        studentBean.create("Carlos Costa", "1115@my.ipleiria.pt", "1234");
        studentBean.create("Fábio Gaspar", "1116@my.ipleiria.pt", "1234");
        studentBean.create("Daniel Carreira", "1117@my.ipleiria.pt", "1234");


        Student student = studentBean.findStudent(2);
        System.out.println("Find - "+ student.getName());
        List<Student> studentsList = studentBean.getAllStudents();
        for (Student s:studentsList){
            System.out.println(s.getName());
        }

        studentBean.update(1, "Carla Sofia Crespo Mendes", "1111@my.ipleiria.pt");
        studentBean.update(4, "Carlos Pereira Martinho da Costa", "");


        studentBean.delete(6);
    }


}

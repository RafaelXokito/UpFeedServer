package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.Category;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Note;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Student;
import pt.ipleiria.estg.dei.ei.UpFeed.entities.Administrator;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.UpFeed.exceptions.MyIllegalArgumentException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class ConfigBean {

    @EJB
    StudentBean studentBean;

    @EJB
    AdministratorBean administratorBean;

    @EJB
    TeacherBean teacherBean;

    @EJB
    ChannelBean channelBean;

    @EJB
    CategoryBean categoryBean;

    @EJB
    NoteBean noteBean;

    // Pay attention to the correct import: import java.util.logging.Logger;
    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");

    @PostConstruct
    public void populateDB() throws MyEntityNotFoundException, MyIllegalArgumentException, MyConstraintViolationException {
        System.out.println("Starting Point!");
        try {
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
            studentBean.delete(1);

            System.out.println("# Categories");
            long id1 = categoryBean.create("1113@my.ipleiria.pt", "DAE");
            long id2 = categoryBean.create("1116@my.ipleiria.pt", "SAD");
            long id3 = categoryBean.create("1117@my.ipleiria.pt", "DAD");
            long id4 = categoryBean.create("1114@my.ipleiria.pt", "TAES");

            Category category = categoryBean.find(id1);
            System.out.println("Find - "+ id1 + " | " + category.getName());

            List<Category> categories = categoryBean.getAllCategories();
            for (Category c:categories){
                System.out.println(c.getName() + " | " + c.getOwner().getName());
            }
            categoryBean.update(id4, "Advanced Topics of Software Enginnering");
            categoryBean.delete(id3);

            System.out.println("# Notes");
            long noteBruna = noteBean.create("1114@my.ipleiria.pt","TAES Project", "Do US12",false,4);
            long noteFabio = noteBean.create("1116@my.ipleiria.pt","SAD Practice exam study", "Do the Transform phase",true, 2);
            long noteRafael = noteBean.create("1113@my.ipleiria.pt", "DAE Theoric class study", "Chapters 3 and 4",false, 1);
            Note noteFind = noteBean.find(noteFabio);
            System.out.println("Find - "+ noteFabio + " | " + noteFind.getTitle());

            List<Note> notes = noteBean.getAllNotes();
            for (Note note:notes){
                System.out.println(note.getTitle() + " | " + note.getOwner().getName());
            }
            noteBean.update(noteBruna,"TAES - Finish US21", "do the katalon tests also");
            noteBean.delete(noteRafael);

            System.out.println("# Administrators ");
            System.out.println("## Creating Administrators ");
            long adminRafael = administratorBean.create("RafaelPereira@hotmail.com","1234","Rafael Pereira");
            System.out.println("## Updating Administrators ");
            administratorBean.update(adminRafael,"RafaelMendesPereira@hotmail.com","Rafael Mendes Pereira");
            System.out.println("## Deleting Administrators ");
            //administratorBean.delete(adminRafael);

            System.out.println("# Teachers ");
            System.out.println("## Creating Teachers ");
            long teacherCarlos = teacherBean.create("CarlosGrilo@hotmail.com","1234","Carlos Grilo");
            System.out.println("## Updating Teachers ");
            teacherBean.update(teacherCarlos,"CarlosGrilo@my.ipleiria.pt","Carlos Grilo");
            System.out.println("## Deleting Teachers ");
            //teacherBean.delete(teacherCarlos);

            System.out.println("# Channels ");
            System.out.println("## Creating Channel ");
            long channelDAE = channelBean.create(teacherCarlos,"DAE","DAE", true, 6);
            System.out.println("## Updating Channel ");
            channelBean.update(channelDAE,"DAE","Desenvolvimento de Aplicações Empresariais", 6);
            System.out.println("## Deleting Channel ");
            channelBean.delete(channelDAE);

        }catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
        }
    }


}

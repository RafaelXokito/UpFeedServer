package pt.ipleiria.estg.dei.ei.UpFeed.ejbs;

import pt.ipleiria.estg.dei.ei.UpFeed.entities.*;
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
    SubjectRoomBean subjectRoomBean;

    @EJB
    StudyRoomBean studyRoomBean;
    // Pay attention to the correct import: import java.util.logging.Logger;
    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");

    @PostConstruct
    public void populateDB() throws MyEntityNotFoundException, MyIllegalArgumentException, MyConstraintViolationException {
        System.out.println("Starting Point!");
        try {

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
            long teacherRicardoMartinho = teacherBean.create("RicardoMartinho@my.ipleiria.pt","1234","Ricardo Martinho");
            System.out.println("## Updating Teachers ");
            teacherBean.update(teacherCarlos,"CarlosGrilo@my.ipleiria.pt","Carlos Grilo");
            System.out.println("## Deleting Teachers ");
            //teacherBean.delete(teacherCarlos);

            System.out.println("# Students ");
            System.out.println("## Creating Students ");
            long studentCarla = studentBean.create("Carla Mendes", "1112@my.ipleiria.pt", "1234");
            long studentRafael = studentBean.create("Rafael Pereira", "1113@my.ipleiria.pt", "1234");
            long studentBruna = studentBean.create("Bruna Leitão", "1114@my.ipleiria.pt", "1234");
            long studentCarlos = studentBean.create("Carlos Costa", "1115@my.ipleiria.pt", "1234");
            long studentFabio = studentBean.create("Fábio Gaspar", "1116@my.ipleiria.pt", "1234");
            long studentDaniel = studentBean.create("Daniel Carreira", "1117@my.ipleiria.pt", "1234");

            System.out.println("## Find Students ");
            Student student = studentBean.findStudent(studentRafael);
            System.out.println("Find - "+ student.getName());
            List<Student> studentsList = studentBean.getAllStudents();
            for (Student s:studentsList){
                System.out.println(s.getName());
            }

            System.out.println("## Update Students ");
            studentBean.update(studentCarla, "Carla Sofia Crespo Mendes", "1111@my.ipleiria.pt");
            studentBean.update(studentCarlos, "Carlos Pereira Martinho da Costa", "");
            //studentBean.delete(studentCarla);

            System.out.println("# Categories");
            System.out.println("## Create Categories");
            long categoryDAE = categoryBean.create("1113@my.ipleiria.pt", "DAE");
            long categorySAD = categoryBean.create("1116@my.ipleiria.pt", "SAD");
            long categoryDAD = categoryBean.create("1117@my.ipleiria.pt", "DAD");
            long categoryTAES = categoryBean.create("1114@my.ipleiria.pt", "TAES");

            System.out.println("## Find Categories");
            Category category = categoryBean.find(categoryDAE);
            System.out.println("Find - "+ categoryDAE + " | " + category.getName());

            List<Category> categories = categoryBean.getAllCategories();
            for (Category c:categories){
                System.out.println(c.getName() + " | " + c.getOwner().getName());
            }

            System.out.println("## Update Categories");
            categoryBean.update(categoryTAES, "Advanced Topics of Software Enginnering");
            categoryBean.delete(categoryDAD);

            System.out.println("# Channels ");
            System.out.println("## Creating Channel ");
            long channelDAE = channelBean.create(teacherRicardoMartinho,"DAE","DAE", true, 6);
            long channel3AnoStudent = channelBean.create(studentFabio,"EI - 3º Ano","Este channel deve-se apenas e somente ao estudo do terceiro ano de EI", false, null);
            System.out.println("## Updating Channel ");
            channelBean.update(channelDAE,"DAE","Desenvolvimento de Aplicações Empresariais", 6);
            System.out.println("## Deleting Channel ");
            //channelBean.delete(channelDAE);
            System.out.println("## Adding Users to Channel ");
            channelBean.addUserToChannel(channelDAE,"CarlosGrilo@my.ipleiria.pt");
            channelBean.addUserToChannel(channelDAE,"1113@my.ipleiria.pt");
            channelBean.addUserToChannel(channelDAE,"1114@my.ipleiria.pt");
            channelBean.addUserToChannel(channelDAE,"1115@my.ipleiria.pt");

            channelBean.addUserToChannel(channel3AnoStudent,"1111@my.ipleiria.pt");
            channelBean.addUserToChannel(channel3AnoStudent,"1113@my.ipleiria.pt");
            channelBean.addUserToChannel(channel3AnoStudent,"1114@my.ipleiria.pt");
            channelBean.addUserToChannel(channel3AnoStudent,"1115@my.ipleiria.pt");
            channelBean.addUserToChannel(channel3AnoStudent,"1116@my.ipleiria.pt");
            channelBean.addUserToChannel(channel3AnoStudent,"1117@my.ipleiria.pt");


            System.out.println("#Rooms");
            System.out.println("## SubjectRooms ");
            System.out.println("### Creating SubjectRoom ");
            long subjectRoomDAE_TP = subjectRoomBean.create("RicardoMartinho@my.ipleiria.pt", channelDAE, "TP - Diurno", "Teórico-Prático Regime Diurno", 35);
            subjectRoomBean.addStudentToSubjectRoom(subjectRoomDAE_TP,"1113@my.ipleiria.pt");
            subjectRoomBean.addStudentToSubjectRoom(subjectRoomDAE_TP,"1114@my.ipleiria.pt");
            subjectRoomBean.addStudentToSubjectRoom(subjectRoomDAE_TP,"1115@my.ipleiria.pt");

            long subjectRoomDAE_PL1 = subjectRoomBean.create("CarlosGrilo@my.ipleiria.pt", channelDAE, "PL1 - Diurno", "Prática-Laboratorial 1 Regime Diurno", 60);
            subjectRoomBean.addStudentToSubjectRoom(subjectRoomDAE_PL1,"1113@my.ipleiria.pt");

            long subjectRoomDAE_PL2 = subjectRoomBean.create("RicardoMartinho@my.ipleiria.pt", channelDAE, "PL2 - Diurno", "Prática-Laboratorial 2 Regime Diurno", 60);
            subjectRoomBean.addStudentToSubjectRoom(subjectRoomDAE_PL2,"1114@my.ipleiria.pt");
            subjectRoomBean.addStudentToSubjectRoom(subjectRoomDAE_PL2,"1115@my.ipleiria.pt");

            System.out.println("### Updating SubjectRoom ");
            subjectRoomBean.update(subjectRoomDAE_TP,"TP - Diurno", "Teórico-Prático Regime Diurno", 40);
            System.out.println("### Deleting SubjectRoom ");
            //subjectRoomBean.delete(subjectRoomDAE_TP);

            System.out.println("## StudyRooms ");
            System.out.println("### Creating StudyRoom ");
            long studyRoomDAE = studyRoomBean.create(channelDAE, "Estudo DAE", "Easy 27");
            studyRoomBean.addStudentToStudyRoom(studyRoomDAE,"1113@my.ipleiria.pt");
            studyRoomBean.addStudentToStudyRoom(studyRoomDAE,"1114@my.ipleiria.pt");
            studyRoomBean.addStudentToStudyRoom(studyRoomDAE,"1115@my.ipleiria.pt");

            studyRoomBean.removeStudentToStudyRoom(studyRoomDAE,"1115@my.ipleiria.pt");

            long studyRoom3AnoStudent = studyRoomBean.create(channel3AnoStudent, "Le Gang B T Q i +", "Easy 39");
            studyRoomBean.addStudentToStudyRoom(studyRoom3AnoStudent,"1111@my.ipleiria.pt");
            studyRoomBean.addStudentToStudyRoom(studyRoom3AnoStudent,"1113@my.ipleiria.pt");
            studyRoomBean.addStudentToStudyRoom(studyRoom3AnoStudent,"1114@my.ipleiria.pt");
            studyRoomBean.addStudentToStudyRoom(studyRoom3AnoStudent,"1115@my.ipleiria.pt");
            studyRoomBean.addStudentToStudyRoom(studyRoom3AnoStudent,"1116@my.ipleiria.pt");
            studyRoomBean.addStudentToStudyRoom(studyRoom3AnoStudent,"1117@my.ipleiria.pt");

            System.out.println("### Updating StudyRoom ");
            studyRoomBean.update(studyRoomDAE,"Estudo", "Secção de estudo da UC de Desenvolvimento de Aplicações Empresariais");
            System.out.println("### Deleting StudyRoom ");
            //studyRoomBean.delete(studyRoomDAE);

            System.out.println("# Reading Users to Channel (DAE) ");
            Channel objChannelDAE = channelBean.findChannel(channelDAE);
            System.out.println(objChannelDAE.getTitle() + " | Owner - " + objChannelDAE.getOwner().getName());
            System.out.println("----------------------------");
            for (User user:objChannelDAE.getUsers()){
                System.out.println(user.getName());
            }
            for (Room room:objChannelDAE.getRooms()){
                System.out.println("----------- "+room.getTitle()+ "|" + room.getClass().getSimpleName() + " -----------");
                for (User user:room.getStudents()){
                    System.out.println(user.getName());
                }
            }

            System.out.println("# Reading Users to Channel (3º Ano Students) ");
            Channel objChannel3AnoStudents = channelBean.findChannel(channel3AnoStudent);
            System.out.println(objChannel3AnoStudents.getTitle() + " | Owner - " + objChannel3AnoStudents.getOwner().getName());
            System.out.println("----------------------------");
            for (User user:objChannel3AnoStudents.getUsers()){
                System.out.println(user.getName());
            }
            for (Room room:objChannel3AnoStudents.getRooms()){
                System.out.println("----------- "+room.getTitle()+ "|" + room.getClass().getSimpleName() + " -----------");
                for (User user:room.getStudents()){
                    System.out.println(user.getName());
                }
            }


        }catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
        }
    }


}

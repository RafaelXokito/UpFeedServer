package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

import java.util.List;

public class SubjectRoomDTO {
    private long id;
    private String title;
    private String description;
    private List<StudentDTO> students;
    //private List<PostDTO> posts; TODO
    private ChannelDTO channel;
    private int weight;
    private TeacherDTO teacher;

    public SubjectRoomDTO(long id, String title, String description, List<StudentDTO> students, ChannelDTO channel, int weight, TeacherDTO teacher) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.students = students;
        this.channel = channel;
        this.weight = weight;
        this.teacher = teacher;
    }

    public SubjectRoomDTO(String title, String description, List<StudentDTO> students, ChannelDTO channel, int weight, TeacherDTO teacher) {
        this.title = title;
        this.description = description;
        this.students = students;
        this.channel = channel;
        this.weight = weight;
        this.teacher = teacher;
    }

    public SubjectRoomDTO(String title, String description, ChannelDTO channel, int weight, TeacherDTO teacher) {
        this.title = title;
        this.description = description;
        this.channel = channel;
        this.weight = weight;
        this.teacher = teacher;
    }

    public SubjectRoomDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }

    public ChannelDTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelDTO channel) {
        this.channel = channel;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public TeacherDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherDTO teacher) {
        this.teacher = teacher;
    }
}

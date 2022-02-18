package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

public class GradeDTO {
    private long id;
    private float value;
    private String studentEmail;
    private long subjectRoomId;
    private String observations;

    public GradeDTO() {
    }

    public GradeDTO(Float value, String studentEmail, long subjectRoomId, String observations) {
        this.value = value;
        this.studentEmail = studentEmail;
        this.subjectRoomId = subjectRoomId;
        this.observations = observations;
    }

    public GradeDTO(long id, Float value, String studentEmail, long subjectRoomId, String observations) {
        this(value, studentEmail, subjectRoomId, observations);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public long getSubjectRoomId() {
        return subjectRoomId;
    }

    public void setSubjectRoomId(long subjectRoomId) {
        this.subjectRoomId = subjectRoomId;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}

package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

import java.util.Date;

public class PostDTO {
    private long id;
    private String emailOwner;
    private String title;
    private String description;
    private boolean type;
    private long roomId;
    private Date date;

    public PostDTO() {
    }

    public PostDTO(String emailOwner, String title, String description, boolean type, long roomId, Date date) {
        this.emailOwner = emailOwner;
        this.title = title;
        this.description = description;
        this.type = type;
        this.roomId = roomId;
        this.date = date;
    }

    public PostDTO(long id, String emailOwner, String title, String description, boolean type, long roomId, Date date) {
        this(emailOwner, title, description, type, roomId, date);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmailOwner() {
        return emailOwner;
    }

    public void setEmailOwner(String emailOwner) {
        this.emailOwner = emailOwner;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
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
}

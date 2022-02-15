package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import pt.ipleiria.estg.dei.ei.UpFeed.utils.Hour;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Table(name = "POSTS")
@Entity
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "userId")
    private User owner;
    @ManyToOne
    @JoinColumn(name = "roomId")
    private Room room;
    @NotNull
    private String description;
    @NotNull
    private Boolean type;
    @NotNull
    private Date date;
    @NotNull
    private Hour hour;

    public Post() {
    }

    public Post(User owner, Room room, String description, Boolean type, Date date, Hour hour) {
        this.owner = owner;
        this.description = description;
        this.type = type;
        this.date = date;
        this.hour = hour;
    }

    public Post(Long id,  User owner, Room room,  String description, Boolean type, Date date, Hour hour) {
        this(owner, room, description, type, date, hour);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Hour getHour() {
        return hour;
    }

    public void setHour(Hour hour) {
        this.hour = hour;
    }
}

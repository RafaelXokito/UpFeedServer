package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import pt.ipleiria.estg.dei.ei.UpFeed.utils.Hour;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@NamedQueries({
        @NamedQuery(
            name="getAllPosts",
            query="SELECT p FROM Post p ORDER BY p.id"
        )
})
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
    private String title;
    @NotNull
    private String description;
    @NotNull
    private Boolean type;
    @NotNull
    private Date date;

    public Post() {
    }

    public Post(User owner, Room room, String title, String description, Boolean type) {
        this.owner = owner;
        this.description = description;
        this.type = type;
        this.date = new Date();
    }

    public Post(Long id,  User owner, Room room, String title, String description, Boolean type) {
        this(owner, room, title, description, type);
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

}

package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@NamedQueries({
        @NamedQuery(
                name = "getAllChannels",
                query = "SELECT channel FROM Channel channel ORDER BY channel.title"
        )
})
@Table(name = "CHANNELS")
@Entity
public class Channel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String title;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "userId")
    private User owner;
    @NotNull
    private String description;
    @NotNull
    private Boolean type;
    @NotNull
    private int weight;
    @NotNull
    @OneToMany(mappedBy = "channel", cascade = CascadeType.REMOVE)
    private List<Room> rooms;

    @Version
    private int version;

    public Channel() {
        this.rooms = new LinkedList<>();
    }

    public Channel(String title, User owner, String description, Boolean type, int weight) {
        this();
        this.title = title;
        this.owner = owner;
        this.description = description;
        this.type = type;
        this.weight = weight;
    }

    public Channel(Long id, String title, User owner, String description, Boolean type, int weight) {
        this(title, owner, description, type, weight);
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getType() {
        return this.type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
    public void addRooms(Room room){
        if(room == null || rooms.contains(room)){
            return;
        }
        rooms.add(room);
    }

    public void removeRooms(Room room){
        if(room == null || !rooms.contains(room)){
            return;
        }
        rooms.remove(room);
    }
}

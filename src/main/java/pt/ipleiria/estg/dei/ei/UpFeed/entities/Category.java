package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Table(name = "CATEGORIES")
@Entity
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "userId")
    private User owner;
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Note> notes;

    public Category() {
        this.notes = new LinkedList<>();
    }

    public Category(String name, User owner) {
        this();
        this.name = name;
        this.owner = owner;
    }
    public Category(Long id, String name, User owner) {
        this(name, owner);
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
    public void addNote(Note note){
        if(note == null || notes.contains(note)){
            return;
        }
        notes.add(note);
    }

    public void removeNote(Note note){
        if(note == null || !notes.contains(note)){
            return;
        }
        notes.remove(note);
    }

}

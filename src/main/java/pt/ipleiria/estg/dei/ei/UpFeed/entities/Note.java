package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Table(name = "NOTES")
@Entity
public class Note implements Serializable {
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
    private Boolean status;
    @ManyToOne
    @JoinColumn(name = "categoryId")
    @NotNull
    private Category category;

    public Note() {
    }

    public Note(String title, User owner, String description, Boolean status, Category category) {
        this.title = title;
        this.owner = owner;
        this.description = description;
        this.status = status;
        this.category = category;
    }

    public Note(Long id, String title, User owner, String description, Boolean status, Category category) {
        this(title, owner, description, status, category);
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

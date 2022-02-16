package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

public class NoteDTO {
    private long id;
    private String emailOwner;
    private String title;
    private String description;
    private boolean status;
    private long categoryId;

    public NoteDTO() {
    }

    public NoteDTO(String emailOwner, String title, String description, boolean status, long categoryId) {
        this.emailOwner = emailOwner;
        this.title = title;
        this.description = description;
        this.status = status;
        this.categoryId = categoryId;
    }

    public NoteDTO(long id, String emailOwner, String title, String description, boolean status, long categoryId) {
       this(emailOwner, title, description, status, categoryId);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmailOwner() {
        return emailOwner;
    }

    public void setEmailOwner(String emailOwner) {
        this.emailOwner = emailOwner;
    }

    public String getTitle() {
        return this.title;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}

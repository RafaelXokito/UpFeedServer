package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

public class CategoryDTO {
    private long id;
    private String emailOwner; //TODO email or id?
    private String name;

    public CategoryDTO() {
    }

    public CategoryDTO(String emailOwner, String name) {
        this.emailOwner = emailOwner;
        this.name = name;
    }

    public CategoryDTO(long id, String emailOwner, String name) {
        this.id = id;
        this.emailOwner = emailOwner;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

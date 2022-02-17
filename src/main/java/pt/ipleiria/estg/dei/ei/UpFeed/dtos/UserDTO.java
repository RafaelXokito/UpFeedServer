package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

public class UserDTO {
    private long id;
    private String email;
    private String password;
    private String name;

    public UserDTO(long id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserDTO(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserDTO(long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public UserDTO(String email) {
        this.email = email;
    }

    public UserDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

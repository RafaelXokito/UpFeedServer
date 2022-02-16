package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

public class ChannelDTO {
    private Long id;
    private UserDTO user;
    private String title;
    private String description;
    private Boolean type;
    private Integer weight;

    public ChannelDTO(Long id, UserDTO user, String title, String description, Boolean type, Integer weight) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.type = type;
        this.weight = weight;
    }

    public ChannelDTO(String title, String description, Boolean type, Integer weight) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.weight = weight;
    }

    public ChannelDTO(UserDTO user, String title, String description, Boolean type, Integer weight) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.type = type;
        this.weight = weight;
    }

    public ChannelDTO(String title, String description, Integer weight) {
        this.title = title;
        this.description = description;
        this.weight = weight;
    }

    public ChannelDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}

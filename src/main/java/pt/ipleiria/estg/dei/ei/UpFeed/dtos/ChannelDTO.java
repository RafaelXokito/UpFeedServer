package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChannelDTO {
    private Long id;
    private UserDTO user;
    private String title;
    private String description;
    private Boolean type;
    private Integer weight;
    private List<UserDTO> users;

    public ChannelDTO(Long id, UserDTO user, String title, String description, Boolean type, Integer weight, List<UserDTO> users) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.type = type;
        this.weight = weight;
        this.users = users;
    }

    public ChannelDTO(String title, String description, Boolean type, Integer weight, List<UserDTO> users) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.weight = weight;
        this.users = users;
    }

    public ChannelDTO(UserDTO user, String title, String description, Boolean type, Integer weight, List<UserDTO> users) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.type = type;
        this.weight = weight;
        this.users = users;
    }

    public ChannelDTO(String title, String description, Integer weight, List<UserDTO> users) {
        this.title = title;
        this.description = description;
        this.weight = weight;
        this.users = users;
    }

    public ChannelDTO(List<UserDTO> users) {
        this.users = users;
    }

    public ChannelDTO() {
        this.users = new ArrayList<>();
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

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
}

package pt.ipleiria.estg.dei.ei.UpFeed.dtos;

import java.util.Date;
import java.util.List;

public class PersonDTO {
    private long id;
    private String email;
    private String password;
    private String name;
    private String scope; //Administrator, Teacher, Student
    private List<NoteDTO> notes;
    private List<CategoryDTO> categories;
    private List<PostDTO> posts;
    private List<ChannelDTO> channels;


    public PersonDTO(long id, String email, String password, String name, String scope, List<NoteDTO> notes, List<CategoryDTO> categories, List<PostDTO> posts, List<ChannelDTO> channels) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.scope = scope;
        this.notes = notes;
        this.categories = categories;
        this.posts = posts;
        this.channels = channels;
    }

    public PersonDTO(long id, String email, String name, String scope) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.scope = scope;
    }

    public PersonDTO(long id, String email, String name, String scope, List<ChannelDTO> channels) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.scope = scope;
        this.channels = channels;
    }

    public PersonDTO() {
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<NoteDTO> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteDTO> notes) {
        this.notes = notes;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public List<PostDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDTO> posts) {
        this.posts = posts;
    }

    public List<ChannelDTO> getChannels() {
        return channels;
    }

    public void setChannels(List<ChannelDTO> channels) {
        this.channels = channels;
    }
}


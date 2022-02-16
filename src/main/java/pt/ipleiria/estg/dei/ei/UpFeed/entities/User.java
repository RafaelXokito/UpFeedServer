package pt.ipleiria.estg.dei.ei.UpFeed.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


@Table(name = "USERS")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User extends Person implements Serializable{
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Note> notes;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Category> categories;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Post> posts;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Channel> channels;

    public User() {
        super();
        this.categories = new LinkedList<>();
        this.notes = new LinkedList<>();
        this.posts = new LinkedList<>();
        this.channels = new LinkedList<>();
    }
    public User(String name, String email, String password) {
        super(name,email,password);
        this.notes = new LinkedList<>();
        this.categories = new LinkedList<>();
        this.posts = new LinkedList<>();
        this.channels = new LinkedList<>();
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
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    public void addCategory(Category category){
        if(category == null || categories.contains(category)){
            return;
        }
        categories.add(category);
    }

    public void removeCategory(Category category){
        if(category == null || !categories.contains(category)){
            return;
        }
        categories.remove(category);
    }
    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
    public void addPost(Post post){
        if(post == null || posts.contains(post)){
            return;
        }
        posts.add(post);
    }

    public void removePost(Post post){
        if(post == null || !posts.contains(post)){
            return;
        }
        posts.remove(post);
    }
    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
    public void addChannel(Channel channel){
        if(channel == null || channels.contains(channel)){
            return;
        }
        channels.add(channel);
    }

    public void removeChannel(Channel channel){
        if(channel == null || !channels.contains(channel)){
            return;
        }
        channels.remove(channel);
    }
}

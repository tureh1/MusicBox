package onetomany.Posts;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Date;

import onetomany.Users.User;



public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String contents;
    private int rating;
    private Date date = new Date();

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getContents(){
        return contents;
    }

    public void setContents(String contents){
        this.contents = contents;
    }

    public int getRating(){
        return rating;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public int  getAlbumId() {
        return albumId;
    }
/*
    public void setAlbumId(Integer  albumId) {
        this.albumId = albumId;
    }
*/
    public User getUser(){
        return User;
    }

    public void setUser(User user){
        this.User = user;
    }
}

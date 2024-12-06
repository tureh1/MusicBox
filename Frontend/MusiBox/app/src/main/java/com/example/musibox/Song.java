package com.example.musibox;

public class Song {
    private int id;
    private String title;
    private String artist;
    private double averageRating;
    private float userRating;  // Store user rating here
    private boolean isHeartFilled;

    public Song(int id, String title, String artist, double averageRating) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.averageRating = averageRating;
        this.isHeartFilled = false;

    }

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public float getUserRating() {
        return userRating;
    }
    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }
    public String getTitle() {
        return title;
    }

    public String getArtist() {

        return artist;
    }
    public String setTitle(String title) {

        return title;
    }
    public void setArtist(String artist) {
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public boolean isHeartFilled() {
        return isHeartFilled;
    }

    public void setHeartFilled(boolean heartFilled) {
        isHeartFilled = heartFilled;
    }

}
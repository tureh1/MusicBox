package com.example.musibox;

public class Song {
    private int id;
    private String title;
    private String artist;
    private float averageRating;

    public Song(int id, String title, String artist, float averageRating) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.averageRating = averageRating;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }
}
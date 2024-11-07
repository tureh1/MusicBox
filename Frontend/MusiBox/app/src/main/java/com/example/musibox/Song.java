package com.example.musibox;

public class Song {
    private int id;
    private String title;
    private String artist;
    private double averageRating;

    public Song(int id, String title, String artist, double averageRating) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.averageRating = averageRating;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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


}
package com.example.musibox;

public class Album {
    private String coverUrl;
    private String name;
    private String artist;
    private String releaseDate;
    private float averageRating;

    public Album(String coverUrl, String name, String artist, String releaseDate, float averageRating) {
        this.coverUrl = coverUrl;
        this.name = name;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.averageRating = averageRating;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public float getAverageRating() {
        return averageRating;
    }
}


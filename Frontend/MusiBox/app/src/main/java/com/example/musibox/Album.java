package com.example.musibox;

public class Album {
    private String albumId;
    private String name;
    private String coverUrl;
    private String artist;
    private String releaseDate;
    private float averageRating;

    public Album(String coverUrl, String id, String name, String artist, String releaseDate, float averageRating) {
        this.coverUrl = coverUrl;
        this.albumId = id;
        this.name=name;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.averageRating = averageRating;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getName(){return name;}

    public String getArtist() {
        return artist;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }
}


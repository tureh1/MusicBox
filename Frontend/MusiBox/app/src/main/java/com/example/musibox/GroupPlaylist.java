package com.example.musibox;

import java.util.Set;

public class GroupPlaylist {
    private int id;
    private String name;
    private Set<Song> songs;

    // Constructor
    public GroupPlaylist(int id, String name, Set<Song> songs) {
        this.id = id;
        this.name = name;
        this.songs = songs;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }
}

package com.example.musibox;

import java.util.List;

public class Group {
    private String id;
    private String name;
    private List<String> members; // List of emails of members in the group
    private String groupName;
    private long playlistId;

    public Group(String id, String name, List<String> members, String groupName,long playlistId) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.groupName = groupName;
        this.playlistId = playlistId;
    }

    public Group(String groupName) {
        this.name = groupName;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}

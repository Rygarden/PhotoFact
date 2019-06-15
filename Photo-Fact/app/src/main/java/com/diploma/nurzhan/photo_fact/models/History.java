package com.diploma.nurzhan.photo_fact.models;

public class History {
    private String username;
    private String imageName;
    private String time;
    private String type;
    private String location;

    public History(String username, String imageName, String time, String type, String location) {
        this.username = username;
        this.imageName = imageName;
        this.time = time;
        this.type = type;
        this.location = location;
    }

    public History() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

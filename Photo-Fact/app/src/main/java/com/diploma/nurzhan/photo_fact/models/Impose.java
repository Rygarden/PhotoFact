package com.diploma.nurzhan.photo_fact.models;

import java.io.File;
import java.util.Date;

public class Impose {
    private File image;
    private String user;
    private int type;
    private double latitude;
    private double longitude;
    private Date date;

    public Impose(File image, String user, int type, double latitude, double longitude, Date date) {
        this.image = image;
        this.user = user;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public Impose() {
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Impose{" +
                "image=" + image +
                ", user='" + user + '\'' +
                ", type=" + type +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", date=" + date +
                '}';
    }
}

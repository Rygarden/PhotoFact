package com.diploma.nurzhan.photo_fact.models.JSONmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("img_url")
    @Expose
    private String imgUrl;
    @SerializedName("offense_image_name")
    @Expose
    private String offenseImageName;
    @SerializedName("offense_type")
    @Expose
    private String offenseType;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("offense_status")
    @Expose
    private String offenseStatus;
    @SerializedName("fineAddress")
    @Expose
    private String fineAddress;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getOffenseImageName() {
        return offenseImageName;
    }

    public void setOffenseImageName(String offenseImageName) {
        this.offenseImageName = offenseImageName;
    }

    public String getOffenseType() {
        return offenseType;
    }

    public void setOffenseType(String offenseType) {
        this.offenseType = offenseType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOffenseStatus() {
        return offenseStatus;
    }

    public void setOffenseStatus(String offenseStatus) {
        this.offenseStatus = offenseStatus;
    }

    public String getFineAddress() {
        return fineAddress;
    }

    public void setFineAddress(String fineAddress) {
        this.fineAddress = fineAddress;
    }

}
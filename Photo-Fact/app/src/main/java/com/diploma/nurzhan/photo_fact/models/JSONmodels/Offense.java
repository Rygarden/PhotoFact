package com.diploma.nurzhan.photo_fact.models.JSONmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Offense {

    @SerializedName("data")
    @Expose
    private List<Data> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
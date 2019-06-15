package com.diploma.nurzhan.photo_fact.repository;

public class ApiUtils {
    public static final String BASE_URL = "http://89.223.95.152";

    public static UserService getUserService(){
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}

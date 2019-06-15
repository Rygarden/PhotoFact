package com.diploma.nurzhan.photo_fact.repository;

import com.diploma.nurzhan.photo_fact.models.JSONmodels.Offense;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface UserService {

    @FormUrlEncoded
    @POST("login")
        //endpoints
    Call<ResponseBody> login( //asynchronous request
                              @FieldMap Map<String, String> userLoginData
    );

    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> register(
            @FieldMap Map<String, String> userRegistrationData
    );

    @POST("logout")
    Call<Void> logout();

    @Multipart
    @POST("create")
    Call<ResponseBody> uploadFileWithPartMap(
            @Part MultipartBody.Part file,
            @PartMap() Map<String, RequestBody> partMap
    );

    @FormUrlEncoded
    @POST("history")
    Call<Offense> loadHistory(
            @FieldMap Map<String, String> username
    );

    @FormUrlEncoded
    @POST("history/image")
    Call<ResponseBody> getImage(
            @FieldMap Map<String, String> imageName
    );

    @FormUrlEncoded
    @POST("delete_offense")
    Call<ResponseBody> deleteOffense(
            @FieldMap Map<String, String> deleteItems
    );

    @FormUrlEncoded
    @POST("add_card")
    Call<String> addCard(
            @FieldMap Map<String, String> cards
    );

    @FormUrlEncoded
    @POST("profile")
    Call<String> profile(
            @FieldMap Map<String, String> profile
    );

}

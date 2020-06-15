package com.example.socialmedia.rest.services;

import com.example.socialmedia.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserInterface {

    @POST("login")
    Call<Boolean> signin(@Body LoginActivity.UserInfo userInfo);
}

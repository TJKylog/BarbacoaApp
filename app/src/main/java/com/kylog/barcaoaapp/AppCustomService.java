package com.kylog.barcaoaapp;

import com.kylog.barcaoaapp.models.LoginForm;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AppCustomService {
    @POST("api/auth/login")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<Auth> login(@Body LoginForm loginForm);
}

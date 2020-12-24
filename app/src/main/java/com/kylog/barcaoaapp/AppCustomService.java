package com.kylog.barcaoaapp;

import com.kylog.barcaoaapp.models.Auth;
import com.kylog.barcaoaapp.models.Product;
import com.kylog.barcaoaapp.models.forms.LoginForm;
import com.kylog.barcaoaapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AppCustomService {
    @POST("api/auth/login")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<Auth> login(@Body LoginForm loginForm);

    @GET("api/auth/user")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<User> user(@Header("Authorization") String authorization);

    @GET("api/auth/products")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Class <List<Product>> products(@Header("Authorization") String authorization);
}

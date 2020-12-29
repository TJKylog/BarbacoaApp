package com.kylog.barcaoaapp;

import com.kylog.barcaoaapp.models.Auth;
import com.kylog.barcaoaapp.models.Mesa;
import com.kylog.barcaoaapp.models.Product;
import com.kylog.barcaoaapp.models.forms.LoginForm;
import com.kylog.barcaoaapp.models.User;
import com.kylog.barcaoaapp.models.forms.NewMesaForm;
import com.kylog.barcaoaapp.models.forms.NewProductForm;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    Call<List<Product>> products(@Header("Authorization") String authorization);

    @POST("api/auth/products")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<Product> create_product(@Header("Authorization") String authorization, @Body NewProductForm newProductForm);

    @DELETE("api/auth/products/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> delete_product(@Header("Authorization") String authorization, @Path("id") Integer id);

    @GET("api/auth/mesas")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<List<Mesa>> mesas(@Header("Authorization") String authorization);

    @POST("api/auth/mesas")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<Mesa> create_mesa(@Header("Authorization") String authorization, @Body NewMesaForm newMesaForm);

}

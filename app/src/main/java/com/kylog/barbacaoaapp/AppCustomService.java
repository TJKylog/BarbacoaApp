package com.kylog.barbacaoaapp;

import com.kylog.barbacaoaapp.models.Auth;
import com.kylog.barbacaoaapp.models.Mesa;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.forms.LoginForm;
import com.kylog.barbacaoaapp.models.User;
import com.kylog.barbacaoaapp.models.forms.NewMesaForm;
import com.kylog.barbacaoaapp.models.forms.NewProductForm;
import com.kylog.barbacaoaapp.models.forms.ProductsFrom;
import com.kylog.barbacaoaapp.models.forms.UserForm;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("api/auth/products/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<Product> product(@Header("Authorization") String authorization, @Path("id") Integer id);

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

    @PUT("api/auth/products/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<Product> update_product(@Header("Authorization") String authorization, @Path("id") Integer id, @Body NewProductForm newProductForm);

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

    @GET("api/auth/users")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<List<User>> get_users(@Header("Authorization") String authorization);

    @GET("api/auth/users/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<List<User>> get_user(@Header("Authorization") String authorization,@Path("id") Integer id);

    @POST("api/auth/users")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<User> create_user(@Header("Authorization") String authorization, @Body UserForm userForm);

    @DELETE("api/auth/users/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> delete_user(@Header("Authorization") String authorization,@Path("id") Integer id);

    @PUT("api/auth/users/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<User> update_user(@Header("Authorization") String authorization,@Path("id") Integer id,@Body UserForm userForm);

}

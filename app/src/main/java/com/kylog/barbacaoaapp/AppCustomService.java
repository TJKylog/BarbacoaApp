package com.kylog.barbacaoaapp;

import com.kylog.barbacaoaapp.models.ActiveMesa;
import com.kylog.barbacaoaapp.models.Auth;
import com.kylog.barbacaoaapp.models.Consume;
import com.kylog.barbacaoaapp.models.DataAvailable;
import com.kylog.barbacaoaapp.models.Mesa;
import com.kylog.barbacaoaapp.models.Note;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.ProductType;
import com.kylog.barbacaoaapp.models.forms.AddAmount;
import com.kylog.barbacaoaapp.models.forms.DeleteProduct;
import com.kylog.barbacaoaapp.models.forms.FormActive;
import com.kylog.barbacaoaapp.models.forms.LoginForm;
import com.kylog.barbacaoaapp.models.User;
import com.kylog.barbacaoaapp.models.forms.NewMesaForm;
import com.kylog.barbacaoaapp.models.forms.NewProductForm;
import com.kylog.barbacaoaapp.models.forms.ProductsFrom;
import com.kylog.barbacaoaapp.models.forms.UserForm;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
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

    @GET("api/auth/mesas/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<Mesa> get_mesa(@Header("Authorization") String authorization,@Path("id") Integer id);

    @PUT("api/auth/mesas/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> update_mesa(
            @Header("Authorization") String authorization,
            @Path("id") Integer id,
            @Body NewMesaForm newMesaForm);

    @DELETE("api/auth/mesas/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> delete_mesa(@Header("Authorization") String authorization, @Path("id") Integer id);

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
    Call<User> get_user(@Header("Authorization") String authorization,@Path("id") Integer id);

    @POST("api/auth/users")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<User> create_user(@Header("Authorization") String authorization, @Body UserForm userForm);

    @PUT("api/auth/users/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<User> update_user(@Header("Authorization") String authorization, @Path("id") Integer id, @Body UserForm userForm);

    @DELETE("api/auth/users/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> delete_user(@Header("Authorization") String authorization,@Path("id") Integer id);

    @GET("api/auth/types")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<List<ProductType>> get_types(@Header("Authorization") String authorization);

    @GET("api/auth/mesas_active")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<List<ActiveMesa>> get_active(@Header("Authorization") String authorization);

    @GET("api/auth/get/available/info")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<DataAvailable> get_data_available(@Header("Authorization") String authorization);

    @POST("api/auth/add/active")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> add_active(@Header("Authorization") String authorization, @Body FormActive formActive);

    @DELETE("api/auth/delete/active/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> delete_active(@Header("Authorization") String authorization, @Path("id") Integer id);

    @GET("api/auth/products/type/{type}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<List<Product>> get_products_by_type(@Header("Authorization") String authorization, @Path("type") String type);

    @GET("api/auth/mesas/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<Note> get_mesa_consume(@Header("Authorization") String authorization, @Path("id") Integer id);

    @PUT("api/auth/mesa/update_product/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> add_product_mesa(@Header("Authorization") String authorization, @Path("id") Integer id,@Body AddAmount addAmount);

    @PUT("api/auth/mesa/delete_product/{id}")
    @Headers({
            "Content-Type: application/json",
            "X-Requested-With: XMLHttpRequest"
    })
    Call<ResponseBody> delete_product_mesa(@Header("Authorization") String authorization, @Path("id") Integer id,@Body DeleteProduct deleteProduct);

}

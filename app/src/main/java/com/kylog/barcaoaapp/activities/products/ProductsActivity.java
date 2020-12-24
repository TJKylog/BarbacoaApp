package com.kylog.barcaoaapp.activities.products;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.kylog.barcaoaapp.AppCustomService;
import com.kylog.barcaoaapp.R;
import com.kylog.barcaoaapp.models.Product;
import com.kylog.barcaoaapp.models.User;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductsActivity extends AppCompatActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            token = bundle.getString("token");
            Toast.makeText(ProductsActivity.this,token ,Toast.LENGTH_LONG);
        } else{
            Toast.makeText(ProductsActivity.this, "xD" , Toast.LENGTH_LONG);
        }
    }

    private void get_products(String token){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://18.219.178.157/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        AppCustomService service = retrofit.create(AppCustomService.class);

        String authUser = "Bearer ";

        Class<List<Product>> productCall = service.products(authUser+token);

        /* productCall.cast(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(ProductsActivity.this, "Productos obtenidos" , Toast.LENGTH_LONG).show();
                } else {
                    response.errorBody(); // do something with that
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }); */
    }

}
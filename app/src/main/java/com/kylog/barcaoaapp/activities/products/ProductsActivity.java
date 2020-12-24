package com.kylog.barcaoaapp.activities.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.kylog.barcaoaapp.AppCustomService;
import com.kylog.barcaoaapp.R;
import com.kylog.barcaoaapp.RetrofitClient;
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

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        get_products();
    }

    private void get_products(){
        AppCustomService service = RetrofitClient.getClient();

        Call<List<Product>> productCall = service.products(getTokenType()+" "+getToken());

        productCall.enqueue(new Callback<List<Product>>() {
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
        });
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
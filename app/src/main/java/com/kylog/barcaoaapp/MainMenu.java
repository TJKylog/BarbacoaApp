package com.kylog.barcaoaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barcaoaapp.activities.products.ProductsActivity;
import com.kylog.barcaoaapp.models.Auth;
import com.kylog.barcaoaapp.models.User;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainMenu extends AppCompatActivity {

    private SharedPreferences pref;
    private TextView tokenview;
    private Button products_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        tokenview = findViewById(R.id.token_view);
        products_button = findViewById(R.id.productos_button);

        products_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_products();
            }
        });

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        getUser();
    }

    private void getUser(){
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


        Call<User> userCall = service.user(getTokenType()+" "+getToken());

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    tokenview.setText(user.getEmail());
                    Toast.makeText(MainMenu.this, user.getName() , Toast.LENGTH_LONG);
                } else {
                    response.errorBody(); // do something with that
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainMenu.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void show_products()
    {
        Intent intent = new Intent(MainMenu.this , ProductsActivity.class);
        startActivity(intent);
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
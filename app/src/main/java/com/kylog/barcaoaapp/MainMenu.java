package com.kylog.barcaoaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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

    private TextView tokenview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        tokenview = findViewById(R.id.token_view);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            String token = bundle.getString("token");
            getUser(token);
        } else{
            Toast.makeText(MainMenu.this, "xD" , Toast.LENGTH_LONG);
        }
    }

    private void getUser(String token){
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

        Call<User> userCall = service.user(authUser+token);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    tokenview.setText(user.getEmail());
                    Toast.makeText(MainMenu.this, user.getName() , Toast.LENGTH_LONG).show();
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
}
package com.kylog.barcaoaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.kylog.barcaoaapp.models.Auth;
import com.kylog.barcaoaapp.models.forms.LoginForm;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;

    private EditText emailEdit;
    private EditText passwordEdit;
    private Boolean remember_me = true;
    private Button login_button;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailEdit = findViewById(R.id.user_email);
        passwordEdit = findViewById(R.id.user_password);
        login_button = findViewById(R.id.login_button);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        setCredentialsIfExist();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(new LoginForm(emailEdit.getText().toString(),passwordEdit.getText().toString(),remember_me));
            }
        });
    }

    private void login(LoginForm loginForm) {
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

        Call<Auth> authCall = service.login(loginForm);

        authCall.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if (response.isSuccessful()) {
                    Auth auth = response.body();

                    saveOnPreferences(auth.getAccessToken(),auth.getTokenType());

                    token = auth.getAccessToken();
                    Intent intent = new Intent(MainActivity.this , MainMenu.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    response.errorBody(); // do something with that
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() > 4;
    }

    private void saveOnPreferences(String accessToken, String tokenType) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token",accessToken);
        editor.putString("token_type",tokenType);
        editor.apply();
    }

    private void setCredentialsIfExist() {
        String token = getToken();
        String token_type = getTokenType();
        if(!TextUtils.isEmpty(token_type) && !TextUtils.isEmpty(token))
        {
            Intent intent = new Intent(MainActivity.this , MainMenu.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
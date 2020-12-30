package com.kylog.barbacaoaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.activities.mesas.MesasActivity;
import com.kylog.barbacaoaapp.activities.products.ProductsActivity;
import com.kylog.barbacaoaapp.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenu extends AppCompatActivity {

    private SharedPreferences pref;
    private TextView tokenview;
    private Button products_button;
    private Button mesas_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        tokenview = findViewById(R.id.token_view);
        products_button = findViewById(R.id.productos_button);
        mesas_button = findViewById(R.id.mesas_button);

        products_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_products();
            }
        });
        mesas_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_mesas();
            }
        });

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        getUser();
    }

    private void getUser(){

        AppCustomService service = RetrofitClient.getClient();

        Call<User> userCall = service.user(getTokenType()+" "+getToken());

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    tokenview.setText(user.getEmail());
                    Toast.makeText(MainMenu.this, user.getName() , Toast.LENGTH_LONG);
                } else { //this returns you to the login activity if your session is invalid or has expired
                    response.errorBody();
                    pref.edit().clear().apply();
                    Intent intent = new Intent(MainMenu.this , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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

    private void show_mesas(){
        Intent intent = new Intent(MainMenu.this , MesasActivity.class);
        startActivity(intent);
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
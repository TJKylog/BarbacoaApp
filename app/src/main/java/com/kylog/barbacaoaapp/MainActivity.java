package com.kylog.barbacaoaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.kylog.barbacaoaapp.activities.reset.password.EmailActivity;
import com.kylog.barbacaoaapp.models.Auth;
import com.kylog.barbacaoaapp.models.forms.LoginForm;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;

    private EditText emailEdit;
    private EditText passwordEdit;
    private Boolean remember_me = true;
    private Button login_button;
    private TextView reset_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailEdit = findViewById(R.id.user_email);
        passwordEdit = findViewById(R.id.user_password);
        login_button = findViewById(R.id.login_button);
        reset_password = findViewById(R.id.password_recovery_view);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        setCredentialsIfExist();

        reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EmailActivity.class);
                startActivity(intent);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(emailEdit.getText().toString()) && isValidPassword(passwordEdit.getText().toString())) {
                    login(new LoginForm(emailEdit.getText().toString(),passwordEdit.getText().toString(),remember_me));
                }
                else{
                    Toast.makeText(MainActivity.this, "Escribe un correo valido y una contraseña", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login(LoginForm loginForm) {
        AppCustomService service = RetrofitClient.getClient();

        Call<Auth> authCall = service.login(loginForm);

        authCall.enqueue(new Callback<Auth>() {
            @Override
            public void onResponse(Call<Auth> call, Response<Auth> response) {
                if (response.isSuccessful()) {
                    Auth auth = response.body();
                    saveOnPreferences(auth.getAccessToken(),auth.getTokenType());
                    Intent intent = new Intent(MainActivity.this , MainMenu.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    response.errorBody();
                    Toast.makeText(MainActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Auth> call, Throwable t) {
                Toast.makeText(MainActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
package com.kylog.barbacaoaapp.activities.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.products.ProductsActivity;
import com.kylog.barbacaoaapp.activities.products.ProductsCreate;
import com.kylog.barbacaoaapp.models.User;
import com.kylog.barbacaoaapp.models.forms.UserForm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersCreate extends AppCompatActivity {

    private SharedPreferences pref;
    private EditText editName;
    private EditText editEmail;
    private EditText editUserType;
    private EditText editPassword;
    private Button saveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_create);

        editName = findViewById(R.id.user_name_field);
        editEmail = findViewById(R.id.user_email_field);
        editUserType = findViewById(R.id.user_role_field);
        editPassword = findViewById(R.id.user_password_field);
        saveUser = findViewById(R.id.save_user_button);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        saveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_user(new UserForm(editName.getText().toString(),editEmail.getText().toString(),editPassword.getText().toString(),editUserType.getText().toString()));
            }
        });
    }

    private void save_user(UserForm userForm) {
        AppCustomService service = RetrofitClient.getClient();
        Call<User> userCall = service.create_user(getTokenType()+" "+getToken() , userForm);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    User user = response.body();
                    Intent intent = new Intent(UsersCreate.this , ProductsActivity.class);
                    startActivity(intent);
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UsersCreate.this, "Error", Toast.LENGTH_LONG).show();
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
package com.kylog.barbacaoaapp.activities.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private List<User> users;
    private ListView list_user_view;
    private UserAdapter userAdapter;
    private FloatingActionButton add_user_button_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        list_user_view = findViewById(R.id.user_list_labels);
        add_user_button_1 = findViewById(R.id.add_user_button);

        getUsers();

        add_user_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_user();
            }
        });
    }

    private void getUsers(){
        AppCustomService service = RetrofitClient.getClient();

        Call<List<User>> productCall = service.get_users(getTokenType()+" "+getToken());

        productCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users = new ArrayList<User>();

                    users = response.body();
                    userAdapter = new UserAdapter(UsersActivity.this, 0, (ArrayList<User>) users);
                    list_user_view.setAdapter(userAdapter);

                } else {
                    response.errorBody();
                    pref.edit().clear().apply();
                    Intent intent = new Intent(UsersActivity.this , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(UsersActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void add_user() {
        Intent intent = new Intent(UsersActivity.this, UsersCreate.class);
        startActivity(intent);
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
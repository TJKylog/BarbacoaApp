package com.kylog.barbacaoaapp.activities.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.products.ProductsActivity;
import com.kylog.barbacaoaapp.activities.products.ProductsEdit;
import com.kylog.barbacaoaapp.models.User;
import com.kylog.barbacaoaapp.models.forms.UserForm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersEdit extends AppCompatActivity {

    private SharedPreferences pref;
    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editType;
    private Button updateUser;
    private Integer id;
    private User user;
    private ImageButton userActionsButton,backButton;
    private TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_edit);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editName = findViewById(R.id.edit_user_name_field);
        editEmail = findViewById(R.id.edit_user_email_field);
        editPassword = findViewById(R.id.edit_user_password_field);
        editType = findViewById(R.id.edit_user_role_field);
        updateUser = findViewById(R.id.update_user_button);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            id = bundle.getInt("id");
            get_user(id);
            Toast.makeText(UsersEdit.this, "id: "+ id, Toast.LENGTH_LONG).show();
        }

        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_data(new UserForm(editName.getText().toString(),editEmail.getText().toString(),editPassword.getText().toString(),editType.getText().toString()));
            }
        });

        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);
        user_name = findViewById(R.id.user_name_view);
        user_name.setText(getUseName());

        userActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

    }

    private void get_user(Integer id) {
        AppCustomService service = RetrofitClient.getClient();
        Call<User> userCall = service.get_user(getTokenType()+" "+getToken(),id);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    user = response.body();
                    editName.setText(user.getName());
                    editEmail.setText(user.getEmail());
                    editType.setText(user.getRole());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UsersEdit.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void update_data(UserForm userForm) {
        AppCustomService service = RetrofitClient.getClient();
        Call<User> userCall = service.update_user(getTokenType()+" "+getToken(),id,userForm);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(UsersEdit.this, "Usuario actualizado" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UsersEdit.this , UsersActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UsersEdit.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_menu_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.show();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out: {
                AppCustomService service = RetrofitClient.getClient();
                Call<ResponseBody> responseBodyCall = service.logout(getTokenType()+" "+getToken());
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText(UsersEdit.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(UsersEdit.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(UsersEdit.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private String getUseName(){
        return pref.getString("user_name", null);
    }
    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
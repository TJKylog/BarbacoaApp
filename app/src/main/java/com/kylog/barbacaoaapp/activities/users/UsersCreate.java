package com.kylog.barbacaoaapp.activities.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.MainMenu;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.catalogs.CatalogsActivity;
import com.kylog.barbacaoaapp.activities.expenses.ExpensesActivity;
import com.kylog.barbacaoaapp.models.User;
import com.kylog.barbacaoaapp.models.forms.UserForm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersCreate extends AppCompatActivity {

    private SharedPreferences pref;
    private EditText editName, editEmail, editPassword, firstLastname,secondLastname;
    private Spinner editUserType;
    private Button saveUser;
    private ImageButton userActionsButton,backButton,mainMenu;
    private TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_create);

        editName = findViewById(R.id.user_name_field);
        editEmail = findViewById(R.id.user_email_field);
        editUserType = findViewById(R.id.user_role_field);
        editPassword = findViewById(R.id.user_password_field);
        saveUser = findViewById(R.id.save_user_button);
        firstLastname = findViewById(R.id.user_first_lastname);
        secondLastname = findViewById(R.id.user_second_lastname);
        editUserType = findViewById(R.id.user_role_field);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editUserType.setAdapter(adapter);


        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        saveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editUserType.getSelectedItem().toString().matches("Mesero"))
                {
                    if(editName.getText().toString().matches("") ||
                            !isValidEmail(editEmail.getText().toString()) ||
                            firstLastname.getText().toString().matches("") ||
                            secondLastname.getText().toString().matches("")) {

                        if(editName.getText().toString().matches("")) {
                            editName.setError("Completa este campo");
                        }
                        if(!isValidEmail(editEmail.getText().toString())) {
                            editEmail.setError("Escribe un correo valido");
                        }
                        if(firstLastname.getText().toString().matches("")) {
                            firstLastname.setError("Completa este campo");
                        }
                        if(secondLastname.getText().toString().matches("")) {
                            secondLastname.setError("Completa este campo");
                        }
                    }
                    else {
                        save_user(new UserForm(
                                editName.getText().toString(),
                                editEmail.getText().toString(),
                                firstLastname.getText().toString(),
                                secondLastname.getText().toString(),
                                editPassword.getText().toString(),
                                editUserType.getSelectedItem().toString()
                        ));
                    }
                }
                else {
                    if(editName.getText().toString().matches("") ||
                            !isValidEmail(editEmail.getText().toString()) ||
                            editPassword.getText().toString().matches("") ||
                            firstLastname.getText().toString().matches("") ||
                            secondLastname.getText().toString().matches("")) {

                        if(editName.getText().toString().matches("")) {
                            editName.setError("Completa este campo");
                        }
                        if(!isValidEmail(editEmail.getText().toString())) {
                            editEmail.setError("Escribe un correo valido");
                        }
                        if(!validatePassword()){
                            editPassword.setError("La contraseña debe ser mayor a 6 caracteres");
                        }
                        if(firstLastname.getText().toString().matches("")) {
                            firstLastname.setError("Completa este campo");
                        }
                        if(secondLastname.getText().toString().matches("")) {
                            secondLastname.setError("Completa este campo");
                        }
                    }
                    else {
                        save_user(new UserForm(
                                editName.getText().toString(),
                                editEmail.getText().toString(),
                                firstLastname.getText().toString(),
                                secondLastname.getText().toString(),
                                editPassword.getText().toString(),
                                editUserType.getSelectedItem().toString()
                        ));
                    }
                }
            }
        });
        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);
        user_name = findViewById(R.id.user_name_view);
        mainMenu = findViewById(R.id.to_main_menu_view);
        user_name.setText(getUseName());

        editUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 3){
                    editPassword.setEnabled(false);
                }
                else {
                    editPassword.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersCreate.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    private boolean validatePassword()
    {
        String password = editPassword.getText().toString();
        if(password.length() > 6)
            return true;

        return false;
    }

    private boolean isValidEmail(String email) {
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            return true;
        }
        else {
            return false;
        }
    }

    private void save_user(UserForm userForm) {
        AppCustomService service = RetrofitClient.getClient();
        Call<User> userCall = service.create_user(getTokenType()+" "+getToken() , userForm);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(UsersCreate.this, "Se guardó el usuario correctamente" , Toast.LENGTH_LONG).show();
                    User user = response.body();
                    Intent intent = new Intent(UsersCreate.this ,  UsersActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(UsersCreate.this, "El correo electrónico ya existe" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UsersCreate.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(UsersCreate.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(UsersCreate.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(UsersCreate.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
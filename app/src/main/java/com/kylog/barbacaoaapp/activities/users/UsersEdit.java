package com.kylog.barbacaoaapp.activities.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class UsersEdit extends AppCompatActivity {

    private SharedPreferences pref;
    private EditText editName, editEmail, editPassword, firstLastname,secondLastname;
    private Spinner editType;
    private Button updateUser;
    private Integer id;
    private User user;
    private ImageButton userActionsButton,backButton, mainMenu;
    private TextView user_name;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_edit);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editName = findViewById(R.id.edit_user_name_field);
        editEmail = findViewById(R.id.edit_user_email_field);
        editPassword = findViewById(R.id.edit_user_password_field);
        editType = findViewById(R.id.edit_user_role_field);
        firstLastname = findViewById(R.id.edit_user_first_lastname);
        secondLastname = findViewById(R.id.edit_user_second_lastname);
        updateUser = findViewById(R.id.update_user_button);

        editPassword.setText("defalutpass");

        adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editType.setAdapter(adapter);

        editType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                if(position == 3){
                    editPassword.setEnabled(false);
                    Toast.makeText(UsersEdit.this,text ,Toast.LENGTH_SHORT).show();
                }
                else {
                    editPassword.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                String string = "";
                if(!editPassword.getText().toString().matches("defalutpass"))
                    string = editPassword.getText().toString();

                if(editType.getSelectedItem().toString().matches("Mesero")) {
                    if(editName.getText().toString().matches("") ||
                            !isValidEmail(editEmail.getText().toString()) ||
                            firstLastname.getText().toString().matches("") ||
                            secondLastname.getText().toString().matches("")) {
                        if(editName.getText().toString().matches("")){
                            editEmail.setError("Completa este campos");
                        }
                        if(!isValidEmail(editEmail.getText().toString())) {
                            editEmail.setError("Escribe un email valido");
                        }
                        if(firstLastname.getText().toString().matches("")) {
                            firstLastname.setError("Complete este campo");
                        }
                        if(secondLastname.getText().toString().matches("")) {
                            secondLastname.setError("Complete este campo");
                        }
                    }
                    else {
                        update_data(new UserForm(editName.getText().toString(),
                                editEmail.getText().toString(),
                                firstLastname.getText().toString(),
                                secondLastname.getText().toString(),
                                string,
                                editType.getSelectedItem().toString()
                        ));
                    }
                }
                else {
                    if(editName.getText().toString().matches("") ||
                            !isValidEmail(editEmail.getText().toString()) ||
                            firstLastname.getText().toString().matches("") ||
                            secondLastname.getText().toString().matches("")) {
                        if(editName.getText().toString().matches("")){
                            editEmail.setError("Completa este campos");
                        }
                        if(!isValidEmail(editEmail.getText().toString())) {
                            editEmail.setError("Escribe un email valido");
                        }
                        if(firstLastname.getText().toString().matches("")) {
                            firstLastname.setError("Complete este campo");
                        }
                        if(secondLastname.getText().toString().matches("")) {
                            secondLastname.setError("Complete este campo");
                        }
                        if(!validatePassword()){
                            editPassword.setError("La contraseña debe tener al menos 6 caracteres");
                        }
                    }
                    else {
                        update_data(new UserForm(editName.getText().toString(),
                                editEmail.getText().toString(),
                                firstLastname.getText().toString(),
                                secondLastname.getText().toString(),
                                string,
                                editType.getSelectedItem().toString()
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
                Intent intent = new Intent(UsersEdit.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
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
                    firstLastname.setText(user.getFirstLastname());
                    secondLastname.setText(user.getSecondLastname());
                    editEmail.setText(user.getEmail());
                    editType.setSelection(adapter.getPosition(user.getRole()));
                }
                else if (response.code() == 401) {
                    Toast.makeText(UsersEdit.this, "El correo ya existe", Toast.LENGTH_SHORT).show();
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
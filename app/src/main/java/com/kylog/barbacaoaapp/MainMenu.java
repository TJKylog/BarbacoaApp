package com.kylog.barbacaoaapp;

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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.activities.catalogs.CatalogsActivity;
import com.kylog.barbacaoaapp.activities.events.EventsActivity;
import com.kylog.barbacaoaapp.activities.expenses.ExpensesActivity;
import com.kylog.barbacaoaapp.activities.notes.NotesActivity;
import com.kylog.barbacaoaapp.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenu extends AppCompatActivity {

    private SharedPreferences pref;
    private TextView user_name;
    private Button products_button, events_button;
    private ImageButton notes_button;
    private Button expenses_button;
    private ImageButton userActionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        products_button = findViewById(R.id.productos_button);
        notes_button = findViewById(R.id.consumos_button);
        userActionsButton = findViewById(R.id.user_actions_button);
        user_name = findViewById(R.id.user_name_view);
        expenses_button = findViewById(R.id.egresos_button);
        events_button = findViewById(R.id.envetos_button);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

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

        products_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_products();
            }
        });

        notes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_notes();
            }
        });

        expenses_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_expenses();
            }
        });

        events_button.setOnClickListener(v -> { show_events(); });

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        getUser();
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
                            pref.edit().clear().apply();
                            Intent intent = new Intent(MainMenu.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(MainMenu.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                    }
                });
                Toast.makeText(MainMenu.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void getUser(){

        AppCustomService service = RetrofitClient.getClient();

        Call<User> userCall = service.user(getTokenType()+" "+getToken());

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    user_name.setText(user.getName());
                    if(getUseName()==null && getUserEmail() == null)
                        saveOnPreferences(user.getName(),user.getEmail());
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
                Toast.makeText(MainMenu.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveOnPreferences(String user_name, String user_email) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_name",user_name);
        editor.putString("user_email",user_email);
        editor.apply();
    }

    private void show_expenses() {
        Intent intent = new Intent(MainMenu.this , ExpensesActivity.class);
        startActivity(intent);
    }

    private void show_products()
    {
        Intent intent = new Intent(MainMenu.this , CatalogsActivity.class);
        startActivity(intent);
    }

    private void show_notes() {
        Intent intent = new Intent(MainMenu.this, NotesActivity.class);
        startActivity(intent);
    }

    private void show_events(){
        Intent intent = new Intent(MainMenu.this, EventsActivity.class);
        startActivity(intent);
    }

    private String getUserEmail(){
        return pref.getString("user_email", null);
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
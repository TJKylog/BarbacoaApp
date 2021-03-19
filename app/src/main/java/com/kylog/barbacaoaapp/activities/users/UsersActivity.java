package com.kylog.barbacaoaapp.activities.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.MainMenu;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.User;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private List<User> users;
    private ListView list_user_view;
    private UserAdapter userAdapter;
    private FloatingActionButton add_user_button_1;
    private ImageButton userActionsButton,backButton, mainMenu;
    private TextView user_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        list_user_view = findViewById(R.id.user_list_labels);
        add_user_button_1 = findViewById(R.id.add_user_button);
        mainMenu = findViewById(R.id.to_main_menu_view);

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        getUsers();

        add_user_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_user();
            }
        });

        registerForContextMenu(list_user_view);

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
                Toast.makeText(UsersActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(users.get(info.position).getName());
        inflater.inflate(R.menu.context_menu, menu);
    }

    private void add_user() {
        Intent intent = new Intent(UsersActivity.this, UsersCreate.class);
        startActivity(intent);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Integer id;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        id = this.users.get(info.position).getId();
        String name = this.users.get(info.position).getName();
        switch (item.getItemId()){
            case R.id.delete_option:
            {
                if(id != 1 && id != getUserId())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UsersActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.delete_active_mesa_dialog, null);
                    TextView title = (TextView) getLayoutInflater().inflate(R.layout.title_dialog,null);
                    title.setText("Eliminar usuario");

                    builder.setView(view).setCustomTitle(title);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    TextView mesa_name = view.findViewById(R.id.delete_active_mesa_name);
                    Button cancel = view.findViewById(R.id.cancel_button_delete_product);
                    Button save = view.findViewById(R.id.save_button_delete_product);

                    mesa_name.setText("¿Desea eliminar el usuario \""+name+"\" ?");

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppCustomService service = RetrofitClient.getClient();
                            Call<ResponseBody> deleteResponse = service.delete_user(getTokenType()+" "+getToken(), id);
                            deleteResponse.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()) {
                                        Toast.makeText(UsersActivity.this, "Usuario eliminado" , Toast.LENGTH_LONG).show();
                                        users.remove(info.position);
                                        userAdapter.notifyDataSetChanged();
                                    }
                                    else {
                                        Toast.makeText(UsersActivity.this, "Ocurrio un error al eliminar el usuario" , Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(UsersActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
                else {
                    Toast.makeText(UsersActivity.this, "No puedes eliminar este usuario", Toast.LENGTH_LONG).show();
                }

                return true;
            }
            case R.id.edit_option:{
                Intent intent = new  Intent(UsersActivity.this, UsersEdit.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
            default:
                return super.onContextItemSelected(item);
        }
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
                            Toast.makeText(UsersActivity.this, "Cerrando sesión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(UsersActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(UsersActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private Integer getUserId(){
        return Integer.parseInt(pref.getString("user_id", null));
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
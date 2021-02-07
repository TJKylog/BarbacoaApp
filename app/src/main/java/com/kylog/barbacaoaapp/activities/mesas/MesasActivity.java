package com.kylog.barbacaoaapp.activities.mesas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.expenses.ExpensesActivity;
import com.kylog.barbacaoaapp.activities.products.ProductsActivity;
import com.kylog.barbacaoaapp.models.Mesa;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesasActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private List<Mesa> mesas;
    private MesaAdapter adapter;
    private ListView mesas_list_view;
    private FloatingActionButton create_mesa;
    private ImageButton userActionsButton,backButton;
    private TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);

        mesas_list_view = findViewById(R.id.list_mesas_labels);
        create_mesa = findViewById(R.id.float_button_create_mesa);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        get_mesas();

        create_mesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MesasActivity.this, MesasCreate.class);
                startActivity(intent);
            }
        });
        registerForContextMenu(mesas_list_view);

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
                            Toast.makeText(MesasActivity.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(MesasActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(MesasActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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

    private void get_mesas(){
        AppCustomService service = RetrofitClient.getClient();

        Call<List<Mesa>> mesaCall = service.mesas(getTokenType()+" "+getToken());

        mesaCall.enqueue(new Callback<List<Mesa>>() {
            @Override
            public void onResponse(Call<List<Mesa>> call, Response<List<Mesa>> response) {
                if(response.isSuccessful()) {
                    mesas = new ArrayList<Mesa>();
                    mesas = (List<Mesa>) response.body();

                    adapter = new MesaAdapter(MesasActivity.this, 0 , (ArrayList<Mesa>) mesas);
                    mesas_list_view.setAdapter(adapter);

                }
                else {
                    response.errorBody();
                    pref.edit().clear().apply();
                    Intent intent = new Intent(MesasActivity.this , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Mesa>> call, Throwable t) {
                Toast.makeText(MesasActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.mesas.get(info.position).getId().toString()+" "+mesas.get(info.position).getName());
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Integer id;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        id = this.mesas.get(info.position).getId();
        String name = this.mesas.get(info.position).getName();
        switch (item.getItemId()) {
            case R.id.delete_option: {
                final CharSequence [] options = {"Eliminar", "Cancelar"};
                final AlertDialog.Builder alertDelete = new AlertDialog.Builder(MesasActivity.this);
                alertDelete.setTitle("Desea eliminar: "+name);
                alertDelete.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(options[which].equals("Eliminar")) {
                            AppCustomService service = RetrofitClient.getClient();
                            Call<ResponseBody> responseBodyCall = service.delete_mesa(getTokenType()+" "+getToken(), id);
                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()) {
                                        Toast.makeText(MesasActivity.this, "Mesa eliminada: "+ name , Toast.LENGTH_LONG).show();
                                        mesas.remove(info.position);
                                        adapter.notifyDataSetChanged();
                                    }
                                    else {
                                        Toast.makeText(MesasActivity.this, "Se produjo un error al eliminar: "+ name , Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(MesasActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                        else {
                            dialog.dismiss();
                        }
                    }
                });
                alertDelete.show();
                return true;
            }
            case R.id.edit_option: {
                Intent intent = new Intent(MesasActivity.this, MesasEdit.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
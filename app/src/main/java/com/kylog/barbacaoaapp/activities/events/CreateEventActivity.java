package com.kylog.barbacaoaapp.activities.events;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.MainMenu;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.expenses.ExpensesActivity;
import com.kylog.barbacaoaapp.models.BasicPackage;
import com.kylog.barbacaoaapp.models.Others;
import com.kylog.barbacaoaapp.models.forms.Event;
import com.kylog.barbacaoaapp.models.forms.EventInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private ImageButton userActionsButton,backButton, mainMenu;
    private Button save_event;
    private TextView user_name;
    private List<BasicPackage> basicPackageList;
    private List<Others> othersList;
    private RecyclerView basicPackageR, extrasList;
    private BasicAdapter basicAdapter;
    private OthersAdapter othersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);
        mainMenu = findViewById(R.id.to_main_menu_view);
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

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        basicPackageR = findViewById(R.id.basic_package_list);
        extrasList = findViewById(R.id.extras_list);
        save_event = findViewById(R.id.save_event_button);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        basicPackageR.setLayoutManager(linearLayoutManager);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        extrasList.setLayoutManager(layoutManager1);

        othersList = new ArrayList<Others>();
        othersList.add(new Others(1.0,200.0,"Personas"));

        othersAdapter = new OthersAdapter(othersList, R.layout.item_package_list, new OthersAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Others others, int position) {
                Toast.makeText(CreateEventActivity.this, others.getName(), Toast.LENGTH_SHORT).show();
            }
        }, CreateEventActivity.this);
        extrasList.setAdapter(othersAdapter);

        basicPackageList = new ArrayList<BasicPackage>();
        basicPackageList.add(new BasicPackage(1.0,200.0,"Personas"));
        basicPackageList.add(new BasicPackage(1.0,200.0,"Borrego"));
        basicPackageList.add(new BasicPackage(1.0,200.0,"Consomé con garbanzo (guarniciones)"));
        basicPackageList.add(new BasicPackage(1.0,200.0,"Arroz"));
        basicPackageList.add(new BasicPackage(1.0,200.0,"Tortillas a mano"));

        basicAdapter = new BasicAdapter(basicPackageList, R.layout.item_package_list, new BasicAdapter.onItemClickListener() {
            @Override
            public void onItemClick(BasicPackage basicPackage, int position) {
                Toast.makeText(CreateEventActivity.this, basicPackage.getName(), Toast.LENGTH_SHORT).show();
            }
        }, CreateEventActivity.this);

        save_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_event();
            }
        });

        basicPackageR.setAdapter(basicAdapter);

    }

    private void save_event() {
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.save_event(getTokenType()+" "+getToken(),
                new Event(
                        new EventInfo(othersList,
                                basicPackageList,
                                10.00,
                                10.00,
                                "name",
                                "Calle",
                                "2211673238",
                                "05/03/2021",
                                "10:00")
                )
        );

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(CreateEventActivity.this, "se guardo" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, "Error" , Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CreateEventActivity.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(CreateEventActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(CreateEventActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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

    public String authToken() {
        return getTokenType()+" "+getToken();
    }

}
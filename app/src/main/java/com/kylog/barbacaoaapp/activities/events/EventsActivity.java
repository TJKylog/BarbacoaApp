package com.kylog.barbacaoaapp.activities.events;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.MainMenu;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.BasicPackage;
import com.kylog.barbacaoaapp.models.forms.Event;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsActivity extends AppCompatActivity {

    private Button newEvent;
    private SharedPreferences pref;
    private ImageButton userActionsButton,backButton, mainMenu;
    private TextView user_name, actionView;
    private RecyclerView events_list;
    private List<Event> events;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);
        mainMenu = findViewById(R.id.to_main_menu_view);
        user_name = findViewById(R.id.user_name_view);
        actionView = findViewById(R.id.expenses_action);
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
                Intent intent = new Intent(EventsActivity.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        newEvent = findViewById(R.id.add_event_button);
        events_list = findViewById(R.id.list_events);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        events_list.setLayoutManager(linearLayoutManager);

        events = new ArrayList<Event>();
        eventAdapter = new EventAdapter(events, R.layout.item_event_list, new EventAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Event event, int position) {
                Toast.makeText(EventsActivity.this, event.getId().toString(), Toast.LENGTH_SHORT).show();
            }
        }, this);
        events_list.setAdapter(eventAdapter);

        newEvent.setOnClickListener(v -> {
            Intent intent = new Intent(EventsActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        getEvents();

    }

    public void getEvents(){
        AppCustomService service = RetrofitClient.getClient();
        Call<List<Event>> callEvent = service.get_events(getTokenType()+" "+getToken());
        callEvent.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if(response.isSuccessful())
                {
                    events = response.body();
                    eventAdapter.updateList(events);
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(EventsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
                            Toast.makeText(EventsActivity.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(EventsActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(EventsActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
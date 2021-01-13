package com.kylog.barbacaoaapp.activities.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.ActiveMesa;
import com.kylog.barbacaoaapp.models.DataAvailable;
import com.kylog.barbacaoaapp.models.ProductType;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private RecyclerView typesList;
    private TypesAdapter typesAdapter;
    private ActiveAdapter activeAdapter;
    private ArrayList<ProductType> productTypes;
    private ArrayList<ActiveMesa> activeMesas;
    private RecyclerView activeslist;
    private DataAvailable dataAvailable;
    private Button add_active_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        typesList = findViewById(R.id.list_types);
        activeslist = findViewById(R.id.active_list);
        add_active_button = findViewById(R.id.add_active_button);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typesList.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        activeslist.setLayoutManager(layoutManager2);

        add_active_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddActive();
            }
        });

        get_types();
        get_active();

    }

    private void get_types() {
        AppCustomService service = RetrofitClient.getClient();
        Call<List<ProductType>> typescall = service.get_types(getTokenType()+" "+getToken());

        typescall.enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(Call<List<ProductType>> call, Response<List<ProductType>> response) {
                if(response.isSuccessful()) {
                    productTypes = (ArrayList<ProductType>) response.body();
                    if (productTypes.isEmpty()) {

                    }
                    else {
                        typesAdapter = new TypesAdapter(productTypes, R.layout.types_item_list_layout, new TypesAdapter.onItemClickListener() {
                            @Override
                            public void onItemClick(ProductType productType, int position) {
                                Toast.makeText(NotesActivity.this, productType.getType(), Toast.LENGTH_LONG).show();
                            }
                        });
                        typesList.setAdapter(typesAdapter);
                    }

                }
                else {

                }
            }

            @Override
            public void onFailure(Call<List<ProductType>> call, Throwable t) {

            }
        });
    }

    private void get_active() {
        AppCustomService service = RetrofitClient.getClient();
        Call<List<ActiveMesa>> activesCall = service.get_active(getTokenType()+" "+getToken());

        activesCall.enqueue(new Callback<List<ActiveMesa>>() {
            @Override
            public void onResponse(Call<List<ActiveMesa>> call, Response<List<ActiveMesa>> response) {
                if(response.isSuccessful()){
                    activeMesas = (ArrayList<ActiveMesa>) response.body();
                    activeAdapter = new ActiveAdapter(activeMesas, R.layout.mesas_actives_layout, new ActiveAdapter.itemClickListener() {
                        @Override
                        public void onItemClick(ActiveMesa activeMesa, int position) {
                            Toast.makeText(NotesActivity.this, activeMesa.getName(), Toast.LENGTH_LONG).show();
                        }
                    });
                    activeslist.setAdapter(activeAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ActiveMesa>> call, Throwable t) {

            }
        });
    }

    private void showAddActive(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.add_mesa, null);
        final Spinner spinnerMesas = v.findViewById(R.id.spinner_mesas);
        final Spinner spinnerWaiters = v.findViewById(R.id.spinner_waiters);

        builder.setView(v)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AppCustomService service = RetrofitClient.getClient();
        Call<DataAvailable> dataAvailableCall = service.get_data_available(getTokenType()+" "+getToken());

        dataAvailableCall.enqueue(new Callback<DataAvailable>() {
            @Override
            public void onResponse(Call<DataAvailable> call, Response<DataAvailable> response) {
                if(response.isSuccessful()) {
                    dataAvailable = response.body();
                    ArrayAdapter spinnerAdapterMesas =  new ArrayAdapter( v.getContext() ,R.layout.support_simple_spinner_dropdown_item, dataAvailable.getMesas());
                    spinnerMesas.setAdapter(spinnerAdapterMesas);
                    ArrayAdapter spinnerAdapterWaiters = new ArrayAdapter(v.getContext(),R.layout.support_simple_spinner_dropdown_item , dataAvailable.getWaiters());
                    spinnerWaiters.setAdapter(spinnerAdapterWaiters);
                }
            }

            @Override
            public void onFailure(Call<DataAvailable> call, Throwable t) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
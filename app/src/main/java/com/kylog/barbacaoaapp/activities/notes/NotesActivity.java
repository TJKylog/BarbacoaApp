package com.kylog.barbacaoaapp.activities.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private Spinner spinnerMesas;
    private Spinner spinnerWaiters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        typesList = findViewById(R.id.list_types);
        activeslist = findViewById(R.id.active_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typesList.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        activeslist.setLayoutManager(layoutManager2);


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
        View v = inflater.inflate(R.layout.add_mesa, null);
        builder.setView(v);

        AlertDialog dialog = builder.create();
        dialog.show();
        spinnerMesas = findViewById(R.id.spinner_mesas);
        spinnerWaiters = findViewById(R.id.spinner_waiters);

        get_available_data();
    }

    private void get_available_data() {
        AppCustomService service = RetrofitClient.getClient();
        Call<DataAvailable> dataAvailableCall = service.get_data_available(getTokenType()+" "+getToken());

        dataAvailableCall.enqueue(new Callback<DataAvailable>() {
            @Override
            public void onResponse(Call<DataAvailable> call, Response<DataAvailable> response) {
                if(response.isSuccessful()) {
                    dataAvailable = response.body();
                    //ArrayAdapter spinnerAdapterMesas =  new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item , dataAvailable.getMesas());
                }
            }

            @Override
            public void onFailure(Call<DataAvailable> call, Throwable t) {

            }
        });
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
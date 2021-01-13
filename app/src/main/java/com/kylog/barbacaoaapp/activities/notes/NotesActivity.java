package com.kylog.barbacaoaapp.activities.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import com.kylog.barbacaoaapp.models.Mesa;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.ProductType;
import com.kylog.barbacaoaapp.models.Waiter;
import com.kylog.barbacaoaapp.models.forms.FormActive;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
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
    private Spinner spinnerMesas;
    private Spinner spinnerWaiters;
    private ProductsAdapter productsAdapter;
    private List<Product> products;
    private RecyclerView productsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        typesList = findViewById(R.id.list_types);
        activeslist = findViewById(R.id.active_list);
        add_active_button = findViewById(R.id.add_active_button);
        productsGrid = findViewById(R.id.products_grid);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typesList.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        activeslist.setLayoutManager(layoutManager2);
        LinearLayoutManager layoutManager3 = new GridLayoutManager(this,2);
        productsGrid.setLayoutManager(layoutManager3);

        add_active_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddActive();
            }
        });

        get_types();
        get_active();

    }

    private void get_products_by_type(String type) {
        AppCustomService service = RetrofitClient.getClient();
        Call<List<Product>> listCall = service.get_products_by_type(getTokenType()+" "+getToken(), type );

        listCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()) {
                    products = response.body();
                    productsAdapter = new ProductsAdapter(products, R.layout.grid_item_produts_layout, new ProductsAdapter.itemClickListener() {
                        @Override
                        public void onItemClick(Product product, int position) {
                            Toast.makeText(NotesActivity.this, product.getName(), Toast.LENGTH_LONG).show();
                        }
                    });
                    productsGrid.setAdapter(productsAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
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
                                get_products_by_type(productType.getType());
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
        spinnerMesas = v.findViewById(R.id.spinner_mesas);
        spinnerWaiters = v.findViewById(R.id.spinner_waiters);

        builder.setView(v)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Mesa mesa = (Mesa) spinnerMesas.getSelectedItem();
                        final Waiter waiter = (Waiter) spinnerWaiters.getSelectedItem();
                        add_active(new FormActive(waiter.getId(),mesa.getId()));
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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

    private void add_active(FormActive formActive) {
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.add_active(getTokenType()+" "+getToken(), formActive);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    get_active();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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
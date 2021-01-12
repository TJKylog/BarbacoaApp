package com.kylog.barbacaoaapp.activities.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
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
    private ArrayList<ProductType> productTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        typesList = findViewById(R.id.list_types);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typesList.setLayoutManager(layoutManager);

        get_types();

    }

    private void get_types() {
        AppCustomService service = RetrofitClient.getClient();
        Call<List<ProductType>> typescall = service.get_types(getTokenType()+" "+getToken());

        typescall.enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(Call<List<ProductType>> call, Response<List<ProductType>> response) {
                if(response.isSuccessful()) {
                    productTypes = (ArrayList<ProductType>) response.body();
                    typesAdapter = new TypesAdapter(productTypes, R.layout.types_item_list_layout , new TypesAdapter.onItemClickListener() {
                        @Override
                        public void onItemClick(ProductType productType, int position) {
                            Toast.makeText(NotesActivity.this, productType.getType() , Toast.LENGTH_LONG).show();
                        }
                    });
                    typesList.setAdapter(typesAdapter);
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<List<ProductType>> call, Throwable t) {

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
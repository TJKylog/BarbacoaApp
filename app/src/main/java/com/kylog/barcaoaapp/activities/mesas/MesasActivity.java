package com.kylog.barcaoaapp.activities.mesas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.kylog.barcaoaapp.AppCustomService;
import com.kylog.barcaoaapp.MainActivity;
import com.kylog.barcaoaapp.R;
import com.kylog.barcaoaapp.RetrofitClient;
import com.kylog.barcaoaapp.activities.products.ProductsActivity;
import com.kylog.barcaoaapp.models.Mesa;
import com.kylog.barcaoaapp.models.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesasActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private List<Mesa> mesas;
    private MesaAdapter adapter;
    private ListView mesas_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);

        mesas_list_view = findViewById(R.id.list_mesas_labels);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        get_mesas();

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
                Toast.makeText(MesasActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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
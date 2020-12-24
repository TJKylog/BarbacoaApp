package com.kylog.barcaoaapp.activities.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.kylog.barcaoaapp.AppCustomService;
import com.kylog.barcaoaapp.MainActivity;
import com.kylog.barcaoaapp.MainMenu;
import com.kylog.barcaoaapp.R;
import com.kylog.barcaoaapp.RetrofitClient;
import com.kylog.barcaoaapp.models.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private ListView list_products_view;
    private List<Product> products;
    private ProductAdapter adbPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        list_products_view = findViewById(R.id.list_products_labels);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        get_products();
    }

    private void get_products(){
        AppCustomService service = RetrofitClient.getClient();

        Call<List<Product>> productCall = service.products(getTokenType()+" "+getToken());

        productCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductsActivity.this, "Productos obtenidos" , Toast.LENGTH_LONG).show();
                    products = new ArrayList<Product>();

                    products = (List<Product>) response.body();

                    adbPerson = new ProductAdapter (ProductsActivity.this, 0, (ArrayList<Product>) products);
                    list_products_view.setAdapter(adbPerson);

                } else {
                    response.errorBody();
                    pref.edit().clear().apply();
                    Intent intent = new Intent(ProductsActivity.this , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
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


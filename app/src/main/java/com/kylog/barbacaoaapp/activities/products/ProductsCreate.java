package com.kylog.barbacaoaapp.activities.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.forms.NewProductForm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductsCreate extends AppCompatActivity {

    private SharedPreferences pref;
    private EditText editName;
    private EditText editPrice;
    private EditText editMeasure;
    private EditText editType;
    private Button save_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_create);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        editName = findViewById(R.id.product_name_field);
        editPrice = findViewById(R.id.product_price_field);
        editMeasure = findViewById(R.id.product_measure_field);
        editType = findViewById(R.id.product_type_field);
        save_product = findViewById(R.id.save_product_button);

        save_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_product(new NewProductForm(editName.getText().toString() , Double.parseDouble(String.valueOf(editPrice.getText())), editMeasure.getText().toString(), editType.getText().toString()));
            }
        });
    }

    private void save_product(NewProductForm newProductForm) {
        AppCustomService service = RetrofitClient.getClient();

        Call<Product> authCall = service.create_product( getTokenType()+" "+getToken() , newProductForm);

        authCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProductsCreate.this, "Se guardó el producto correctamente", Toast.LENGTH_LONG).show();
                    Product product = response.body();
                    Intent intent = new Intent(ProductsCreate.this , ProductsActivity.class);
                    startActivity(intent);
                } else {
                    response.errorBody(); // do something with that
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductsCreate.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
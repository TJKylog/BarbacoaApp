package com.kylog.barbacaoaapp.activities.products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.forms.NewProductForm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsEdit extends AppCompatActivity {

    private SharedPreferences pref;
    private Integer id;
    private TextView editingProduct;
    private EditText editName;
    private EditText editPrice;
    private EditText editType;
    private EditText editMeasure;
    private Button updateProductButton;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_edit);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        editingProduct = findViewById(R.id.edit_product_view_id);
        editName = findViewById(R.id.edit_product_name_field);
        editPrice = findViewById(R.id.edit_product_price_field);
        editType = findViewById(R.id.edit_product_type_field);
        editMeasure = findViewById(R.id.edit_product_measure_field);
        updateProductButton = findViewById(R.id.update_product_button);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            id = bundle.getInt("id");
            getProduct();
            Toast.makeText(ProductsEdit.this, "id: "+ id, Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(ProductsEdit.this, "xD" , Toast.LENGTH_LONG).show();
        }

        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct();
            }
        });

    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }

    private void getProduct(){
        AppCustomService service = RetrofitClient.getClient();

        Call<Product> productCall = service.product(getTokenType()+" "+getToken(),id);

        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    product = response.body();
                    editingProduct.setText("Editar: "+product.getName());
                    editName.setText(product.getName());
                    editPrice.setText(product.getPrice().toString());
                    editType.setText(product.getType());
                    editMeasure.setText(product.getMeasure());
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });
    }

    private void updateProduct(){
        AppCustomService service = RetrofitClient.getClient();

        Call<Product> productCall = service.update_product(getTokenType()+" "+getToken(),id,new NewProductForm(editName.getText().toString() , Double.parseDouble(String.valueOf(editPrice.getText())), editMeasure.getText().toString(), editType.getText().toString()));

        productCall.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(ProductsEdit.this, "Producto actualizado" , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ProductsEdit.this , ProductsActivity.class);
                    startActivity(intent);
                }
                else{

                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });
    }

}
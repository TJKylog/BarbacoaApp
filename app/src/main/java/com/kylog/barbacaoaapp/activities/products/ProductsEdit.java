package com.kylog.barbacaoaapp.activities.products;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.MainMenu;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.catalogs.CatalogsActivity;
import com.kylog.barbacaoaapp.activities.expenses.ExpensesActivity;
import com.kylog.barbacaoaapp.activities.mesas.MesasEdit;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.forms.NewProductForm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsEdit extends AppCompatActivity {

    private SharedPreferences pref;
    private Integer id;
    private TextView editingProduct;
    private EditText editName;
    private EditText editPrice;
    private Spinner editType;
    private Spinner editMeasure;
    private Button updateProductButton;
    private Product product;
    private ImageButton userActionsButton,backButton,mainMenu;
    private TextView user_name;
    private ArrayAdapter<CharSequence> adapterMeasure,adapterType;

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

        adapterType = ArrayAdapter.createFromResource(this,
                R.array.product_types_array, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editType.setAdapter(adapterType);

        adapterMeasure = ArrayAdapter.createFromResource(this,
                R.array.product_measures_array, android.R.layout.simple_spinner_item);
        adapterMeasure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editMeasure.setAdapter(adapterMeasure);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            id = bundle.getInt("id");
            getProduct();
            Toast.makeText(ProductsEdit.this, "id: "+ id, Toast.LENGTH_LONG).show();
        }

        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editName.getText().toString().matches("") || editPrice.getText().toString().matches("")) {
                    if(editName.getText().toString().matches("")) {
                        editName.setError("Completa este campo");
                    }
                    if(editPrice.getText().toString().matches("")) {
                        editPrice.setError("Completa este campo");
                    }
                }
                else {
                    updateProduct();
                }
            }
        });

        userActionsButton = findViewById(R.id.user_actions_button);
        mainMenu = findViewById(R.id.to_main_menu_view);
        backButton = findViewById(R.id.back_button);
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
                Intent intent = new Intent(ProductsEdit.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

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
                    editType.setSelection(adapterType.getPosition(product.getType()));
                    editMeasure.setSelection(adapterMeasure.getPosition(product.getMeasure()));
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

        Call<Product> productCall = service.update_product(getTokenType()+" "+getToken(),id,new NewProductForm(editName.getText().toString() , Double.parseDouble(String.valueOf(editPrice.getText())), editMeasure.getSelectedItem().toString(), editType.getSelectedItem().toString()));

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
                    Toast.makeText(ProductsEdit.this, "Ocurrio un error al guardar" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductsEdit.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(ProductsEdit.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(ProductsEdit.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ProductsEdit.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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

}
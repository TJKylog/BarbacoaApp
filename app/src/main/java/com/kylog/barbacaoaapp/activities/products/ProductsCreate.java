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
import com.kylog.barbacaoaapp.models.Exist;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.forms.NewMesaForm;
import com.kylog.barbacaoaapp.models.forms.NewProductForm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductsCreate extends AppCompatActivity {

    private SharedPreferences pref;
    private EditText editName;
    private EditText editPrice;
    private Spinner editMeasure;
    private Spinner editType;
    private Button save_product;
    private ImageButton userActionsButton,backButton, mainMenu;
    private TextView user_name;
    private ArrayAdapter<CharSequence> adapterMeasure,adapterType;
    private Exist exist = null;

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


        adapterType = ArrayAdapter.createFromResource(this,
                R.array.product_types_array, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editType.setAdapter(adapterType);

        adapterMeasure = ArrayAdapter.createFromResource(this,
                R.array.product_measures_array, android.R.layout.simple_spinner_item);
        adapterMeasure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editMeasure.setAdapter(adapterMeasure);

        editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    verify_name(editName.getText().toString());
                }
            }
        });

        save_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exist != null)
                {
                    if(editName.getText().toString().matches("") || editPrice.getText().toString().matches("") || !exist.getExist()) {
                        if(!exist.getExist()) {
                            editName.setError("Este producto ya existe");
                        }
                        if(editName.getText().toString().matches("")) {
                            editName.setError("Completa este campo");
                        }
                        if(editPrice.getText().toString().matches("")) {
                            editPrice.setError("Completa este campo");
                        }
                    }
                    else {
                        save_product(new NewProductForm(editName.getText().toString() , Double.parseDouble(String.valueOf(editPrice.getText())), editMeasure.getSelectedItem().toString(), editType.getSelectedItem().toString()));
                    }
                }
                else {
                    if(editName.getText().toString().matches("") || editPrice.getText().toString().matches("")) {
                        if(editName.getText().toString().matches("")) {
                            editName.setError("Completa este campo");
                        }
                        if(editPrice.getText().toString().matches("")) {
                            editPrice.setError("Completa este campo");
                        }
                    }
                    else {
                        save_product(new NewProductForm(editName.getText().toString() , Double.parseDouble(String.valueOf(editPrice.getText())), editMeasure.getSelectedItem().toString(), editType.getSelectedItem().toString()));
                    }
                }
            }
        });

        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);
        user_name = findViewById(R.id.user_name_view);
        mainMenu = findViewById(R.id.to_main_menu_view);
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
                Intent intent = new Intent(ProductsCreate.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    public void verify_name(String name){
        AppCustomService service = RetrofitClient.getClient();
        Call<Exist> booleanCall = service.verify(getTokenType()+" "+getToken(), new NewMesaForm(name));
        booleanCall.enqueue(new Callback<Exist>() {
            @Override
            public void onResponse(Call<Exist> call, Response<Exist> response) {
                if(response.isSuccessful()) {
                    exist = response.body();

                    if(exist.getExist())
                    {
                        editName.setError("Este producto ya existe");
                    }
                }
            }

            @Override
            public void onFailure(Call<Exist> call, Throwable t) {
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
                    Toast.makeText(ProductsCreate.this, "Ya existe el producto", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(ProductsCreate.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(ProductsCreate.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(ProductsCreate.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ProductsCreate.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
}
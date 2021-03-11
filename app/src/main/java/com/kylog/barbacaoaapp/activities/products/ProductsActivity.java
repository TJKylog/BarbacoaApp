package com.kylog.barbacaoaapp.activities.products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.MainMenu;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.users.UsersActivity;
import com.kylog.barbacaoaapp.models.Product;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private ListView list_products_view;
    private List<Product> products;
    private ProductAdapter adbPerson;
    private FloatingActionButton add_product_button_1;
    private ImageButton userActionsButton,backButton, mainMenu;
    private TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        list_products_view = findViewById(R.id.list_products_labels);
        add_product_button_1 = findViewById(R.id.add_product_button);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        get_products();

        add_product_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductsActivity.this, ProductsCreate.class);
                startActivity(intent);
            }
        });

        registerForContextMenu(list_products_view);

        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);
        user_name = findViewById(R.id.user_name_view);
        user_name.setText(getUseName());
        mainMenu = findViewById(R.id.to_main_menu_view);

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductsActivity.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

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

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    private void get_products(){
        AppCustomService service = RetrofitClient.getClient();

        Call<List<Product>> productCall = service.products(getTokenType()+" "+getToken());

        productCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
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
                Toast.makeText(ProductsActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(ProductsActivity.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(ProductsActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ProductsActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(products.get(info.position).getName());
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Integer id;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        id = this.products.get(info.position).getId();
        String name = this.products.get(info.position).getName();
        switch (item.getItemId()){
            case R.id.delete_option:
            {
                if(id != 1)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductsActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.delete_active_mesa_dialog, null);
                    builder.setView(view).setTitle("Eliminar mesa");
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    TextView mesa_name = view.findViewById(R.id.delete_active_mesa_name);
                    Button cancel = view.findViewById(R.id.cancel_button_delete_product);
                    Button save = view.findViewById(R.id.save_button_delete_product);
                    mesa_name.setText("¿Desea eliminar el producto \""+name+"\" ?");

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppCustomService service = RetrofitClient.getClient();
                            Call<ResponseBody> deleteResponse = service.delete_product(getTokenType()+" "+getToken(), id);
                            deleteResponse.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()) {
                                        Toast.makeText(ProductsActivity.this, "Producto eliminado: "+ name , Toast.LENGTH_LONG).show();
                                        products.remove(info.position);
                                        adbPerson.notifyDataSetChanged();
                                    }
                                    else {
                                        Toast.makeText(ProductsActivity.this, "No se completo la acción: "+ name , Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(ProductsActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
                else {
                    Toast.makeText(ProductsActivity.this, "No se puedes eliminar este producto", Toast.LENGTH_LONG).show();
                }

                return true;
            }
            case R.id.edit_option:{
                if(id != 1) {
                    Intent intent = new  Intent(ProductsActivity.this,ProductsEdit.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ProductsActivity.this, "No se puedes editar este producto", Toast.LENGTH_LONG).show();
                }
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

}
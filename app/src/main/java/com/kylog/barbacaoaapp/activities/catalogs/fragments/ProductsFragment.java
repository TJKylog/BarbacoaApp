package com.kylog.barbacaoaapp.activities.catalogs.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.products.ProductAdapter;
import com.kylog.barbacaoaapp.activities.products.ProductsCreate;
import com.kylog.barbacaoaapp.activities.products.ProductsEdit;
import com.kylog.barbacaoaapp.models.Product;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsFragment} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment {

    private SharedPreferences pref;
    private ListView list_products_view;
    private List<Product> products;
    private ProductAdapter adbPerson;
    private FloatingActionButton add_product_button_1;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_products, container, false);

        list_products_view = v.findViewById(R.id.list_products_labels);
        add_product_button_1 = v.findViewById(R.id.add_product_button);

        pref = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        get_products();

        add_product_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProductsCreate.class);
                startActivity(intent);
            }
        });

        registerForContextMenu(list_products_view);

        return v;
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

                    adbPerson = new ProductAdapter (getActivity(), 0, (ArrayList<Product>) products);
                    list_products_view.setAdapter(adbPerson);

                } else {
                    response.errorBody();
                    pref.edit().clear().apply();
                    Intent intent = new Intent(getContext() , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(getContext(), "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.products.get(info.position).getId().toString()+" "+products.get(info.position).getName());
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
                final CharSequence [] options = {"Eliminar","Cancelar"};
                final AlertDialog.Builder alertDelete = new AlertDialog.Builder(getContext());
                alertDelete.setTitle("Desea eliminar: "+name);
                alertDelete.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(options[which].equals("Eliminar")) {
                            AppCustomService service = RetrofitClient.getClient();
                            Call<ResponseBody> deleteResponse = service.delete_product(getTokenType()+" "+getToken(), id);
                            deleteResponse.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Producto eliminado: "+ name , Toast.LENGTH_LONG).show();
                                        products.remove(info.position);
                                        adbPerson.notifyDataSetChanged();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "No se completo la acción: "+ name , Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(getContext(), "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            dialog.dismiss();
                        }
                    }
                });
                alertDelete.show();
                return true;
            }
            case R.id.edit_option:{
                Intent intent = new  Intent(getContext(), ProductsEdit.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }
}
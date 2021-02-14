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
import com.kylog.barbacaoaapp.activities.mesas.MesaAdapter;
import com.kylog.barbacaoaapp.activities.mesas.MesasCreate;
import com.kylog.barbacaoaapp.activities.mesas.MesasEdit;
import com.kylog.barbacaoaapp.models.Mesa;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MesasFragment} factory method to
 * create an instance of this fragment.
 */
public class MesasFragment extends Fragment {

    private SharedPreferences pref;
    private List<Mesa> mesas;
    private MesaAdapter adapter;
    private ListView mesas_list_view;
    private FloatingActionButton create_mesa;

    public MesasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mesas, container, false);

        mesas_list_view = v.findViewById(R.id.list_mesas_labels);
        create_mesa = v.findViewById(R.id.float_button_create_mesa);

        pref = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        get_mesas();

        create_mesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MesasCreate.class);
                startActivity(intent);
            }
        });
        registerForContextMenu(mesas_list_view);

        return v;
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

                    adapter = new MesaAdapter(getActivity(), 0 , (ArrayList<Mesa>) mesas);
                    mesas_list_view.setAdapter(adapter);

                }
                else {
                    response.errorBody();
                    pref.edit().clear().apply();
                    Intent intent = new Intent(getContext() , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<Mesa>> call, Throwable t) {
                Toast.makeText(getContext(), "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.mesas.get(info.position).getId().toString()+" "+mesas.get(info.position).getName());
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Integer id;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        id = this.mesas.get(info.position).getId();
        String name = this.mesas.get(info.position).getName();
        switch (item.getItemId()) {
            case R.id.delete_option: {
                final CharSequence [] options = {"Eliminar", "Cancelar"};
                final AlertDialog.Builder alertDelete = new AlertDialog.Builder(getContext());
                alertDelete.setTitle("Desea eliminar: "+name);
                alertDelete.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(options[which].equals("Eliminar")) {
                            AppCustomService service = RetrofitClient.getClient();
                            Call<ResponseBody> responseBodyCall = service.delete_mesa(getTokenType()+" "+getToken(), id);
                            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Mesa eliminada: "+ name , Toast.LENGTH_LONG).show();
                                        mesas.remove(info.position);
                                        adapter.notifyDataSetChanged();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "Se produjo un error al eliminar: "+ name , Toast.LENGTH_LONG).show();
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
            case R.id.edit_option: {
                Intent intent = new Intent(getContext(), MesasEdit.class);
                intent.putExtra("id",id);
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
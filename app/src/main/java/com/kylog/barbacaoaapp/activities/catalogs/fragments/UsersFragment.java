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
import com.kylog.barbacaoaapp.activities.users.UserAdapter;
import com.kylog.barbacaoaapp.activities.users.UsersCreate;
import com.kylog.barbacaoaapp.activities.users.UsersEdit;
import com.kylog.barbacaoaapp.models.User;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    private SharedPreferences pref;
    private List<User> users;
    private ListView list_user_view;
    private UserAdapter userAdapter;
    private FloatingActionButton add_user_button_1;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_users, container, false);

        pref = this.getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        list_user_view = v.findViewById(R.id.user_list_labels);
        add_user_button_1 = v.findViewById(R.id.add_user_button);

        getUsers();

        add_user_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_user();
            }
        });

        registerForContextMenu(list_user_view);

        return v;
    }

    private void getUsers(){
        AppCustomService service = RetrofitClient.getClient();

        Call<List<User>> productCall = service.get_users(getTokenType()+" "+getToken());

        productCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    users = new ArrayList<User>();

                    users = response.body();
                    userAdapter = new UserAdapter(getActivity(), 0, (ArrayList<User>) users);
                    list_user_view.setAdapter(userAdapter);

                } else {
                    response.errorBody();
                    pref.edit().clear().apply();
                    Intent intent = new Intent(getContext() , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(this.users.get(info.position).getId().toString()+" "+users.get(info.position).getName());
        inflater.inflate(R.menu.context_menu, menu);
    }

    private void add_user() {
        Intent intent = new Intent(getContext(), UsersCreate.class);
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Integer id;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        id = this.users.get(info.position).getId();
        String name = this.users.get(info.position).getName();
        switch (item.getItemId()){
            case R.id.delete_option:
            {
                final CharSequence [] options = {"Eliminar","Cancelar"};
                final AlertDialog.Builder alertDelete = new AlertDialog.Builder(getContext());
                alertDelete.setTitle("Confirmar acción: " + name);
                alertDelete.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(options[which].equals("Eliminar")) {
                            AppCustomService service = RetrofitClient.getClient();
                            Call<ResponseBody> deleteResponse = service.delete_user(getTokenType()+" "+getToken(), id);
                            deleteResponse.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()) {
                                        Toast.makeText(getContext(), "Usuario eliminado" , Toast.LENGTH_LONG).show();
                                        users.remove(info.position);
                                        userAdapter.notifyDataSetChanged();
                                    }
                                    else {
                                        Toast.makeText(getContext(), "No se completo la acción" , Toast.LENGTH_LONG).show();
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
                Intent intent = new  Intent(getContext(), UsersEdit.class);
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
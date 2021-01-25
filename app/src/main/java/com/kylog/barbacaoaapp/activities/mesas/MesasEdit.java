package com.kylog.barbacaoaapp.activities.mesas;

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
import com.kylog.barbacaoaapp.activities.products.ProductsEdit;
import com.kylog.barbacaoaapp.models.Mesa;
import com.kylog.barbacaoaapp.models.forms.NewMesaForm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesasEdit extends AppCompatActivity {

    private SharedPreferences pref;
    private Integer id;
    private EditText edit_name;
    private Button update_mesa;
    private Mesa mesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas_edit);

        edit_name = findViewById(R.id.mesa_edit_name_field);
        update_mesa = findViewById(R.id.update_mesa_button);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            id = bundle.getInt("id");
            get_mesa();
            Toast.makeText(MesasEdit.this, "id: "+ id, Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(MesasEdit.this, "xD" , Toast.LENGTH_LONG).show();
        }

        update_mesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_mesa();
            }
        });
    }

    private void update_mesa() {
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.update_mesa(getTokenType()+" "+getToken(), id,new NewMesaForm(edit_name.getText().toString()));

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(MesasEdit.this, "Mesa actualizada", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MesasEdit.this, MesasActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void get_mesa() {
        AppCustomService service = RetrofitClient.getClient();
        Call<Mesa> mesaCall = service.get_mesa(getTokenType()+" "+getToken(), id);

        mesaCall.enqueue(new Callback<Mesa>() {
            @Override
            public void onResponse(Call<Mesa> call, Response<Mesa> response) {
                if(response.isSuccessful()) {
                    mesa = response.body();
                    edit_name.setText(mesa.getName());
                }
            }

            @Override
            public void onFailure(Call<Mesa> call, Throwable t) {

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
package com.kylog.barcaoaapp.activities.mesas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kylog.barcaoaapp.AppCustomService;
import com.kylog.barcaoaapp.R;
import com.kylog.barcaoaapp.RetrofitClient;
import com.kylog.barcaoaapp.activities.products.ProductsActivity;
import com.kylog.barcaoaapp.activities.products.ProductsCreate;
import com.kylog.barcaoaapp.models.Mesa;
import com.kylog.barcaoaapp.models.forms.NewMesaForm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MesasCreate extends AppCompatActivity {

    private SharedPreferences pref;
    private EditText edit_name;
    private Button save_mesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas_create);

        edit_name = findViewById(R.id.mesa_name_field);
        save_mesa = findViewById(R.id.save_mesa_button);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        save_mesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMesa(new NewMesaForm(edit_name.getText().toString()));
            }
        });
    }

    private void saveMesa(NewMesaForm newMesaForm) {
        AppCustomService service = RetrofitClient.getClient();
        Call<Mesa> mesaCall = service.create_mesa(getTokenType()+" "+getToken(), newMesaForm);

        mesaCall.enqueue(new Callback<Mesa>() {
            @Override
            public void onResponse(Call<Mesa> call, Response<Mesa> response) {
                if(response.isSuccessful())
                {
                    Mesa mesa = response.body();
                    Intent intent = new Intent(MesasCreate.this , MesasActivity.class);
                    startActivity(intent);
                }
                else{
                    response.errorBody();
                }
            }

            @Override
            public void onFailure(Call<Mesa> call, Throwable t) {
                Toast.makeText(MesasCreate.this, "Error", Toast.LENGTH_LONG).show();
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
package com.kylog.barbacaoaapp.activities.reset.password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.forms.SendEmailForm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailActivity extends AppCompatActivity {

    private EditText editEmail;
    private Button sendEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        editEmail = findViewById(R.id.reset_password_email_field);
        sendEmailButton = findViewById(R.id.send_email_reset_password);

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_email();
            }
        });
    }

    private void send_email(){
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody>  responseBodyCall = service.reset_password(new SendEmailForm(editEmail.getText().toString()));

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(EmailActivity.this, "Se envio el correo con el código de recupreación",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EmailActivity.this, CodeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(EmailActivity.this, "No se encontro el correo electornico",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EmailActivity.this, "No se pudo conectar con el servicio revise su conexión",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
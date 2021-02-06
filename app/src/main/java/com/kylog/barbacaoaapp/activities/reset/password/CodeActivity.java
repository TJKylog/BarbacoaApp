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
import com.kylog.barbacaoaapp.activities.products.ProductsEdit;
import com.kylog.barbacaoaapp.models.forms.CodeForm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeActivity extends AppCompatActivity {

    private EditText editCode;
    private Button sendCode;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);

        editCode = findViewById(R.id.reset_password_code_field);
        sendCode = findViewById(R.id.send_code_button);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            email = bundle.getString("email");
            Toast.makeText(CodeActivity.this, "email: "+ email, Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(CodeActivity.this, "xD" , Toast.LENGTH_LONG).show();
        }

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_code(editCode.getText().toString());
            }
        });
    }

    private void send_code(String code) {
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.send_code(new CodeForm(code));
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Intent intent = new Intent(CodeActivity.this,PasswordActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("code", code);
                    startActivity(intent);
                }
                else
                    Toast.makeText(CodeActivity.this, "", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CodeActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
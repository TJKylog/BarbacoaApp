package com.kylog.barbacaoaapp.activities.reset.password;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.forms.NewPasswordForm;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    private EditText editPassword, editRepeatPassword;
    private Button sendPasswordButton;
    private String email;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        editPassword = findViewById(R.id.reset_password_new_field);
        editRepeatPassword = findViewById(R.id.reset_password_repeat_field);
        sendPasswordButton = findViewById(R.id.save_new_password);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            email = bundle.getString("email");
            code = bundle.getString("code");
        } else{
            Toast.makeText(PasswordActivity.this, "error" , Toast.LENGTH_LONG).show();
        }

        sendPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !validatePassword() || !validatePassword2() ||  !editPassword.getText().toString().matches(editRepeatPassword.getText().toString()) )
                {
                    if(!validatePassword())
                        editPassword.setError("La contraseña debe tener al menos 6 caracteres");
                    if(!validatePassword2())
                        editRepeatPassword.setError("La contraseña debe tener al menos 6 caracteres");
                    if (!editPassword.getText().toString().matches(editRepeatPassword.getText().toString()))
                        Toast.makeText(PasswordActivity.this, "Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();
                }
                else{
                    send_new_password();
                }

            }
        });

    }

    private boolean validatePassword()
    {
        String password = editPassword.getText().toString();
        if(password.length() >= 6)
            return true;

        return false;
    }

    private boolean validatePassword2()
    {
        String password = editRepeatPassword.getText().toString();
        if(password.length() >= 6)
            return true;

        return false;
    }

    private void send_new_password() {
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.send_new_password(new NewPasswordForm(email, code, editPassword.getText().toString(), editRepeatPassword.getText().toString()));

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(PasswordActivity.this, "Se guardo su contraseña correctamente",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PasswordActivity.this , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(PasswordActivity.this, "Su código ya fue usuado o expiró",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(PasswordActivity.this, "Ocurrio un error al guardar su contraseña",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
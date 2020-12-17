package com.kylog.barcaoaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    private TextView tokenview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        tokenview = findViewById(R.id.token_view);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            String token = bundle.getString("token");
            tokenview.setText(token);
            Toast.makeText(MainMenu.this, token , Toast.LENGTH_LONG);
        } else{
            Toast.makeText(MainMenu.this, "xD" , Toast.LENGTH_LONG);
        }
    }
}
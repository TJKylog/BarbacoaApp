package com.kylog.barcaoaapp.activities.products;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.kylog.barcaoaapp.MainMenu;
import com.kylog.barcaoaapp.R;

public class ProductsActivity extends AppCompatActivity {

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            token = bundle.getString("token");
        } else{
            Toast.makeText(ProductsActivity.this, "xD" , Toast.LENGTH_LONG);
        }
    }

}
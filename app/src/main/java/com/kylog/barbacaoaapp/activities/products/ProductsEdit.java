package com.kylog.barbacaoaapp.activities.products;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.forms.NewProductForm;

public class ProductsEdit extends AppCompatActivity {

    private Integer id;
    private NewProductForm newProductForm;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_edit);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            id = Integer.parseInt(bundle.getString("id"));
            Toast.makeText(ProductsEdit.this, "id: "+id.toString() , Toast.LENGTH_LONG);
        } else{
            Toast.makeText(ProductsEdit.this, "xD" , Toast.LENGTH_LONG);
        }
    }
}
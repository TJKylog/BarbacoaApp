package com.kylog.barbacaoaapp.activities.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.ActiveMesa;
import com.kylog.barbacaoaapp.models.Product;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private List<Product> productList;
    private int layout;
    private itemClickListener listener;

    public ProductsAdapter(List<Product> productList, int layout, itemClickListener listener) {
        this.productList = productList;
        this.layout = layout;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(productList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView product_name;
        public TextView product_measure;
        public TextView product_type;
        public TextView product_price;

        public ViewHolder(View v) {
            super(v);
            this.product_name = v.findViewById(R.id.product_name_text_grid);
            this.product_price = v.findViewById(R.id.product_price_text_grid);
            this.product_measure = v.findViewById(R.id.product_measure_text_grid);
            this.product_type = v.findViewById(R.id.product_type_text_grid);
        }

        public void bind(final Product product, final itemClickListener listener){
            this.product_name.setText(product.getName());
            this.product_price.setText(product.getPrice().toString());
            this.product_measure.setText(product.getMeasure());
            this.product_type.setText(product.getType());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(product, getAdapterPosition());
                }
            });

        }
    }

    public interface itemClickListener{
        void onItemClick(Product product, int position);
    }
}
package com.kylog.barbacaoaapp.activities.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.ProductType;

import java.util.List;

public class TypesAdapter extends RecyclerView.Adapter<TypesAdapter.ViewHolder> {

    private List<ProductType> productTypes;
    private int layout;
    private onItemClickListener listener;

    public TypesAdapter(List<ProductType> productTypes, int layout, onItemClickListener listener){
        this.productTypes = productTypes;
        this.layout = layout;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(productTypes.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return productTypes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView type_name;

        public ViewHolder (View v){
            super(v);
            this.type_name = v.findViewById(R.id.type_name_item);
        }

        public void bind(final ProductType productType, final onItemClickListener listener){
            this.type_name.setText(productType.getType());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(productType, getAdapterPosition());
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(ProductType productType, int position);
    }
}

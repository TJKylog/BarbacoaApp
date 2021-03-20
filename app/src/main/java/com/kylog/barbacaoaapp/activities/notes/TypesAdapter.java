package com.kylog.barbacaoaapp.activities.notes;

import android.graphics.Color;
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
    private int selected_position = -1;

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView type_name;
        public TextView icon;

        public ViewHolder (View v){
            super(v);
            this.type_name = v.findViewById(R.id.type_name_item);
            this.icon = v.findViewById(R.id.type_name_item_icon);
        }

        public void bind(final ProductType productType, final onItemClickListener listener){
            this.type_name.setText(productType.getType());

            this.type_name.setTextColor(getSelected() == getAdapterPosition() ? Color.BLACK : Color.WHITE);

            if(productType.getType().matches("Barbacoa"))
                this.icon.setBackgroundResource(R.drawable.ic_drumstick_bite_solid);
            else if(productType.getType().matches("Bebida sin alcohol"))
                this.icon.setBackgroundResource(R.drawable.ic_coffee_solid);
            else if(productType.getType().matches("Bebida alcohólica"))
                this.icon.setBackgroundResource(R.drawable.ic_beer_solid);
            else if(productType.getType().matches("Entrada"))
                this.icon.setBackgroundResource(R.drawable.ic_utensils_solid);
            else if(productType.getType().matches("Postre"))
                this.icon.setBackgroundResource(R.drawable.ic_cheese_solid);
            else
                this.icon.setBackgroundResource(R.drawable.ic_candy_cane_solid);

            itemView.setBackgroundResource(getSelected() == getAdapterPosition() ? R.drawable.type_selected : R.drawable.rounded_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSelectedPosition(getAdapterPosition());
                    notifyDataSetChanged();
                    listener.onItemClick(productType, getAdapterPosition());
                }
            });
        }
    }

    public void updateSelectedPosition(int position){
        this.selected_position = position;
    }

    private int getSelected(){
        return this.selected_position;
    }

    public interface onItemClickListener {
        void onItemClick(ProductType productType, int position);
    }

    public void updateList(List<ProductType> productTypes) {
        this.productTypes.clear();
        this.productTypes.addAll(productTypes);
        this.notifyDataSetChanged();
    }
}

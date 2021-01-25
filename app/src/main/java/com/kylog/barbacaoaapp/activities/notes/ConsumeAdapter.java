package com.kylog.barbacaoaapp.activities.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.ActiveMesa;
import com.kylog.barbacaoaapp.models.Consume;

import java.util.List;

public class ConsumeAdapter extends RecyclerView.Adapter<ConsumeAdapter.ViewHolder>{
    private List<Consume> consumes;
    private int layout;
    private itemClickListener listener;

    public ConsumeAdapter(List<Consume> consumes, int layout, itemClickListener listener){
        this.consumes = consumes;
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
        holder.bind(consumes.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return consumes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView product_amount;
        private TextView product_name;
        private TextView product_price;
        private TextView amount_price;

        public ViewHolder(View v) {
            super(v);
            this.product_amount = v.findViewById(R.id.amount_product_consume_item);
            this.product_name = v.findViewById(R.id.product_name_consume_item);
            this.product_price = v.findViewById(R.id.product_price_consume_item);
            this.amount_price = v.findViewById(R.id.product_amount_price_consume_item);
        }

        public void bind(final Consume consume,final itemClickListener listener) {
            this.product_amount.setText(consume.getAmount().toString());
            this.product_name.setText(consume.getName());
            this.product_price.setText(consume.getPrice().toString()+" $");
            this.amount_price.setText(consume.getAmountPrice().toString()+" $");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(consume,getAdapterPosition());
                }
            });
        }
    }

    public interface itemClickListener{
        void onItemClick(Consume consume, int position);
    }
    public void updateList(List<Consume> consumes) {
        this.consumes.clear();
        this.consumes.addAll(consumes);
        this.notifyDataSetChanged();
    }
}

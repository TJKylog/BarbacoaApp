package com.kylog.barbacaoaapp.activities.events;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.Others;

import java.util.List;

public class OthersAdapter extends  RecyclerView.Adapter<OthersAdapter.ViewHolder> {

    private List<Others> othersList;
    private int layout;
    private onItemClickListener listener;
    private Activity activity;
    private onLongItemClickListener onLongListener;

    public OthersAdapter(List<Others> othersList, int layout, onItemClickListener listener,onLongItemClickListener listener1, Activity activity) {
        this.othersList = othersList;
        this.layout = layout;
        this.listener = listener;
        this.activity = activity;
        this.onLongListener = listener1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(othersList.get(position), listener, onLongListener);
    }

    @Override
    public int getItemCount() {
        return this.othersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView nameView, priceView, amountView;

        public ViewHolder(@NonNull View v) {
            super(v);
            this.nameView = v.findViewById(R.id.item_name_package);
            this.amountView = v.findViewById(R.id.item_amount_package);
            this.priceView = v.findViewById(R.id.item_price_package);
        }

        public void bind(final Others others, final onItemClickListener listener, final onLongItemClickListener listener1 )
        {
            this.nameView.setText(others.getName());
            this.amountView.setText(others.getAmount().toString());
            this.priceView.setText(others.getPrice().toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(others, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener1.onLongItemClicked(others, getAdapterPosition());
                    return false;
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(Others others, int position);
    }

    public interface onLongItemClickListener {
        void onLongItemClicked(Others others, int position);
    }

    public void updateList(List<Others> othersList) {
        this.othersList.clear();
        this.othersList.addAll(othersList);
        this.notifyDataSetChanged();
    }

    public void addOther(Others others, int position){
        this.othersList.add(position,others);
        this.notifyItemInserted(position);
    }

    public void editOther(Others others, int position){
        this.othersList.set(position, others);
        this.notifyItemChanged(position);
    }

    public void removeOther(int position){
        this.othersList.remove(position);
        this.notifyItemRemoved(position);
    }
}

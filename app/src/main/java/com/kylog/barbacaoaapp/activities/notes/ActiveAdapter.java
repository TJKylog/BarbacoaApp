package com.kylog.barbacaoaapp.activities.notes;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.ActiveMesa;
import com.kylog.barbacaoaapp.models.ProductType;

import java.util.List;

public class ActiveAdapter extends RecyclerView.Adapter<ActiveAdapter.ViewHolder> {

    private List<ActiveMesa> activeMesaList;
    private int layout;
    private itemClickListener listener;
    private OnItemLongClickListener listener1;

    public ActiveAdapter(List<ActiveMesa> activeMesas, int layout, itemClickListener listener, OnItemLongClickListener listener1) {
        this.activeMesaList = activeMesas;
        this.layout = layout;
        this.listener = listener;
        this.listener1 = listener1;
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
        holder.bind(activeMesaList.get(position), listener, listener1);
    }

    @Override
    public int getItemCount() {
        return activeMesaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private static final int NONE = 0;
        public TextView mesa_name;

        public ViewHolder(View v) {
            super(v);
            this.mesa_name = v.findViewById(R.id.mesa_name_item);
        }

        public void bind(final ActiveMesa activeMesa, final itemClickListener listener, final OnItemLongClickListener listener1){
            this.mesa_name.setText(activeMesa.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(activeMesa, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener1.onItemLongClicked(activeMesa, getAdapterPosition());

                    return true;
                }
            });
        }
    }

    public interface itemClickListener{
        void onItemClick(ActiveMesa activeMesa, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(ActiveMesa activeMesa, int position);
    }

    public void updateList(List<ActiveMesa> activeMesas) {
        this.activeMesaList.clear();
        this.activeMesaList.addAll(activeMesas);
        this.notifyDataSetChanged();
    }
}

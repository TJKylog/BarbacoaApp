package com.kylog.barbacaoaapp.activities.notes;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.ActiveMesa;

import java.util.List;

public class ActiveAdapter extends RecyclerView.Adapter<ActiveAdapter.ViewHolder> {

    private List<ActiveMesa> activeMesaList;
    private int layout;
    private itemClickListener listener;
    private OnItemLongClickListener listener1;
    private int selected_position = -1;

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mesa_name;

        public ViewHolder(View v) {
            super(v);
            this.mesa_name = v.findViewById(R.id.mesa_name_item);
        }

        public void bind(final ActiveMesa activeMesa, final itemClickListener listener, final OnItemLongClickListener listener1){
            this.mesa_name.setText(activeMesa.getName());
            this.mesa_name.setTextColor(getSelected() == getAdapterPosition() ? Color.BLACK : Color.WHITE);
            itemView.setBackgroundResource(getSelected() == getAdapterPosition() ? R.drawable.type_selected : R.drawable.rounded_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateSelectedPosition(getAdapterPosition());
                    notifyDataSetChanged();
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

    public void updateSelectedPosition(int position){
        this.selected_position = position;
    }

    private int getSelected(){
        return this.selected_position;
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

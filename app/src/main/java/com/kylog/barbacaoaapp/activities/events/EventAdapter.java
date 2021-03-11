package com.kylog.barbacaoaapp.activities.events;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.Expense;
import com.kylog.barbacaoaapp.models.forms.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> events;
    private int layout;
    private onItemClickListener listener;
    private Activity activity;

    public EventAdapter(List<Event> eventList, int layout, onItemClickListener listener, Activity activity)
    {
        this.events = eventList;
        this.layout = layout;
        this.listener = listener;
        this.activity = activity;
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
        holder.bind(events.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return this.events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventId, eventName, eventDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.eventId = itemView.findViewById(R.id.item_event_id);
            this.eventName = itemView.findViewById(R.id.item_event_name);
            this.eventDate = itemView.findViewById(R.id.item_event_date);
        }

        public void bind(final Event event, final onItemClickListener listener){
            this.eventId.setText(event.getId().toString());
            this.eventName.setText(event.getEvent_info().getCustomer_name());
            this.eventDate.setText(event.getEvent_info().getDate());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(event, getAdapterPosition());
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(Event event, int position);
    }

    public void updateList(List<Event> eventList) {
        this.events.clear();
        this.events.addAll(eventList);
        this.notifyDataSetChanged();
    }

}

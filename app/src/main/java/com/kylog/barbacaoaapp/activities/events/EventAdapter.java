package com.kylog.barbacaoaapp.activities.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.forms.Event;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> events;
    private int layout;
    private onItemClickListener listener;
    private Activity activity;
    private Context context;

    public EventAdapter(List<Event> eventList, int layout, onItemClickListener listener, Activity activity, Context context)
    {
        this.events = eventList;
        this.layout = layout;
        this.listener = listener;
        this.activity = activity;
        this.context = context;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public TextView eventId, eventName, eventDate,eventTotal,eventPayment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.eventId = itemView.findViewById(R.id.item_event_id);
            this.eventName = itemView.findViewById(R.id.item_event_name);
            this.eventDate = itemView.findViewById(R.id.item_event_date);
            this.eventTotal = itemView.findViewById(R.id.item_event_total);
            this.eventPayment = itemView.findViewById(R.id.item_event_payment);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(@NonNull final Event event, final onItemClickListener listener){
            this.eventId.setText(event.getId().toString());
            this.eventName.setText(event.getEvent_info().getCustomer_name());
            this.eventDate.setText(event.getEvent_info().getDate());
            this.eventTotal.setText("$"+event.getEvent_info().getTotal().toString());
            this.eventPayment.setText("$"+event.getEvent_info().getAdvance_payment().toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(event, getAdapterPosition());
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.event_delete_option: {
                    if(context instanceof EventsActivity) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = ((EventsActivity) context).getLayoutInflater();
                        final View v = inflater.inflate(R.layout.delete_active_mesa_dialog, null);
                        TextView title = (TextView) inflater.inflate(R.layout.title_dialog,null);
                        title.setText("Eliminar evento");
                        builder.setView(v).setCustomTitle(title);
                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        TextView mesa_name = v.findViewById(R.id.delete_active_mesa_name);
                        Button cancel = v.findViewById(R.id.cancel_button_delete_product);
                        Button save = v.findViewById(R.id.save_button_delete_product);

                        mesa_name.setText("¿Esta seguro de eliminar el evento?");

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AppCustomService service = RetrofitClient.getClient();
                                Call<ResponseBody> responseBodyCall = service.delete_event( ((EventsActivity)context).authToken()  , events.get(getAdapterPosition()).getId());
                                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if(response.isSuccessful())
                                        {
                                            events.remove(getAdapterPosition());
                                            notifyItemRemoved(getAdapterPosition());
                                            Toast.makeText(context, "Se elimino correctamente", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Toast.makeText(context, "Ocurrio un error al eliminar", Toast.LENGTH_LONG).show();
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(context, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                    return true;
                }
                case R.id.event_edit_option: {
                    if(context instanceof EventsActivity){
                        Intent intent = new Intent(activity, EditEventActivity.class);
                        intent.putExtra("id", events.get(getAdapterPosition()).getId());
                        activity.startActivity(intent);
                    }
                    return true;
                }
                default:
                    return false;
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Event event = events.get(this.getAdapterPosition());
            menu.setHeaderTitle(event.getId().toString());
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.events_context_menu, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setOnMenuItemClickListener(this);
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

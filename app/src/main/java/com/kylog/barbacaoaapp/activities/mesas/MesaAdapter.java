package com.kylog.barbacaoaapp.activities.mesas;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.models.Mesa;

import java.util.ArrayList;

public class MesaAdapter extends ArrayAdapter<Mesa> {
    private Activity activity;
    private ArrayList<Mesa> lmesa;
    private static LayoutInflater inflater = null;

    public MesaAdapter(Activity activity, int resource, ArrayList<Mesa> lmesa) {
        super(activity, resource, lmesa);
        try {
            this.activity = activity;
            this.lmesa = lmesa;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        catch (Exception $e) {

        }

    }

    public int getCount() {
        return lmesa.size();
    }

    public Mesa getItem(Mesa item) {
        return item;
    }

    public long getItemId(int i) {
        return i;
    }

    public static class viewHolder{
        public TextView name_item_mesa;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final MesaAdapter.viewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.list_item_mesas_layout, null);
                holder = new MesaAdapter.viewHolder();

                holder.name_item_mesa = (TextView) vi.findViewById(R.id.mesa_name_text_list);

                vi.setTag(holder);
            } else {
                holder = (MesaAdapter.viewHolder) vi.getTag();
            }

            String str = lmesa.get(position).getName();

            holder.name_item_mesa.setText(str);

        } catch (Exception e) {

        }
        return vi;
    }
}

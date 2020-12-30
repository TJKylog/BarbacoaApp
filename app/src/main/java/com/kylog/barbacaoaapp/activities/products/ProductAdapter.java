package com.kylog.barbacaoaapp.activities.products;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.kylog.barbacaoaapp.R;

import com.kylog.barbacaoaapp.models.Product;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private Activity activity;
    private ArrayList<Product> lproduct;
    private static LayoutInflater inflater = null;

    public ProductAdapter(Activity activity, int resource, ArrayList<Product> lproduct) {
        super(activity, resource,lproduct);
        try {
            this.activity = activity;
            this.lproduct = lproduct;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        catch (Exception $e) {

        }

    }

    public int getCount() {
        return lproduct.size();
    }

    public Product getItem(Product item) {
        return item;
    }

    public long getItemId(int i) {
        return i;
    }

    public static class viewHolder{
        public TextView name_item;
        public TextView price_item;
        public TextView id_item;
        public TextView measure_item;
        public TextView type_item;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final viewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.list_item_produts_layout, null);
                holder = new viewHolder();

                holder.name_item = (TextView) vi.findViewById(R.id.product_name_text_list);
                holder.id_item = (TextView) vi.findViewById(R.id.product_id_text_list);
                holder.price_item = (TextView) vi.findViewById(R.id.product_price_text_list);
                holder.measure_item = (TextView) vi.findViewById(R.id.product_measure_text_list);
                holder.type_item = (TextView) vi.findViewById(R.id.product_type_text_list);

                vi.setTag(holder);
            } else {
                holder = (viewHolder) vi.getTag();
            }

            holder.name_item.setText(lproduct.get(position).getName());
            holder.id_item.setText(lproduct.get(position).getId().toString());
            holder.type_item.setText(lproduct.get(position).getType());
            holder.measure_item.setText(lproduct.get(position).getMeasure());
            holder.price_item.setText(lproduct.get(position).getPrice().toString());

        } catch (Exception e) {

        }
        return vi;
    }
}

package com.kylog.barbacaoaapp.activities.users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.kylog.barbacaoaapp.R;

import com.kylog.barbacaoaapp.models.User;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {

    private Activity activity;
    private ArrayList<User> listUsers;
    private static LayoutInflater inflater = null;

    public UserAdapter(Activity activity, int resource, ArrayList<User> lusers) {
        super(activity, resource,lusers);
        try {
            this.activity = activity;
            this.listUsers = lusers;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        catch (Exception ignored) {

        }

    }

    public int getCount() {
        return listUsers.size();
    }

    public User getItem(User item) {
        return item;
    }

    public long getItemId(int i) {
        return i;
    }

    public static class viewHolder{
        public TextView user_name_item;
        public TextView user_email_item;
        public TextView user_role_item;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final viewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.list_item_users_layout, null);
                holder = new viewHolder();

                holder.user_name_item = vi.findViewById(R.id.user_name_text_list);
                holder.user_email_item = vi.findViewById(R.id.user_email_text_list);
                holder.user_role_item = vi.findViewById(R.id.user_role_text_list);

                vi.setTag(holder);
            } else {
                holder = (viewHolder) vi.getTag();
            }

            holder.user_name_item.setText(listUsers.get(position).getName());
            holder.user_email_item.setText(listUsers.get(position).getEmail());
            holder.user_role_item.setText(listUsers.get(position).getRole());

        } catch (Exception ignored) {

        }
        return vi;
    }
}
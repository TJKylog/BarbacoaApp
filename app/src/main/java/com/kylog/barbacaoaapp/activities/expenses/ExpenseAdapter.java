package com.kylog.barbacaoaapp.activities.expenses;

import android.app.Activity;
import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.Expense;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenseList;
    private int layout;
    private Activity activity;
    private itemClickListener listener;
    private Context context;

    public ExpenseAdapter(List<Expense> expenseList, int layout, itemClickListener listener, Activity activity, Context context){
        this.expenseList = expenseList;
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
        holder.bind(expenseList.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView viewApprovedBy, viewReason, viewAmount, viewCreatedByName, viewCreatedAt;

        public ViewHolder(View v) {
            super(v);
            this.viewApprovedBy = v.findViewById(R.id.item_expense_approved_by);
            this.viewAmount = v.findViewById(R.id.item_expense_amount);
            this.viewReason = v.findViewById(R.id.item_expense_reason);
            this.viewCreatedByName = v.findViewById(R.id.item_expense_created_by_name);
            this.viewCreatedAt = v.findViewById(R.id.item_expense_created_at);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(final Expense expense, final itemClickListener listener) {
            this.viewApprovedBy.setText(expense.getApprovedBy());
            this.viewReason.setText(expense.getReason());
            this.viewAmount.setText(expense.getAmount().toString());
            this.viewCreatedByName.setText(expense.getCreatedByName());
            this.viewCreatedAt.setText(expense.getCreatedAt());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(expense, getAdapterPosition());
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_expense_option: {
                    if(context instanceof ExpensesActivity){
                        AppCustomService service = RetrofitClient.getClient();
                        retrofit2.Call<ResponseBody> responseBodyCall = service.delete_expense( ((ExpensesActivity)context).authToken()  , expenseList.get(this.getAdapterPosition()).getId());
                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful())
                                {
                                    expenseList.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                    Toast.makeText(context, "Se elimino correctamente", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(context, "Ocurrio un error al eliminar", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(context, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    return true;
                }
                case R.id.edit_expense_option: {
                    if(context instanceof ExpensesActivity){
                        ((ExpensesActivity)context).setExpense(expenseList.get(this.getAdapterPosition()));
                    }
                    return true;
                }
                default:
                    return false;
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Expense expense = expenseList.get(this.getAdapterPosition());
            menu.setHeaderTitle(expense.getId().toString());
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.context_menu_expenses, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setOnMenuItemClickListener(this);
        }
    }

    public interface itemClickListener{
        void onItemClick(Expense expense, int position);
    }

    public void updateList(List<Expense> expenses) {
        this.expenseList.clear();
        this.expenseList.addAll(expenses);
        this.notifyDataSetChanged();
    }

}

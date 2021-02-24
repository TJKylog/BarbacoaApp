package com.kylog.barbacaoaapp.activities.expenses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.models.Expense;
import com.kylog.barbacaoaapp.models.forms.ExpenseForm;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensesActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private ImageButton userActionsButton,backButton;
    private Button saveButton;
    private TextView user_name;
    private EditText editAmount, editReason, editApprovedBy;
    private RecyclerView expensesList;
    private List<Expense> expenses;
    private Expense editExpense;
    private ExpenseAdapter expenseAdapter;
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);
        user_name = findViewById(R.id.user_name_view);
        editAmount = findViewById(R.id.expenses_amount_edit);
        editApprovedBy = findViewById(R.id.expenses_name_edit);
        editReason = findViewById(R.id.expenses_reason_edit);
        expensesList = findViewById(R.id.expenses_list);
        saveButton = findViewById(R.id.save_expense_button);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        expensesList.setLayoutManager(linearLayoutManager);

        expenses = new ArrayList<Expense>();
        expenseAdapter = new ExpenseAdapter(expenses, R.layout.item_list_expense_layout, new ExpenseAdapter.itemClickListener() {
            @Override
            public void onItemClick(Expense expense, int position) {
                Toast.makeText(ExpensesActivity.this, expense.getId().toString(), Toast.LENGTH_LONG).show();
            }
        },ExpensesActivity.this, ExpensesActivity.this);

        expensesList.setAdapter(expenseAdapter);

        user_name.setText(getUseName());

        userActionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editAmount.getText().toString().matches("") || editApprovedBy.getText().toString().matches("") || editAmount.getText().toString().matches(""))
                {
                    Toast.makeText(ExpensesActivity.this, "Complete los campos faltantes", Toast.LENGTH_LONG).show();
                }
                else {
                    if(isEdit == false)
                    {
                        saveExpense();
                    }
                    else {
                        updateExpense();
                    }
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        get_expenses();

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_menu_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.show();
    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out: {
                AppCustomService service = RetrofitClient.getClient();
                Call<ResponseBody> responseBodyCall = service.logout(getTokenType()+" "+getToken());
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText(ExpensesActivity.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(ExpensesActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(ExpensesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void updateExpense(){
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.update_expense(getTokenType()+" "+getToken(), editExpense.getId() , new ExpenseForm(
                editApprovedBy.getText().toString(),
                editReason.getText().toString(),
                Double.parseDouble(String.valueOf(editAmount.getText()))));

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    editApprovedBy.setText("");
                    editReason.setText("");
                    editAmount.setText("");
                    editExpense = null;
                    isEdit = false;
                    get_expenses();
                    Toast.makeText(ExpensesActivity.this, "Se actualizo correctamente", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ExpensesActivity.this, "Ocurrio un error al guardar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ExpensesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveExpense(){
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.save_expense(getTokenType()+" "+getToken(), new ExpenseForm(
                editApprovedBy.getText().toString(),
                editReason.getText().toString(),
                Double.parseDouble(String.valueOf(editAmount.getText()))));

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    editApprovedBy.setText("");
                    editReason.setText("");
                    editAmount.setText("");
                    get_expenses();
                    Toast.makeText(ExpensesActivity.this, "Se guardo correctamente", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ExpensesActivity.this, "Ocurrio un error al guardar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ExpensesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void get_expenses(){
        AppCustomService service = RetrofitClient.getClient();
        Call<List<Expense>> expensesCall = service.expenses(getTokenType()+" "+getToken());
        expensesCall.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(Call<List<Expense>> call, Response<List<Expense>> response) {
                if(response.isSuccessful())
                {
                    expenses = response.body();
                    expenseAdapter.updateList(expenses);
                }
            }

            @Override
            public void onFailure(Call<List<Expense>> call, Throwable t) {
                Toast.makeText(ExpensesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void setExpense(Expense expense) {
        this.editExpense = expense;
        this.isEdit = true;
        this.editApprovedBy.setText(editExpense.getApprovedBy());
        this.editReason.setText(editExpense.getReason());
        this.editAmount.setText(editExpense.getAmount().toString());

        Toast.makeText(ExpensesActivity.this, "Editando: "+expense.getId().toString(), Toast.LENGTH_SHORT).show();
    }

    private String getUseName(){
        return pref.getString("user_name", null);
    }
    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }

    public String authToken() {
        return getTokenType()+" "+getToken();
    }

}
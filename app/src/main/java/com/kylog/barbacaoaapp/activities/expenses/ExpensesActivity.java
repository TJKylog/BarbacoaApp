package com.kylog.barbacaoaapp.activities.expenses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.BluetoothService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.MainMenu;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.notes.DeviceListActivity;
import com.kylog.barbacaoaapp.command.Command;
import com.kylog.barbacaoaapp.command.PrinterCommand;
import com.kylog.barbacaoaapp.models.Expense;
import com.kylog.barbacaoaapp.models.UserName;
import com.kylog.barbacaoaapp.models.forms.Event;
import com.kylog.barbacaoaapp.models.forms.ExpenseForm;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpensesActivity extends AppCompatActivity {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTION_LOST = 6;
    public static final int MESSAGE_UNABLE_CONNECT = 7;
    /*******************************************************************************************************/
    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothService mService;
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter;


    private SharedPreferences pref;
    private ImageButton userActionsButton, mainMenu;
    private Button saveButton;
    private TextView user_name, actionView;
    private EditText editAmount, editReason;
    private RecyclerView expensesList;
    private List<Expense> expenses;
    private Expense editExpense;
    private ExpenseAdapter expenseAdapter;
    private List<UserName> userNameList;
    private Spinner users_names;
    private boolean isEdit = false;
    private ArrayAdapter spinnerUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        userActionsButton = findViewById(R.id.user_actions_button);
        mainMenu = findViewById(R.id.to_main_menu_view_2);
        user_name = findViewById(R.id.user_name_view);
        actionView = findViewById(R.id.expenses_action);
        user_name.setText(getUseName());

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExpensesActivity.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        actionView.setText("Nuevo egreso");

        editAmount = findViewById(R.id.expenses_amount_edit);
        editReason = findViewById(R.id.expenses_reason_edit);
        expensesList = findViewById(R.id.expenses_list);
        saveButton = findViewById(R.id.save_expense_button);
        users_names = findViewById(R.id.users_name);

        get_expenses();

        userNameList = new ArrayList<UserName>();

        spinnerUsers =  new ArrayAdapter( this ,R.layout.simple_spinner_item, userNameList );
        spinnerUsers.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        users_names.setAdapter(spinnerUsers);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        expensesList.setLayoutManager(linearLayoutManager);

        expenses = new ArrayList<Expense>();
        expenseAdapter = new ExpenseAdapter(expenses, R.layout.item_list_expense_layout, new ExpenseAdapter.itemClickListener() {
            @Override
            public void onItemClick(Expense expense, int position) {


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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate_fields())
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

        get_user_names();
    }

    public void check_bluetooth(Expense expense) {
        if(mConnectedDeviceName == null) {
            Intent serverIntent = new Intent(ExpensesActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
        else {
            showPrintExpenseDialog(expense);
        }
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

    private void get_user_names() {
        AppCustomService service = RetrofitClient.getClient();
        Call<List<UserName>> userNamesCall = service.user_names(getTokenType()+" "+getToken());
        userNamesCall.enqueue(new Callback<List<UserName>>() {
            @Override
            public void onResponse(Call<List<UserName>> call, Response<List<UserName>> response) {
                if(response.isSuccessful())
                {
                    userNameList = response.body();
                    spinnerUsers =  new ArrayAdapter( ExpensesActivity.this ,R.layout.support_simple_spinner_dropdown_item, userNameList );
                    users_names.setAdapter(spinnerUsers);
                }
            }

            @Override
            public void onFailure(Call<List<UserName>> call, Throwable t) {

            }
        });

    }

    private void showPrintExpenseDialog(Expense expense){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.print_expense_dialog, null);
        TextView title = (TextView) getLayoutInflater().inflate(R.layout.title_dialog,null);
        title.setText("Imprimir egreso");
        builder.setView(v).setCustomTitle(title);
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView date = v.findViewById(R.id.print_expense_date);
        Button cancel = v.findViewById(R.id.cancel_action_print_expense);
        Button print = v.findViewById(R.id.print_expense);

        date.setText("¿Desea imprimir el egreso con fecha de "+expense.getCreatedAt()+"?");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendDataByte(Command.ESC_Init);
                SendDataByte(Command.LF);
                Print_Ex(expense);
                dialog.dismiss();
            }
        });
    }

    private void updateExpense(){
        final UserName userName = (UserName) users_names.getSelectedItem();

        AppCustomService service = RetrofitClient.getClient();
        Call<com.kylog.barbacaoaapp.models.Message> messageCall = service.update_expense(getTokenType()+" "+getToken(), editExpense.getId() , new ExpenseForm(
                userName.getName(),
                editReason.getText().toString(),
                Double.parseDouble(String.valueOf(editAmount.getText()))));

        messageCall.enqueue(new Callback<com.kylog.barbacaoaapp.models.Message>() {
            @Override
            public void onResponse(Call<com.kylog.barbacaoaapp.models.Message> call, Response<com.kylog.barbacaoaapp.models.Message> response) {
                if(response.isSuccessful())
                {
                    editReason.setText("");
                    editAmount.setText("");
                    editExpense = null;
                    isEdit = false;
                    get_expenses();
                    actionView.setText("Nuevo egreso");
                    com.kylog.barbacaoaapp.models.Message message = response.body();
                    Toast.makeText(ExpensesActivity.this, message.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ExpensesActivity.this, "Ocurrio un error al guardar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<com.kylog.barbacaoaapp.models.Message> call, Throwable t) {
                Toast.makeText(ExpensesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveExpense(){
        final UserName userName = (UserName) users_names.getSelectedItem();
        AppCustomService service = RetrofitClient.getClient();
        Call<com.kylog.barbacaoaapp.models.Message> responseBodyCall = service.save_expense(getTokenType()+" "+getToken(), new ExpenseForm(
                userName.getName(),
                editReason.getText().toString(),
                Double.parseDouble(String.valueOf(editAmount.getText()))));

        responseBodyCall.enqueue(new Callback<com.kylog.barbacaoaapp.models.Message>() {
            @Override
            public void onResponse(Call<com.kylog.barbacaoaapp.models.Message> call, Response<com.kylog.barbacaoaapp.models.Message> response) {
                if(response.isSuccessful())
                {
                    editReason.setText("");
                    editAmount.setText("");
                    get_expenses();
                    com.kylog.barbacaoaapp.models.Message message = response.body();
                    Toast.makeText(ExpensesActivity.this, message.getMessage(), Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ExpensesActivity.this, "Ocurrio un error al guardar", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<com.kylog.barbacaoaapp.models.Message> call, Throwable t) {
                Toast.makeText(ExpensesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean validate_fields()
    {
        boolean isValid = false;
        if( editReason.getText().toString().matches("") || editAmount.getText().toString().matches("")) {
            if (editReason.getText().toString().matches("")) {
                editReason.setError("Complete este campo");
            }
            if (editAmount.getText().toString().matches("")) {
                editAmount.setError("Complete este campo");
            }
        }
        else {
            isValid = true;
        }

        return isValid;
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
        this.users_names.setSelection(spinnerUsers.getPosition(editExpense.getApprovedBy()));
        this.editReason.setText(editExpense.getReason());
        this.editAmount.setText(editExpense.getAmount().toString());
        actionView.setText("Editando egreso: "+expense.getCreatedAt());
        Toast.makeText(ExpensesActivity.this, "Editando: "+expense.getId().toString(), Toast.LENGTH_SHORT).show();
    }

    public void onStart() {
        super.onStart();

        // If Bluetooth is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the session
        } else {
            if (mService == null)
                KeyListenerInit();//监听
        }
    }

    private void SendDataString(String data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "No hay una impresora conectada", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (data.length() > 0) {
            try {
                mService.write(data.getBytes("GBK"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
     *SendDataByte
     */
    private void SendDataByte(byte[] data) {

        if (mService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "No hay una impresora conectada", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mService.write(data);
    }

    /****************************************************************************************************/
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            break;
                        case BluetoothService.STATE_CONNECTING:

                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:

                            break;
                    }
                    break;
                case MESSAGE_WRITE:

                    break;
                case MESSAGE_READ:

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),
                            "Conectado a " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    Toast.makeText(getApplicationContext(), "Se perdio la conexion con el dispositivo",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Toast.makeText(getApplicationContext(), "No se puede conectar",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: {
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    if (BluetoothAdapter.checkBluetoothAddress(address)) {
                        BluetoothDevice device = mBluetoothAdapter
                                .getRemoteDevice(address);
                        // Attempt to connect to the device
                        mService.connect(device);
                    }
                }
                break;
            }
            case REQUEST_ENABLE_BT: {
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a session
                    KeyListenerInit();
                } else {
                    // User did not enable Bluetooth or an error occured

                    Toast.makeText(this, "Bluetooth no disponible",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            }
        }
    }

    /**
     *
     */
    @SuppressLint("SimpleDateFormat")
    private void Print_Ex(Expense expense){

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String date = str + "\n";
        try {
            Command.ESC_Align[2] = 0x01;
            SendDataByte(Command.ESC_Align);
            SendDataByte("Egreso\n".getBytes("GBK"));
            SendDataString(date);
            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            SendDataByte(Command.GS_ExclamationMark);
            SendDataByte(PrinterCommand.POS_Print_Text(expense.toString(), "GBK", 0, 0, 0, 0));
            SendDataByte(Command.LF);
            SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(48));
            SendDataByte(Command.GS_V_m_n);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void KeyListenerInit() {
        mService = new BluetoothService(this, mHandler);
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
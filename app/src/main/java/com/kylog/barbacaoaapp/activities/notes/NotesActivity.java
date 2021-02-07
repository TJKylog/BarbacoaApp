package com.kylog.barbacaoaapp.activities.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.BluetoothService;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.command.Command;
import com.kylog.barbacaoaapp.command.PrinterCommand;
import com.kylog.barbacaoaapp.models.ActiveMesa;
import com.kylog.barbacaoaapp.models.Consume;
import com.kylog.barbacaoaapp.models.DataAvailable;
import com.kylog.barbacaoaapp.models.Mesa;
import com.kylog.barbacaoaapp.models.Note;
import com.kylog.barbacaoaapp.models.Product;
import com.kylog.barbacaoaapp.models.ProductType;
import com.kylog.barbacaoaapp.models.Waiter;
import com.kylog.barbacaoaapp.models.forms.AddAmount;
import com.kylog.barbacaoaapp.models.forms.DeleteProduct;
import com.kylog.barbacaoaapp.models.forms.DoneTicketForm;
import com.kylog.barbacaoaapp.models.forms.FormActive;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesActivity extends AppCompatActivity {

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
    private SharedPreferences pref;
    private RecyclerView typesList;
    private TypesAdapter typesAdapter;
    private ActiveAdapter activeAdapter;
    private ArrayList<ProductType> productTypes;
    private ArrayList<ActiveMesa> activeMesas;
    private ConsumeAdapter consumeAdapter;
    private Note note;
    private RecyclerView activeslist;
    private DataAvailable dataAvailable;
    private Button add_active_button;
    private Spinner spinnerMesas;
    private Spinner spinnerWaiters;
    private ProductsAdapter productsAdapter;
    private List<Product> products;
    private RecyclerView productsGrid;
    private RecyclerView note_product_list;
    private TextView total_consume_price;
    private TextView note_mesa_name;
    private TextView note_waiter_name;
    private BluetoothService mService;
    private Button print_ticket, done_tikcet;
    private String mConnectedDeviceName = null;
    private Double payment,change;
    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        typesList = findViewById(R.id.list_types);
        activeslist = findViewById(R.id.active_list);
        add_active_button = findViewById(R.id.add_active_button);
        productsGrid = findViewById(R.id.products_grid);
        note_product_list = findViewById(R.id.note_products_list);
        total_consume_price = findViewById(R.id.consume_total_price);
        note_mesa_name = findViewById(R.id.note_mesa_name);
        note_waiter_name = findViewById(R.id.note_waiter_name);
        print_ticket = findViewById(R.id.print_note);
        done_tikcet = findViewById(R.id.print_done_note);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typesList.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        activeslist.setLayoutManager(layoutManager2);
        LinearLayoutManager layoutManager3 = new GridLayoutManager(this,2);
        productsGrid.setLayoutManager(layoutManager3);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this);
        layoutManager4.setOrientation(LinearLayoutManager.VERTICAL);
        note_product_list.setLayoutManager(layoutManager4);

        add_active_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddActive();
            }
        });

        /*Product types list */
        productTypes = new ArrayList<ProductType>();
        typesAdapter = new TypesAdapter(productTypes, R.layout.types_item_list_layout, new TypesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(ProductType productType, int position) {
                Toast.makeText(NotesActivity.this, productType.getType(), Toast.LENGTH_LONG).show();
                get_products_by_type(productType.getType());
            }
        });
        typesList.setAdapter(typesAdapter);

        /* Mesa activas */
        activeMesas = new ArrayList<ActiveMesa>();
        activeAdapter = new ActiveAdapter(activeMesas, R.layout.mesas_actives_layout, new ActiveAdapter.itemClickListener() {
            @Override
            public void onItemClick(ActiveMesa activeMesa, int position) {
                Toast.makeText(NotesActivity.this, activeMesa.getName(), Toast.LENGTH_LONG).show();
                get_mesa_consume(activeMesa.getId());
            }
        }, new ActiveAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(ActiveMesa activeMesa, int position) {
                showDialogDeleteMesa(activeMesa.getId(),activeMesa.getName());
                return false;
            }
        });
        activeslist.setAdapter(activeAdapter);

        /* lista de consumos del cliente */
        consumeAdapter = new ConsumeAdapter(new ArrayList<Consume>(), R.layout.consumes_list,
                new ConsumeAdapter.itemClickListener() {
                    @Override
                    public void onItemClick(Consume consume, int position) {
                        showDialogProduct(consume.getId(), consume.getName());
                    }
                },
                new ConsumeAdapter.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClicked(Consume consume, int position) {
                        showDialogDeleteProduct(consume.getId(),consume.getName());
                        return false;
                    }
                }
        );
        note_product_list.setAdapter(consumeAdapter);

        productsAdapter = new ProductsAdapter(new ArrayList<Product>(), R.layout.grid_item_produts_layout, new ProductsAdapter.itemClickListener() {
            @Override
            public void onItemClick(Product product, int position) {
                if(note != null)
                {
                    showDialogProduct(product.getId(),product.getName());
                }
                else {
                    Toast.makeText(NotesActivity.this, "Seleccione una mesa primero", Toast.LENGTH_SHORT).show();
                }
            }
        });
        productsGrid.setAdapter(productsAdapter);

        print_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*  */
                if(mConnectedDeviceName == null) {
                    Intent serverIntent = new Intent(NotesActivity.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
                else {
                    if(note != null)
                    {
                        if(note.getConsumes().isEmpty())
                        {
                            Toast.makeText(NotesActivity.this,"No ha consumido nada",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            SendDataByte(Command.ESC_Init);
                            SendDataByte(Command.LF);
                            Print_Ex();
                        }
                    }
                    else {
                        Toast.makeText(NotesActivity.this, "No ha seleccionado una mesa",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        done_tikcet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialogDoneTicket();
                if(mConnectedDeviceName == null) {
                    Intent serverIntent = new Intent(NotesActivity.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                }
                else {
                    if(note != null)
                    {
                        if(note.getConsumes().isEmpty())
                        {
                            Toast.makeText(NotesActivity.this,"No ha consumido nada",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            showDialogDoneTicket();
                        }
                    }
                    else {
                        Toast.makeText(NotesActivity.this, "No ha seleccionado una mesa",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        get_types();
        get_active();

    }

    private void showDialogDoneTicket(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_product_amount_note, null);
        builder.setView(view).setTitle("Cantidad del pago");
        final AlertDialog dialog = builder.create();
        dialog.show();
        EditText amount = view.findViewById(R.id.add_amount_product);
        Button cancel = view.findViewById(R.id.cancel_button_add_product);
        Button save = view.findViewById(R.id.save_button_add_product);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payment = Double.parseDouble(String.valueOf(amount.getText()));
                if(payment >= note.getTotal())
                {
                    change = note.getTotal() - payment;
                    AppCustomService service = RetrofitClient.getClient();
                    Call<ResponseBody> responseBodyCall = service.done_ticket(getTokenType()+" "+getToken(), note.getId(),
                            new DoneTicketForm(payment,change)
                    );
                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful())
                            {
                                dialog.dismiss();
                                Toast.makeText(NotesActivity.this, "La venta se guardó correctamente",Toast.LENGTH_LONG).show();
                                SendDataByte(Command.ESC_Init);
                                SendDataByte(Command.LF);
                                Print_Ex2();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(NotesActivity.this, "Ocurrió un error al conectarse al servidor",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Toast.makeText(NotesActivity.this, "El pago debe ser mayor o igual al total",Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void get_products_by_type(String type) {
        AppCustomService service = RetrofitClient.getClient();
        Call<List<Product>> listCall = service.get_products_by_type(getTokenType()+" "+getToken(), type );

        listCall.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()) {
                    products = response.body();
                    productsAdapter.updateList(products);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }

    private void get_types() {
        AppCustomService service = RetrofitClient.getClient();
        Call<List<ProductType>> typescall = service.get_types(getTokenType()+" "+getToken());

        typescall.enqueue(new Callback<List<ProductType>>() {
            @Override
            public void onResponse(Call<List<ProductType>> call, Response<List<ProductType>> response) {
                if(response.isSuccessful()) {
                    productTypes = (ArrayList<ProductType>) response.body();
                    typesAdapter.updateList(productTypes);
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<List<ProductType>> call, Throwable t) {

            }
        });
    }

    private void get_active() {
        AppCustomService service = RetrofitClient.getClient();
        Call<List<ActiveMesa>> activesCall = service.get_active(getTokenType()+" "+getToken());

        activesCall.enqueue(new Callback<List<ActiveMesa>>() {
            @Override
            public void onResponse(Call<List<ActiveMesa>> call, Response<List<ActiveMesa>> response) {
                if(response.isSuccessful()){
                    activeMesas = (ArrayList<ActiveMesa>) response.body();
                    activeAdapter.updateList(activeMesas);
                }
            }

            @Override
            public void onFailure(Call<List<ActiveMesa>> call, Throwable t) {

            }
        });
    }



    private void showDialogDeleteMesa(Integer id, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_active_mesa_dialog, null);
        builder.setView(view).setTitle(name);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView mesa_name = view.findViewById(R.id.delete_active_mesa_name);
        Button cancel = view.findViewById(R.id.cancel_button_delete_product);
        Button save = view.findViewById(R.id.save_button_delete_product);

        mesa_name.setText("Borrar: "+name);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCustomService service = RetrofitClient.getClient();
                Call<ResponseBody> responseBodyCall = service.delete_active(getTokenType()+" "+getToken(), id);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful())
                        {
                            get_active();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(NotesActivity.this, "Ocurrio un error",Toast.LENGTH_LONG).show();
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

    private void showDialogDeleteProduct(Integer id, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_active_mesa_dialog, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView mesa_name = view.findViewById(R.id.delete_active_mesa_name);
        Button cancel = view.findViewById(R.id.cancel_button_delete_product);
        Button save = view.findViewById(R.id.save_button_delete_product);

        mesa_name.setText("Borrar: "+name);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCustomService service = RetrofitClient.getClient();
                Call<ResponseBody> responseBodyCall = service.delete_product_mesa(getTokenType()+" "+getToken(), note.getId(), new DeleteProduct(id));
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful())
                        {
                            get_mesa_consume(note.getId());
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(NotesActivity.this, "Ocurrio un error",Toast.LENGTH_LONG).show();
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

    private void showDialogProduct(Integer id, String name){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_product_amount_note, null);
        builder.setView(view).setTitle(name);
        final AlertDialog dialog = builder.create();
        dialog.show();
        EditText amount = view.findViewById(R.id.add_amount_product);
        Button cancel = view.findViewById(R.id.cancel_button_add_product);
        Button save = view.findViewById(R.id.save_button_add_product);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCustomService service = RetrofitClient.getClient();
                Call<ResponseBody> responseBodyCall = service.add_product_mesa(getTokenType()+" "+getToken(), note.getId(),
                        new AddAmount(id,
                                Double.parseDouble(String.valueOf(amount.getText())))
                );
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful())
                        {
                            get_mesa_consume(note.getId());
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(NotesActivity.this, "Ocurrio un error",Toast.LENGTH_LONG).show();
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

    private void showAddActive(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.add_mesa, null);
        spinnerMesas = v.findViewById(R.id.spinner_mesas);
        spinnerWaiters = v.findViewById(R.id.spinner_waiters);

        builder.setView(v)
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Mesa mesa = (Mesa) spinnerMesas.getSelectedItem();
                        final Waiter waiter = (Waiter) spinnerWaiters.getSelectedItem();
                        add_active(new FormActive(waiter.getId(),mesa.getId()));
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

        AppCustomService service = RetrofitClient.getClient();
        Call<DataAvailable> dataAvailableCall = service.get_data_available(getTokenType()+" "+getToken());

        dataAvailableCall.enqueue(new Callback<DataAvailable>() {
            @Override
            public void onResponse(Call<DataAvailable> call, Response<DataAvailable> response) {
                if(response.isSuccessful()) {
                    dataAvailable = response.body();
                    if(dataAvailable.getMesas().isEmpty() || dataAvailable.getWaiters().isEmpty() )
                    {
                        Toast.makeText(NotesActivity.this, "No hay mesas disponibles", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    else{
                        ArrayAdapter spinnerAdapterMesas =  new ArrayAdapter( v.getContext() ,R.layout.support_simple_spinner_dropdown_item, dataAvailable.getMesas());
                        spinnerMesas.setAdapter(spinnerAdapterMesas);
                        ArrayAdapter spinnerAdapterWaiters = new ArrayAdapter(v.getContext(),R.layout.support_simple_spinner_dropdown_item , dataAvailable.getWaiters());
                        spinnerWaiters.setAdapter(spinnerAdapterWaiters);
                    }
                }
            }

            @Override
            public void onFailure(Call<DataAvailable> call, Throwable t) {

            }
        });


    }

    private void add_active(FormActive formActive) {
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.add_active(getTokenType()+" "+getToken(), formActive);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    get_active();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void get_mesa_consume(Integer id) {
        AppCustomService service = RetrofitClient.getClient();
        Call<Note> noteCall = service.get_mesa_consume(getTokenType()+" "+getToken(), id);

        noteCall.enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if(response.isSuccessful()) {
                    note = response.body();
                    note_mesa_name.setText(note.getName());
                    note_waiter_name.setText("Mesero: "+note.getWaiter());
                    total_consume_price.setText("Total: "+note.getTotal().toString()+" $");
                    consumeAdapter.updateList(note.getConsumes());
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {

            }
        });
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
            Toast.makeText(this, "R.string.not_connected", Toast.LENGTH_SHORT)
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
            Toast.makeText(this, "R.string.not_connected", Toast.LENGTH_SHORT)
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
                            "Connected to " + mConnectedDeviceName,
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),
                            msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                            .show();
                    break;
                case MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                    Toast.makeText(getApplicationContext(), "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_UNABLE_CONNECT:     //无法连接设备
                    Toast.makeText(getApplicationContext(), "Unable to connect device",
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

                    Toast.makeText(this, "xD",
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
    private void Print_Test(){
    }

    /**
     *
     */
    @SuppressLint("SimpleDateFormat")
    private void Print_Ex(){

            SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss ");
            Date curDate = new Date(System.currentTimeMillis());
            String str = formatter.format(curDate);
            String date = str + "\n";
            try {
                Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                SendDataByte("Ticket de venta\n".getBytes("GBK"));
                SendDataString(date);
                Command.ESC_Align[2] = 0x00;
                SendDataByte(Command.ESC_Align);
                Command.GS_ExclamationMark[2] = 0x00;
                SendDataByte(Command.GS_ExclamationMark);
                SendDataByte(PrinterCommand.POS_Print_Text(note.toString(), "GBK", 0, 0, 0, 0));
                SendDataByte(Command.LF);
                SendDataByte("Este documento no tiene validez fiscal\n\n\n\n\n".getBytes("GBK"));
                SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(48));
                SendDataByte(Command.GS_V_m_n);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    @SuppressLint("SimpleDateFormat")
    private void Print_Ex2(){

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String date = str + "\n";
        String pago = "Pago: "+String.valueOf(payment)+"\n";
        String cambio = "Cambio: "+String.valueOf(change)+"\n";

        try {
            Command.ESC_Align[2] = 0x01;
            SendDataByte(Command.ESC_Align);
            SendDataByte("Ticket de venta\n".getBytes("GBK"));
            SendDataString(date);
            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            SendDataByte(Command.GS_ExclamationMark);
            SendDataByte(PrinterCommand.POS_Print_Text(note.toString(), "GBK", 0, 0, 0, 0));
            SendDataByte(pago.getBytes("GBK"));
            SendDataByte(cambio.getBytes("GBK"));
            SendDataByte(Command.LF);
            SendDataByte("Este documento no tiene validez fiscal\n\n\n\n\n".getBytes("GBK"));
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

    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }

}
package com.kylog.barbacaoaapp.activities.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.BluetoothService;
import com.kylog.barbacaoaapp.MainActivity;
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
import com.kylog.barbacaoaapp.models.SalesDay;
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
    private ImageButton add_active_button;
    private Spinner spinnerMesas;
    private Spinner spinnerWaiters;
    private ProductsAdapter productsAdapter;
    private List<Product> products;
    private RecyclerView productsGrid;
    private RecyclerView note_product_list;
    private TextView total_consume_price,user_name;
    private TextView note_mesa_name;
    private TextView note_waiter_name;
    private BluetoothService mService;
    private Button print_ticket, done_tikcet;
    private String mConnectedDeviceName = null;
    private Double payment_amount,change;
    private ImageButton userActionsButton,backButton;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        mService = null;

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
        user_name = findViewById(R.id.user_name_view);
        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);

        user_name.setText(getUseName());

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

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
                getDataAvailable();
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
                        showDialogProduct(consume.getId(), consume.getName(),consume.getMeasure(),consume.getAmount());
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
                    showDialogProduct(product.getId(),product.getName(),product.getMeasure(),0.0);
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

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.user_menu_actions_consumes, popup.getMenu());
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
                            Toast.makeText(NotesActivity.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(NotesActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
            case R.id.sales_day: {
                showDialogDoneDaySales();
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showDialogDoneDaySales(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.sales_day_dialog, null);
        builder.setView(view).setTitle("Venta del día");
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView sale_date_total = view.findViewById(R.id.sales_day_total);
        Button close_dialog = view.findViewById(R.id.close_dialog_sales_day);

        AppCustomService service = RetrofitClient.getClient();
        Call<SalesDay> salesDayCall = service.sale_day(getTokenType()+" "+getToken());

        salesDayCall.enqueue(new Callback<SalesDay>() {
            @Override
            public void onResponse(Call<SalesDay> call, Response<SalesDay> response) {
                if(response.isSuccessful())
                {
                    SalesDay salesDay = response.body();
                    sale_date_total.setText("$ "+salesDay.getTotal().toString());
                }
            }

            @Override
            public void onFailure(Call<SalesDay> call, Throwable t) {
                Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void showDialogDoneTicket(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.payment_note_layout, null);
        builder.setView(view).setTitle("Cantidad del pago");
        final AlertDialog dialog = builder.create();
        dialog.show();
        RadioButton paymentCard = view.findViewById(R.id.radio_payment_card);
        RadioButton paymentCash = view.findViewById(R.id.radio_payment_cash);
        EditText amount = view.findViewById(R.id.amount_cash_payment);
        Button cancel = view.findViewById(R.id.cancel_done_ticket);
        Button save = view.findViewById(R.id.done_ticket);

        paymentCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                amount.setEnabled(!isChecked);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paymentCard.isChecked()) {
                    payment_amount = 0.0;
                    AppCustomService service = RetrofitClient.getClient();
                    Call<ResponseBody> responseBodyCall = service.done_ticket(getTokenType() + " " + getToken(), note.getId(),
                                new DoneTicketForm("Tarjeta" ,payment_amount, change)
                    );
                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(NotesActivity.this, "La venta se guardó correctamente", Toast.LENGTH_LONG).show();
                                SendDataByte(Command.ESC_Init);
                                SendDataByte(Command.LF);
                                Print_Ex2("Tarjeta");
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                if(paymentCash.isChecked()){
                    payment_amount = Double.parseDouble(String.valueOf(amount.getText()));
                    if (payment_amount >= note.getTotal()) {
                        change = payment_amount - note.getTotal();
                        AppCustomService service = RetrofitClient.getClient();
                        Call<ResponseBody> responseBodyCall = service.done_ticket(getTokenType() + " " + getToken(), note.getId(),
                                new DoneTicketForm("Efectivo" ,payment_amount, change)
                        );
                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(NotesActivity.this, "La venta se guardó correctamente", Toast.LENGTH_LONG).show();
                                    SendDataByte(Command.ESC_Init);
                                    SendDataByte(Command.LF);
                                    Print_Ex2("Efectivo");
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(NotesActivity.this, "El pago debe ser mayor o igual al total", Toast.LENGTH_SHORT).show();
                    }
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
                Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
                Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
                Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }



    private void showDialogDeleteMesa(Integer id, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_active_mesa_dialog, null);
        builder.setView(view).setTitle("Eliminar consumo");
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView mesa_name = view.findViewById(R.id.delete_active_mesa_name);
        Button cancel = view.findViewById(R.id.cancel_button_delete_product);
        Button save = view.findViewById(R.id.save_button_delete_product);

        mesa_name.setText("¿Está seguro que desea eliminar el consumo de la mesa \""+name+"\"?");

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
                            Toast.makeText(NotesActivity.this, "Se eliminó correctamente el consumo de la mesa ", Toast.LENGTH_LONG).show();
                            get_active();
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
        builder.setView(view).setTitle("Eliminar producto");
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView mesa_name = view.findViewById(R.id.delete_active_mesa_name);
        Button cancel = view.findViewById(R.id.cancel_button_delete_product);
        Button save = view.findViewById(R.id.save_button_delete_product);

        mesa_name.setText("¿Esta seguro de eliminar el producto \""+name+"\" de la mesa \""+note.getName()+"\"?" );

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
                            Toast.makeText(NotesActivity.this, "Se eliminó correctamente el producto", Toast.LENGTH_LONG).show();
                            get_mesa_consume(note.getId());
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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

    private void showDialogProduct(Integer id, String name,String measure_label_input,Double total1 ){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_product_amount_note, null);
        builder.setView(view).setTitle(name);
        final AlertDialog dialog = builder.create();
        dialog.show();
        EditText amount = view.findViewById(R.id.add_amount_product);
        TextView measure_label = view.findViewById(R.id.measure_label_note);
        Button cancel = view.findViewById(R.id.cancel_button_add_product);
        Button save = view.findViewById(R.id.save_button_add_product);
        measure_label.setText(measure_label_input);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(amount.getText().toString().matches(""))
                {
                    amount.setError("Escriba la cantidad");
                }
                else {
                    Double aDouble;
                    Double value1 = Double.parseDouble(String.valueOf(amount.getText()));
                    if(measure_label_input.matches("Gramos"))
                    {
                        aDouble =  value1 + (1000.00 * total1 );
                    }
                    else
                    {
                        aDouble = total1 + value1;
                    }
                    AppCustomService service = RetrofitClient.getClient();
                    Call<ResponseBody> responseBodyCall = service.add_product_mesa(getTokenType()+" "+getToken(), note.getId(),
                            new AddAmount(id, aDouble)
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
                            Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                        }
                    });
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

    private void getDataAvailable(){
        AppCustomService service = RetrofitClient.getClient();
        Call<DataAvailable> dataAvailableCall = service.get_data_available(getTokenType()+" "+getToken());

        dataAvailableCall.enqueue(new Callback<DataAvailable>() {
            @Override
            public void onResponse(Call<DataAvailable> call, Response<DataAvailable> response) {
                if(response.isSuccessful()) {
                    dataAvailable = response.body();
                    if(dataAvailable.getMesas().isEmpty() || dataAvailable.getWaiters().isEmpty() )
                    {
                        if(dataAvailable.getMesas().isEmpty() && dataAvailable.getWaiters().isEmpty())
                        {
                            Toast.makeText(NotesActivity.this, "No hay mesas y meseros disponibles disponibles", Toast.LENGTH_SHORT).show();
                        }
                        else if( dataAvailable.getMesas().isEmpty() ) {
                            Toast.makeText(NotesActivity.this, "No hay mesas disponibles", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(NotesActivity.this, "No hay meseros disponibles", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        showAddActive();
                    }
                }
            }

            @Override
            public void onFailure(Call<DataAvailable> call, Throwable t) {
                Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAddActive(){
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.add_mesa, null);
        builder.setView(v).setTitle("Añadir mesa");
        final AlertDialog dialog = builder.create();
        dialog.show();

        RadioButton delivery = v.findViewById(R.id.radio_mesa_delivery);
        RadioButton here = v.findViewById(R.id.radio_mesa_here);
        spinnerMesas = v.findViewById(R.id.spinner_mesas);
        spinnerWaiters = v.findViewById(R.id.spinner_waiters);
        Button save = v.findViewById(R.id.save_button_add_mesa);
        Button cancel = v.findViewById(R.id.cancel_button_add_mesa);


        ArrayAdapter spinnerAdapterMesas =  new ArrayAdapter( v.getContext() ,R.layout.support_simple_spinner_dropdown_item, dataAvailable.getMesas());
        spinnerMesas.setAdapter(spinnerAdapterMesas);
        ArrayAdapter spinnerAdapterWaiters = new ArrayAdapter(v.getContext(),R.layout.support_simple_spinner_dropdown_item , dataAvailable.getWaiters());
        spinnerWaiters.setAdapter(spinnerAdapterWaiters);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Mesa mesa = (Mesa) spinnerMesas.getSelectedItem();
                final Waiter waiter = (Waiter) spinnerWaiters.getSelectedItem();
                if(delivery.isChecked()) {
                    add_active(new FormActive(waiter.getId(),mesa.getId(),true));
                }
                else {
                    add_active(new FormActive(waiter.getId(),mesa.getId(),false));
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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

                    if(note.isDelivery()){
                        note_mesa_name.setText(note.getName()+": Entrega a domicilio" );
                    }
                    else {
                        note_mesa_name.setText(note.getName());
                    }
                    note_waiter_name.setText("Mesero: "+note.getWaiter());
                    total_consume_price.setText("Total: $"+note.getTotal().toString());
                    consumeAdapter.updateList(note.getConsumes());
                }
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                Toast.makeText(NotesActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
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
            String folio = "Folio: "+note.getInvoice().toString()+"\n";
            String date = str + "\n";
            try {
                Command.ESC_Align[2] = 0x01;
                SendDataByte(Command.ESC_Align);
                SendDataByte("Ticket de venta\n".getBytes("GBK"));
                SendDataByte(folio.getBytes("GBK"));
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
    private void Print_Ex2(String payment_method){

        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String date = str + "\n";
        String pago = "Pago: "+String.valueOf(payment_amount)+"\n";
        String folio = "Folio: "+note.getInvoice().toString()+"\n";
        String cambio = "Cambio: "+String.valueOf(change)+"\n";
        payment_method = "Forma de pago: "+ payment_method+"\n";

        try {
            Command.ESC_Align[2] = 0x01;
            SendDataByte(Command.ESC_Align);
            SendDataByte("Ticket de venta\n".getBytes("GBK"));
            SendDataByte(folio.getBytes("GBK"));
            if(note.isDelivery())
            {
                SendDataByte("Entrega a domicilio\n".getBytes("GBK"));
            }
            SendDataString(date);
            Command.ESC_Align[2] = 0x00;
            SendDataByte(Command.ESC_Align);
            Command.GS_ExclamationMark[2] = 0x00;
            SendDataByte(Command.GS_ExclamationMark);
            SendDataByte(PrinterCommand.POS_Print_Text(note.toString(), "GBK", 0, 0, 0, 0));
            SendDataByte(pago.getBytes("GBK"));
            SendDataByte(cambio.getBytes("GBK"));
            SendDataByte(payment_method.getBytes("GBK"));
            SendDataByte(Command.LF);
            SendDataByte(Command.LF);
            SendDataByte("Gracias por su preferencia\n\n\n".getBytes("GBK"));
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

    private String getUseName(){
        return pref.getString("user_name", null);
    }
    private String getToken(){
        return pref.getString("token", null);
    }
    private String getTokenType(){
        return pref.getString("token_type",null);
    }

}
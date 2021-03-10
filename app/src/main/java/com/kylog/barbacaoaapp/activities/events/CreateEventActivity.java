package com.kylog.barbacaoaapp.activities.events;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.kylog.barbacaoaapp.AppCustomService;
import com.kylog.barbacaoaapp.MainActivity;
import com.kylog.barbacaoaapp.MainMenu;
import com.kylog.barbacaoaapp.R;
import com.kylog.barbacaoaapp.RetrofitClient;
import com.kylog.barbacaoaapp.activities.expenses.ExpensesActivity;
import com.kylog.barbacaoaapp.models.BasicPackage;
import com.kylog.barbacaoaapp.models.Others;
import com.kylog.barbacaoaapp.models.forms.Event;
import com.kylog.barbacaoaapp.models.forms.EventInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateEventActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private ImageButton userActionsButton,backButton, mainMenu;
    private Button save_event, add_extra;
    private TextView user_name, total_event;
    private List<BasicPackage> basicPackageList;
    private EditText dateEvent,timeDate,nameEditText,addressEditText,phoneEditText,advance_payment;
    private List<Others> othersList;
    private RecyclerView basicPackageR, extrasList;
    private BasicAdapter basicAdapter;
    private OthersAdapter othersAdapter;
    private Double total = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        userActionsButton = findViewById(R.id.user_actions_button);
        backButton = findViewById(R.id.back_button);
        mainMenu = findViewById(R.id.to_main_menu_view);
        user_name = findViewById(R.id.user_name_view);
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

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this , MainMenu.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        basicPackageR = findViewById(R.id.basic_package_list);
        extrasList = findViewById(R.id.extras_list);
        save_event = findViewById(R.id.save_event_button);
        dateEvent =  findViewById(R.id.date_event);
        timeDate = findViewById(R.id.time_Event);
        add_extra = findViewById(R.id.add_extra_button);
        nameEditText = findViewById(R.id.name_customer_name_field);
        addressEditText = findViewById(R.id.name_customer_address_field);
        phoneEditText = findViewById(R.id.name_customer_phone_field);
        advance_payment = findViewById(R.id.advance_payment);
        total_event = findViewById(R.id.total_event);

        timeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        dateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        add_extra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddExtra();
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        basicPackageR.setLayoutManager(linearLayoutManager);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        extrasList.setLayoutManager(layoutManager1);

        othersList = new ArrayList<Others>();

        othersAdapter = new OthersAdapter(othersList, R.layout.item_package_list, new OthersAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Others others, int position) {
                showDialogEditExtra(others,position);
            }
        }, CreateEventActivity.this);
        extrasList.setAdapter(othersAdapter);

        basicPackageList = new ArrayList<BasicPackage>();
        basicPackageList.add(new BasicPackage(1.0,200.0,"Personas"));
        basicPackageList.add(new BasicPackage(1.0,200.0,"Borrego"));
        basicPackageList.add(new BasicPackage(1.0,200.0,"Consomé con garbanzo (guarniciones)"));
        basicPackageList.add(new BasicPackage(1.0,200.0,"Arroz"));
        basicPackageList.add(new BasicPackage(1.0,200.0,"Tortillas a mano"));

        basicAdapter = new BasicAdapter(basicPackageList, R.layout.item_package_list, new BasicAdapter.onItemClickListener() {
            @Override
            public void onItemClick(BasicPackage basicPackage, int position) {
                Toast.makeText(CreateEventActivity.this, basicPackage.getName(), Toast.LENGTH_SHORT).show();
            }
        }, CreateEventActivity.this);

        save_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameEditText.getText().toString().matches("") ||
                        addressEditText.getText().toString().matches("") ||
                        phoneEditText.getText().toString().matches("") ||
                        advance_payment.getText().toString().matches("") ||
                        dateEvent.getText().toString().matches("") ||
                        timeDate.getText().toString().matches("")) {
                    if(nameEditText.getText().toString().matches(""))
                    {
                        nameEditText.setError("Completa este campo");
                    }
                    if(addressEditText.getText().toString().matches(""))
                    {
                        addressEditText.setError("Completa este campo");
                    }
                    if(phoneEditText.getText().toString().matches(""))
                    {
                        phoneEditText.setError("Completa esta campo");
                    }
                    if(advance_payment.getText().toString().matches(""))
                    {
                        advance_payment.setError("Completa este campo");
                    }
                    if(dateEvent.getText().toString().matches(""))
                    {
                        dateEvent.setError("Completa este campo");
                    }
                    if (timeDate.getText().toString().matches(""))
                    {
                        timeDate.setError("Completa este campo");
                    }
                }
                else {
                    save_event();
                }

            }
        });

        basicPackageR.setAdapter(basicAdapter);

        updateTotal();

    }

    public void showDialogAddExtra(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.extra_dialog, null);
        builder.setView(view).setTitle("Añadir extra");
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button save = view.findViewById(R.id.save_button_extra);
        Button cancel = view.findViewById(R.id.cancel_button_extra);
        EditText name = view.findViewById(R.id.name_extra_field);
        EditText amount = view.findViewById(R.id.amount_extra_field);
        EditText price = view.findViewById(R.id.price_extra_field);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().matches("") || amount.getText().toString().matches("") || price.getText().toString().matches(""))
                {
                    if(name.getText().toString().matches(""))
                    {
                        name.setError("Complete este campo");
                    }
                    if(amount.getText().toString().matches("")){
                        amount.setError("Complete este campo");
                    }
                    if(price.getText().toString().matches(""))
                    {
                        price.setError("Complete este campo");
                    }
                }
                else {
                    othersAdapter.addOther(new Others( Double.parseDouble(String.valueOf(amount.getText())), Double.parseDouble(String.valueOf(price.getText())),name.getText().toString() ),  othersAdapter.getItemCount() );
                    updateTotal();
                    dialog.dismiss();
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

    public void showDialogEditExtra(Others others, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateEventActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.extra_dialog, null);
        builder.setView(view).setTitle("Editar extra");
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button save = view.findViewById(R.id.save_button_extra);
        Button cancel = view.findViewById(R.id.cancel_button_extra);
        EditText name = view.findViewById(R.id.name_extra_field);
        EditText amount = view.findViewById(R.id.amount_extra_field);
        EditText price = view.findViewById(R.id.price_extra_field);
        name.setText(others.getName());
        amount.setText(others.getAmount().toString());
        price.setText(others.getPrice().toString());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().matches("") || amount.getText().toString().matches("") || price.getText().toString().matches(""))
                {
                    if(name.getText().toString().matches(""))
                    {
                        name.setError("Complete este campo");
                    }
                    if(amount.getText().toString().matches("")){
                        amount.setError("Complete este campo");
                    }
                    if(price.getText().toString().matches(""))
                    {
                        price.setError("Complete este campo");
                    }
                }
                else {
                    othersAdapter.editOther(new Others( Double.parseDouble(String.valueOf(amount.getText())), Double.parseDouble(String.valueOf(price.getText())),name.getText().toString() ),  position );
                    updateTotal();
                    dialog.dismiss();
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

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment().newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                final String selectedTime = hourOfDay + ":" + minute;
                timeDate.setText(selectedTime);
            }
        });
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment1 = new DatePickerFragment().newInstance(new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // Do something with the date chosen by the user
                final String selectedDate = day + "-" + (month+1) + "-" + year;
                dateEvent.setText(selectedDate);
            }
        });
        newFragment1.show(getSupportFragmentManager(), "datePicker");
    }

    private void updateTotal()
    {
        total = 0.0;
        for (Others o:othersList) {
            total =  o.getPrice() + total;
        }

        for (BasicPackage b:basicPackageList)
        {
            total = b.getPrice() + total;
        }
        total_event.setText("$"+total);
    }

    private void save_event() {
        AppCustomService service = RetrofitClient.getClient();
        Call<ResponseBody> responseBodyCall = service.save_event(getTokenType()+" "+getToken(),
                new Event(
                        new EventInfo(othersList,
                                basicPackageList,
                                total,
                                Double.parseDouble(advance_payment.getText().toString()),
                                nameEditText.getText().toString(),
                                addressEditText.getText().toString(),
                                phoneEditText.getText().toString(),
                                dateEvent.getText().toString(),
                                timeDate.getText().toString())
                )
        );

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful())
                {
                    Toast.makeText(CreateEventActivity.this, "El evento se guardo exitosamente" , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateEventActivity.this , EventsActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CreateEventActivity.this, "Error" , Toast.LENGTH_SHORT).show();
            }
        });

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
                            Toast.makeText(CreateEventActivity.this, "Cerrando sessión" , Toast.LENGTH_SHORT).show();
                            pref.edit().clear().apply();
                            Intent intent = new Intent(CreateEventActivity.this , MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(CreateEventActivity.this, "No se pudo conectar con el servidor, revise su conexión", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
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

    private static void toggleTextInputLayoutError(@NonNull TextInputLayout textInputLayout, String msg) {
        textInputLayout.setError(msg);
        textInputLayout.setErrorEnabled(msg != null);
    }

    public static class TimePickerFragment extends DialogFragment {

        private TimePickerDialog.OnTimeSetListener listener;

        public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener){
            TimePickerFragment fragment  = new TimePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }

        public void setListener(TimePickerDialog.OnTimeSetListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), listener, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    public static class DatePickerFragment extends DialogFragment {

        private DatePickerDialog.OnDateSetListener listener;

        public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener){
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.setListener(listener);
            return fragment;
        }

        private void setListener(DatePickerDialog.OnDateSetListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }
    }
}
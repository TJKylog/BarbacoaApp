package com.kylog.barbacaoaapp.activities.users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;

import com.kylog.barbacaoaapp.R;

public class UsersEdit extends AppCompatActivity {

    private SharedPreferences pref;
    private EditText editName;
    private EditText editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_edit);
    }
}
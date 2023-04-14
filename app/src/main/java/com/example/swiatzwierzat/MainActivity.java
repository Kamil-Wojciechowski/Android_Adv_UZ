package com.example.swiatzwierzat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.swiatzwierzat.configuration.BackendConfig;

public class MainActivity extends AppCompatActivity {

    private BackendConfig backendConfig;
    private Button loginButton;

    private void initializer(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.bt_login);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer(savedInstanceState);

        backendConfig = new BackendConfig("dorix6543@gmail.com", "test1234");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valid = backendConfig.isValid();
                Log.w("backend.animal.world", valid.toString());
            }
        });
    }




}
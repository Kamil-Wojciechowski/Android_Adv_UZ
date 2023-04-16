package com.example.swiatzwierzat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiatzwierzat.configuration.BackendConfig;

public class MainActivity extends AppCompatActivity {

    private BackendConfig backendConfig;
    private TextView inEmail;
    private TextView inPassword;
    private Button loginButton;
    private Button registerButton;
    private Button forgetButton;

    private void initializer(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        backendConfig = new BackendConfig();

        inEmail = findViewById(R.id.et_email);
        inPassword = findViewById(R.id.et_password);

        loginButton = findViewById(R.id.bt_login);
        registerButton = findViewById(R.id.bt_register);
        forgetButton = findViewById(R.id.bt_pwdForget);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer(savedInstanceState);

        loginButton.setOnClickListener(this::onLogin);
        registerButton.setOnClickListener(this::onRegister);
        forgetButton.setOnClickListener(this::onForget);
    }

    private void setCredentials(String email, String password) {
        backendConfig.setEmail(email);
        backendConfig.setPassword(password);
    }
    private void onLogin(View v) {
        setCredentials(inEmail.getText().toString(), inPassword.getText().toString());

        Boolean logged = backendConfig.login();
        Log.w("swiatzwierzat.onlogin.valid", logged.toString());
        if(logged) {
            Toast.makeText(v.getContext(), "Correctly logged in!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(v.getContext(), ProductsActivity.class));

        } else {
            Toast.makeText(getApplicationContext(), "Credentials seems wrong! Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    private void onRegister(View v) {
        startActivity(new Intent(v.getContext(), RegisterActivity.class));
    }

    private void onForget(View v) {
        startActivity(new Intent(v.getContext(), ForgetPasswordActivity.class));
    }




}
package com.example.swiatzwierzat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.swiatzwierzat.configuration.BackendConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private BackendConfig backendConfig;
    private TextView inEmail;
    private TextView inPassword;
    private Button loginButton;
    private Button registerButton;
    private Button forgetButton;

    private void initializer(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

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

        Context context = this.getApplicationContext();

        backendConfig.setSharedPreferences(context.getSharedPreferences(String.valueOf(R.string.preference_file_key), Context.MODE_PRIVATE));

        loginButton.setOnClickListener(this::onLogin);
        registerButton.setOnClickListener(this::onRegister);
        forgetButton.setOnClickListener(this::onForget);
    }

    private void onLogin(View v) {
        AndroidNetworking.post(backendConfig.getUrl() + "/login")
                .addBodyParameter("email", inEmail.getText().toString())
                .addBodyParameter("password", inPassword.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("backend.config.success", response.toString());
                        backendConfig.setIsLogged(true);
                        try {
                            backendConfig.setToken("Bearer " + response.getString("access_token"));
                            backendConfig.setRefreshToken(response.getString("refresh_token"));
                            Toast.makeText(v.getContext(), "Correctly logged in!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(v.getContext(), ProductsActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.w("backend.config.error", anError.toString());
                        Toast.makeText(getApplicationContext(), "Credentials seems wrong! Please try again.", Toast.LENGTH_LONG).show();
                        backendConfig.setToken(null);
                        backendConfig.setIsLogged(false);
                    }
                });
    }

    private void onRegister(View v) {
        startActivity(new Intent(v.getContext(), RegisterActivity.class));
    }

    private void onForget(View v) {
        startActivity(new Intent(v.getContext(), ForgetPasswordActivity.class));
    }




}
package com.example.swiatzwierzat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    private BackendConfig backendConfig;
    private TextView inEmail;
    private TextView inPassword;
    private Button loginButton;
    private Button registerButton;
    private Button forgetButton;

    private Boolean credentialsSaved = false;

    private SharedPreferences sharedPreferences;

    private void readSharedPreferences() {
        Context context = this.getApplicationContext();

        sharedPreferences = context.getSharedPreferences(BackendConfig.getSharedPreferenceName(), Context.MODE_PRIVATE);

        backendConfig.setToken(sharedPreferences.getString("token", null));
        backendConfig.setRefreshToken(sharedPreferences.getString("refreshToken", null));
        inEmail.setText(sharedPreferences.getString("loginEmail", ""));

        if(backendConfig.getRefreshToken() != null) {
            credentialsSaved = true;
        }
    }

    private void initializer(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);

        inEmail = findViewById(R.id.et_login_email);
        inPassword = findViewById(R.id.et_login_password);

        loginButton = findViewById(R.id.bt_login_login);
        registerButton = findViewById(R.id.bt_login_register);
        forgetButton = findViewById(R.id.bt_login_forget);

        readSharedPreferences();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer(savedInstanceState);

        loginButton.setOnClickListener(this::onLogin);
        registerButton.setOnClickListener(this::onRegister);
        forgetButton.setOnClickListener(this::onForget);

        if(credentialsSaved) {
            alreadyLogged();
        }
    }

    private void alreadyLogged() {
        Log.w("login.activity.credentials", "Here make biometric login if we have credentials");
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

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("loginEmail", inEmail.getText().toString());
                            editor.putString("token", backendConfig.getToken());
                            editor.putString("refreshToken", backendConfig.getRefreshToken());
                            editor.apply();

                            Toast.makeText(v.getContext(), R.string.credentials_login, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(v.getContext(), ProductsActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.w("login.activity.error", anError.toString());
                        Toast.makeText(getApplicationContext(), R.string.credentials_login_error, Toast.LENGTH_LONG).show();
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
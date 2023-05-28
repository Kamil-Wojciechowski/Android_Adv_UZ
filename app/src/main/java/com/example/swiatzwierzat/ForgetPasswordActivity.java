package com.example.swiatzwierzat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextView inEmail;
    private Button back;
    private Button send;
    private SharedPreferences sharedPreferences;

    private BackendConfig backendConfig;

    /**
     * Zczytanie preferenecji użytkownika oraz uzupełnienie email'a jeśli takowy istnieje.
     */
    private void readSharedPreferences() {
        Context context = this.getApplicationContext();

        sharedPreferences = context.getSharedPreferences(BackendConfig.getSharedPreferenceName(), Context.MODE_PRIVATE);

        inEmail.setText(sharedPreferences.getString("loginEmail", ""));
    }

    /**
     * Inicjalizacja elementów z aktywności
     */
    private void initializer(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forget_password);

        inEmail = findViewById(R.id.et_forget_email);
        back = findViewById(R.id.bt_forget_back);
        send = findViewById(R.id.bt_forget_send);

        readSharedPreferences();
    }

    /**
     * Inicjalizacja aktywności oraz przypisanie funkcjonalności do przycisków
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializer(savedInstanceState);

        back.setOnClickListener(this::onBack);
        send.setOnClickListener(this::onSend);
    }

    /**
     * cofniecie do poprzedniej aktywności
     */
    private void onBack(View v) {
        onBackPressed();
    }

    /**
     * Walidacja pola
     */
    private boolean isFieldsValid() {
        Boolean isValid = true;

        if(inEmail.length() == 0) {
            inEmail.setError(getResources().getString(R.string.register_email_error_empty));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inEmail.getText()).matches()) {
            inEmail.setError(getResources().getString(R.string.register_email_wrong_type));
            isValid = false;
        }

        return isValid;
    }

    /**
     * Po zwalidowaniu pól, zostaje wykonane zapytanie do serwera backend w celu zresetowania hasła danemu użytkownikowi
     */
    private void onSend(View v) {
        if(isFieldsValid()) {
            AndroidNetworking.post(backendConfig.getUrl() + "/recovery/" + inEmail.getText().toString())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(v.getContext(), R.string.forget_success, Toast.LENGTH_LONG).show();
                            onBackPressed();
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.w("login.activity.error", anError.toString());
                            Toast.makeText(getApplicationContext(), R.string.global_something_went_wrong, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}
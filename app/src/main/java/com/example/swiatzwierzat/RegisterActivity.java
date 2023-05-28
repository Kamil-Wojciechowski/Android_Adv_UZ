package com.example.swiatzwierzat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.swiatzwierzat.configuration.BackendConfig;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private BackendConfig backendConfig;
    private TextView inEmail;
    private TextView inPassword;
    private TextView inCPassword;
    private TextView inFirstname;
    private TextView inLastname;

    private Button backButton;
    private Button registerButton;

    /**
     * Inicjalizer wszystkich elementów które znajdują się na aktywności
     */
    private void initializer(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);

        inEmail = findViewById(R.id.et_register_email);
        inPassword = findViewById(R.id.et_register_password);
        inCPassword = findViewById(R.id.et_register_cpassword);
        inFirstname = findViewById(R.id.et_register_firstname);
        inLastname = findViewById(R.id.et_register_lastname);

        backButton = findViewById(R.id.bt_register_back);
        registerButton = findViewById(R.id.bt_register_register);
    }

    /**
     * Inicjalizuje aktywność oraz ustawia odpowiednie funkcji na przyciskach
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializer(savedInstanceState);
        backButton.setOnClickListener(this::onBack);
        registerButton.setOnClickListener(this::onRegister);
    }

    /**
     * Cofanie do poprzednej aktywności
     */
    private void onBack(View v) {
        onBackPressed();
    }

    /**
     * Walidacja pól
     */
    private boolean isFieldsValid() {
        boolean isValid = true;

        if(inFirstname.length() == 0) {
            inFirstname.setError(getResources().getString(R.string.register_firstname_error_empty));
            isValid = false;
        } else if(inFirstname.length() < 2) {
            inFirstname.setError(getResources().getString(R.string.register_firstname_error_min_length));
            isValid = false;
        }

        if(inLastname.length() == 0) {
            inLastname.setError(getResources().getString(R.string.register_lastname_error_empty));
            isValid = false;
        } else if(inLastname.length() < 2) {
            inFirstname.setError(getResources().getString(R.string.register_lastname_error_min_length));
            isValid = false;
        }

        if(inEmail.length() == 0) {
            inEmail.setError(getResources().getString(R.string.register_email_error_empty));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inEmail.getText()).matches()) {
            inEmail.setError(getResources().getString(R.string.register_email_wrong_type));
            isValid = false;
        }

        if(inPassword.length() < 8) {
            inPassword.setError(getResources().getString(R.string.register_password_wrong_min_length));
            isValid = false;
        } else if (!inPassword.getText().toString().equals(inCPassword.getText().toString())) {
            inCPassword.setError(getResources().getString(R.string.register_password_not_equal));
            isValid = false;
        }

        return isValid;
    }

    /**
     * W przypadku gdy pola zostały odpowiednio uzupełnione, wykonywane jest zapytanie do serwera backend w celu utworzenia konta.
     */
    private void onRegister(View v) {
        boolean isValid = isFieldsValid();

        if(isValid) {
            try {
                JSONObject registerBody = new JSONObject()
                        .put("firstname", inFirstname.getText().toString())
                        .put("lastname", inLastname.getText().toString())
                        .put("email", inEmail.getText().toString())
                        .put("password", inPassword.getText().toString())
                        .put("confirmedPassword", inCPassword.getText().toString());

                AndroidNetworking.post(backendConfig.getUrl() + "/register")
                        .addJSONObjectBody(registerBody)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getApplicationContext(), R.string.registered, Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(getApplicationContext(), R.string.global_something_went_wrong, Toast.LENGTH_LONG).show();
                            }
                        });

            } catch (Exception e) {

            }
        }
    }
}
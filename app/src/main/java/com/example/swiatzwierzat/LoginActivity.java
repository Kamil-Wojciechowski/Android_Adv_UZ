package com.example.swiatzwierzat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricPrompt;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.swiatzwierzat.configuration.BackendConfig;
import com.example.swiatzwierzat.library.LibBiometrics;
import com.example.swiatzwierzat.library.LibNotifications;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private BackendConfig backendConfig;
    private TextView inEmail;
    private TextView inPassword;
    private Button loginButton;
    private Button registerButton;
    private Button forgetButton;

    private Button fingerprintButton;
    private Boolean credentialsSaved = false;

    private SharedPreferences sharedPreferences;

    /**
     * Zczytanie elementów z shared preferences
     * takich jak np. "token, refreshToken, loginEmail"
     */
    private void readSharedPreferences() {
        Context context = this.getApplicationContext();

        sharedPreferences = context.getSharedPreferences(BackendConfig.getSharedPreferenceName(), Context.MODE_PRIVATE);

        BackendConfig.setToken(sharedPreferences.getString("token", null));
        BackendConfig.setRefreshToken(sharedPreferences.getString("refreshToken", null));
        inEmail.setText(sharedPreferences.getString("loginEmail", ""));

        if (BackendConfig.getRefreshToken() != null) {
            credentialsSaved = true;
        }
    }

    /**
     * Inicjalizacja wszystkich przycisków, Zczytanie zapisanych ustawień z SharedPreferences
     * Ustawienie night mode wartości.
     */
    private void initializer(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);

        inEmail = findViewById(R.id.et_login_email);
        inPassword = findViewById(R.id.et_login_password);

        loginButton = findViewById(R.id.bt_login_login);
        registerButton = findViewById(R.id.bt_login_register);
        forgetButton = findViewById(R.id.bt_login_forget);
        fingerprintButton = findViewById(R.id.bt_login_fingerprint);

        readSharedPreferences();
        AppCompatDelegate.setDefaultNightMode(sharedPreferences.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO));
    }

    /**
     * Inicjalizacja aktywności.
     * Zapytanie o pozwolenie dla notyfikacji.
     * Skorzystanie z initializera.
     * Przypisanie funkcji do przycisków.
     * Rozpoczęcie czasowych notyfikacji.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LibNotifications.askForNotificationPermissions(this);
        initializer(savedInstanceState);

        loginButton.setOnClickListener(this::onLogin);
        registerButton.setOnClickListener(this::onRegister);
        forgetButton.setOnClickListener(this::onForget);
        fingerprintButton.setOnClickListener(this::onBiometricLogin);

        LibNotifications.startPeriodicalNotifications(this);
    }

    /**
     * Po otworzeniu (nie zamkniętej całkowicie) aplikacji sprawdzany jest dostęp poprzez biometrię
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (credentialsSaved && LibBiometrics.canUseBiometrics(this)) {
            fingerprintButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Implementacja metody dla przycisku "Login"
     * Tworzone jest zapytanie do serwera backend w celu pobrania danych autoryzacyjnych użytkownika.
     * Zapisywane są one do obiektu statycznego. W przypadku braku powodzenia jest wyświetlana odpowiednia notyfikacja.
     */
    private void onLogin(View v) {
        AndroidNetworking.post(BackendConfig.getUrl() + "/login")
                .addBodyParameter("email", inEmail.getText().toString())
                .addBodyParameter("password", inPassword.getText().toString())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("backend.config.success", response.toString());
                        BackendConfig.setIsLogged(true);
                        try {
                            BackendConfig.setToken("Bearer " + response.getString("access_token"));
                            BackendConfig.setRefreshToken(response.getString("refresh_token"));

                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("loginEmail", inEmail.getText().toString());
                            editor.putString("token", BackendConfig.getToken());
                            editor.putString("refreshToken", BackendConfig.getRefreshToken());
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
                        BackendConfig.setToken(null);
                        BackendConfig.setIsLogged(false);
                    }
                });
    }

    /**
     * Metoda dla przypisania logiki dla biometryki
     */
    private void onBiometricLogin(View v) {
        this.handleBiometricLogin();
    }

    /**
     * Logika, której zadaniem jest autoryzacja użytkownika za pomoca biometri.
     * W przypadku gdy uda się użytkownikowi zalogować za pomocą biometri,
     * wywoływany jest request do serwera backend w celu pobrania nowego tokenu na podstawie "refresh_tokena"
     */
    private void handleBiometricLogin() {
        this.toggleButtons(false, false);
        LibBiometrics.showBiometricsDialog(this, R.string.fingerprint_login_title, R.string.fingerprint_login_description, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                LoginActivity.this.toggleButtons(errorCode != BiometricPrompt.ERROR_LOCKOUT, true);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                AndroidNetworking.post(BackendConfig.getUrl() + "/token/refresh/" + BackendConfig.getRefreshToken()).build().getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        BackendConfig.setIsLogged(true);

                        try {
                            BackendConfig.setToken("Bearer " + response.getString("access_token"));
                            BackendConfig.setRefreshToken(response.getString("refresh_token"));

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", BackendConfig.getToken());
                            editor.putString("refreshToken", BackendConfig.getRefreshToken());
                            editor.apply();

                            startActivity(new Intent(LoginActivity.this, ProductsActivity.class));
                        } catch (JSONException e) {
                            Log.e("Respone error", e.toString());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        LibNotifications.sendToast(LoginActivity.this, R.string.notification_refresh_token_error);
                        BackendConfig.setToken(null);
                        BackendConfig.setRefreshToken(null);
                        BackendConfig.setIsLogged(false);

                        LoginActivity.this.toggleButtons(false, true);
                    }
                });
            }
        });
    }

    /**
     *
     * Metody onRegister oraz onForget rozpoczynają nowe aktywności
     */
    private void onRegister(View v) {
        startActivity(new Intent(v.getContext(), RegisterActivity.class));
    }

    private void onForget(View v) {
        startActivity(new Intent(v.getContext(), ForgetPasswordActivity.class));
    }

    /**
     * Funkcja odpowiendnio zarządza przyciskami oraz możliwościami
     */
    private void toggleButtons(boolean isFingerprintEnabled, boolean isEnabled) {
        fingerprintButton.setEnabled(isFingerprintEnabled);
        forgetButton.setEnabled(isEnabled);
        registerButton.setEnabled(isEnabled);
        loginButton.setEnabled(isEnabled);
    }

}
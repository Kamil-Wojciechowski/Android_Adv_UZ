package com.example.swiatzwierzat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Method;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.swiatzwierzat.configuration.BackendConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    private BackendConfig backendConfig;

    private Integer addressId;
    private TextView inFirstname;
    private TextView inLastname;
    private TextView inMobilePhone;
    private TextView inStreet;
    private TextView inCity;
    private TextView inPostalCode;

    private Button save;

    private SharedPreferences sharedPreferences;

    private void loadAddress() {
        addressId = sharedPreferences.getInt("user_address", 0);

        if(addressId != 0) {
            AndroidNetworking.get(backendConfig.getUrl() + "/addresses/" + addressId)
                    .addHeaders("Authorization", backendConfig.getToken())
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                inFirstname.setText(response.getString("firstname"));
                                inLastname.setText(response.getString("lastname"));
                                inMobilePhone.setText(response.getString("mobileNumber"));
                                inStreet.setText(response.getString("street"));
                                inCity.setText(response.getString("city"));
                                inPostalCode.setText(response.getString("postalCode"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            addressId = 0;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("user_address", addressId);
                            editor.apply();
                        }
                    });
        }

    }

    private void initializer() {
        setContentView(R.layout.activity_settings);

        inFirstname = findViewById(R.id.et_settings_firstname);
        inLastname = findViewById(R.id.et_settings_lastname);
        inMobilePhone = findViewById(R.id.et_settings_mobile_phone);
        inStreet = findViewById(R.id.et_settings_street);
        inCity = findViewById(R.id.et_settings_city);
        inPostalCode = findViewById(R.id.et_settings_postal_code);
        save = findViewById(R.id.bt_settings_save);
        save.setOnClickListener(this::save);

        sharedPreferences = getApplicationContext().getSharedPreferences(BackendConfig.getSharedPreferenceName(), Context.MODE_PRIVATE);

        loadAddress();
    }

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

        if(inMobilePhone.length() < 8) {
            inMobilePhone.setError(getResources().getString(R.string.register_phone_empty));
            isValid = false;
        }

        if(inStreet.length() == 0) {
            inStreet.setError(getResources().getString(R.string.register_street_empty));
            isValid = false;
        }

        if(inCity.length() == 0) {
            inCity.setError(getResources().getString(R.string.register_city_empty));
            isValid = false;
        }

        if(inPostalCode.length() == 0) {
            inPostalCode.setError(getResources().getString(R.string.register_postal_code_empty));
            isValid = false;
        }

        return isValid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializer();
    }

    private void save(View view) {
        if(isFieldsValid()) {
            JSONObject address = null;
            try {
                address = new JSONObject()
                   .put("firstname", inFirstname.getText().toString())
                   .put("lastname", inLastname.getText().toString())
                   .put("mobileNumber", inMobilePhone.getText().toString())
                   .put("street", inStreet.getText().toString())
                   .put("postalCode", inPostalCode.getText().toString())
                   .put("city", inCity.getText().toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            int method = 0;
            String url = null;
            if(addressId == 0) {
                method = Method.POST;
                url = backendConfig.getUrl() + "/addresses";
            } else {
                method = Method.PATCH;
                url = backendConfig.getUrl() + "/addresses/" + addressId;
            }

            AndroidNetworking.request(url, method)
                    .addHeaders("Authorization", backendConfig.getToken())
                    .addJSONObjectBody(address)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(addressId != 0 ) {
                                    Toast.makeText(getApplicationContext(), R.string.address_updated, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.address_created, Toast.LENGTH_LONG).show();
                                }


                                try {
                                    addressId = response.getInt("id");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putInt("user_address", addressId);
                                editor.apply();
                            }

                            @Override
                            public void onError(ANError anError) {
                                Toast.makeText(getApplicationContext(), R.string.global_something_went_wrong, Toast.LENGTH_LONG).show();
                            }
                        });
        }
    }
}
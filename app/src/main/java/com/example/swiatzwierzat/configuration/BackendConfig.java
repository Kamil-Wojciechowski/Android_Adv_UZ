package com.example.swiatzwierzat.configuration;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BackendConfig extends Concurent {
    private static String url = "https://app-web-uz.herokuapp.com/api/v1";
    private static String host = "app-web-uz.herokuapp.com:443";
    private static String email = "";
    private static String password = "";
    private static String token = "";
    private static String refreshToken = "";
    private static boolean isLogged = false;

    public static void setEmail(String email) {
        BackendConfig.email = email;
    }

    public static void setPassword(String password) {
        BackendConfig.password = password;
    }

    public static boolean login() {
        AndroidNetworking.post(url + "/login")
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("backend.config.success", response.toString());
                        isLogged = true;
                        try {
                            token = "Bearer " + response.getString("access_token");
                            Log.w("token", token);
                            refreshToken = response.getString("refresh_token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.w("backend.config.error", anError.toString());
                        token = null;
                        isLogged = false;
                    }
                });

        return isLogged;
    }

    public static boolean isValid() {
        Log.w("backend.config.valid.token", token.toString());
        AndroidNetworking.get(url + "/users")
                .addHeaders("Authorization", token)
                .addHeaders("Host", host)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w("backend.config.valid.success", response.toString());
                        isLogged = true;
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.w("backend.config.valid.error", anError.getErrorBody());
                        isLogged = false;
                    }
                });

        return isLogged;
    }


}

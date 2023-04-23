package com.example.swiatzwierzat.configuration;

import android.content.SharedPreferences;

public class BackendConfig {
    private static String url = "https://app-web-uz.herokuapp.com/api/v1";
    private static String token = "";
    private static String refreshToken = "";
    private static boolean isLogged = false;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        editor.putString("token", token);
        BackendConfig.token = token;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static String getUrl() {
        return url;
    }

    public static void setRefreshToken(String refreshToken) {
        editor.putString("refreshToken", refreshToken);
        BackendConfig.refreshToken = refreshToken;
    }

    public static void setSharedPreferences(SharedPreferences sharedPreferences) {
        BackendConfig.sharedPreferences = sharedPreferences;
    }

    public static boolean isIsLogged() {
        return isLogged;
    }

    public static void setIsLogged(boolean isLogged) {
        BackendConfig.isLogged = isLogged;
    }
}

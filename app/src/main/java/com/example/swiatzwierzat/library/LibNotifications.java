package com.example.swiatzwierzat.library;

import android.content.Context;
import android.widget.Toast;

public class LibNotifications {
    /**
     * Wysyłka powiadomienia typu Toast z podaną wiadomościom
     *
     * @param context - "this" z aktywności
     * @param message - wiadomość do wyświetlenia
     */
    public static void sendToast(Context context, String message) {
        if (context == null || message == null) {
            return;
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Wysyłka powiadomienia typu Toast z podaną wiaodmościom jako id z zasobów
     *
     * @param context   - "this" z aktywności
     * @param idMessage - wiadomość do wyświetlenia jako id tłumaczenia z zasobów
     */
    public static void sendToast(Context context, int idMessage) {
        if (context == null) {
            return;
        }

        Toast.makeText(context, idMessage, Toast.LENGTH_SHORT).show();
    }
}

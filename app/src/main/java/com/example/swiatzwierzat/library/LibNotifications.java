package com.example.swiatzwierzat.library;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.swiatzwierzat.NotificationBroadcast;
import com.example.swiatzwierzat.R;

public class LibNotifications {
    // Co ile minut wysyłać powiadomienie cykliczne do użytkownika
    private static final long CYCLIC_NOTIFICATION_INTERVAL = 1;

    /**
     * Odpytanie użytkownika czy wyraża zgodę na powiadomienia
     *
     * @param activity - "this" z aktywności
     */
    public static void askForNotificationPermissions(Activity activity) {
        String[] permissions = new String[]{Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.SCHEDULE_EXACT_ALARM, Manifest.permission.USE_EXACT_ALARM};
        ActivityCompat.requestPermissions(activity, permissions, 1);
    }

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

    /**
     * Wysyłka powiadomienia z podaną wiadomością
     *
     * @param context   - "this" z aktwyności
     * @param idTitle   - tytuł powiadomienia jako id tłumaczenia z zasobów
     * @param idMessage - wiadomość powiadomienia jako id tłumaczenia z zasobów
     */
    public static void sendNotification(Context context, int idTitle, int idMessage) {
        if (context == null) {
            return;
        }

        Resources resources = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification");
        builder.setContentTitle(resources.getString(idTitle));
        builder.setContentText(resources.getString(idMessage));
        builder.setSmallIcon(R.drawable.ic_pets);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1, builder.build());

    }

    /**
     * Wysyłka powiadomień cyklicznych
     *
     * @param context - "this" z aktywności
     */
    public static void startPeriodicalNotifications(Context context) {
        if (context == null) {
            return;
        }

        Intent intent = new Intent(context, NotificationBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), CYCLIC_NOTIFICATION_INTERVAL * 60000, pendingIntent);
    }
}

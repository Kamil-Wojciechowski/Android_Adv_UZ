package com.example.swiatzwierzat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.swiatzwierzat.library.LibNotifications;

public class NotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LibNotifications.sendNotification(context, R.string.notification_reminder_title, R.string.notification_reminder_message);
    }
}

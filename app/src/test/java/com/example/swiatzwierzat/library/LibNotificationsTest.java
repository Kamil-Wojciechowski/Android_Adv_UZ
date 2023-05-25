package com.example.swiatzwierzat.library;

import android.content.Context;

import com.example.swiatzwierzat.ProductsActivity;
import com.example.swiatzwierzat.R;

import org.junit.Test;
import org.mockito.Mockito;

public class LibNotificationsTest {
    @Test
    public void checkSendToastStringWithNullContext() {
        LibNotifications.sendToast(null, "Message");
    }

    @Test
    public void checkSendToastStringWithNullContextAndMessage() {
        LibNotifications.sendToast(null, null);
    }

    @Test
    public void checkSendToastStringWithNullMessage() {
        Context context = Mockito.mock(ProductsActivity.class);
        LibNotifications.sendToast(context, null);
    }

    @Test
    public void checkSendToastIntWithNullContext() {
        LibNotifications.sendToast(null, R.string.notification_cart_item_added_message);
    }

    @Test
    public void checkSendNotificationWithNullContext() {
        LibNotifications.sendNotification(null, R.string.notification_reminder_title, R.string.notification_reminder_message);
    }

    @Test
    public void checkStartPeriodicalNotificationsWithNullContext() {
        LibNotifications.startPeriodicalNotifications(null);
    }
}

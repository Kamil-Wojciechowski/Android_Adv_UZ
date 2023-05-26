package com.example.swiatzwierzat;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.test.core.app.ApplicationProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class LanguagePlTest {
    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale("pl"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
    }
    @Test
    public void testLoginStringResource() {
        Context context = ApplicationProvider.getApplicationContext();
        String loginString = context.getString(R.string.login);
        Assert.assertEquals("Logowanie", loginString);
    }
}
package com.example.swiatzwierzat;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.res.Configuration;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class LanguageTest {
    Context context;

    private String getStringFromLocale(String locale) {
        Configuration configuration = new Configuration(this.context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));

        return this.context.createConfigurationContext(configuration).getResources().getString(R.string.app_name);
    }

    @Before
    public void before() {
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void checkBrandPL() {
        assertEquals(this.getStringFromLocale("pl"), "Świat Zwierząt");
    }

    @Test
    public void checkBrandEN() {
        assertEquals(this.getStringFromLocale("en"), "Animal World");
    }
}

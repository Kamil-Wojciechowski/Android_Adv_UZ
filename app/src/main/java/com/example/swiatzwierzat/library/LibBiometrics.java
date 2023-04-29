package com.example.swiatzwierzat.library;

import android.content.Context;
import android.content.res.Resources;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.swiatzwierzat.R;

import java.util.concurrent.Executor;

public class LibBiometrics {

    /**
     * Sprawdzenie czy dane urządzenie może używać poświadczeń biometrycznych
     *
     * @param context - "this" z aktywności
     */
    public static boolean canUseBiometrics(Context context) {
        if (context == null) {
            return false;
        }

        BiometricManager manager = BiometricManager.from(context);
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS;
    }

    /**
     * Wyświetlenie popup'u z logowaniem biometrycznym
     *
     * @param activity      - "this" z aktywności
     * @param idTitle       - tytuł jako id tłumaczenia z zasobów
     * @param idDescription - opis jako id tłumaczenia z zasobów
     * @param callback      - Zdarzenia, które mają się wykonać po udanej/nieudanej autoryzacji
     */
    public static void showBiometricsDialog(FragmentActivity activity, int idTitle, int idDescription, BiometricPrompt.AuthenticationCallback callback) {
        if (activity == null || callback == null) {
            return;
        }

        Resources resources = activity.getResources();
        Executor executor = ContextCompat.getMainExecutor(activity);
        BiometricPrompt prompt = new BiometricPrompt(activity, executor, callback);
        BiometricPrompt.PromptInfo information = new BiometricPrompt.PromptInfo.Builder().setTitle(resources.getString(idTitle)).setDescription(resources.getString(idDescription)).setNegativeButtonText(resources.getString(R.string.fingerprint_cancel)).build();
        prompt.authenticate(information);
    }
}

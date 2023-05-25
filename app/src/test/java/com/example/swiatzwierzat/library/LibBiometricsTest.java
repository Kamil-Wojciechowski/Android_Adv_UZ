package com.example.swiatzwierzat.library;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import com.example.swiatzwierzat.LoginActivity;
import com.example.swiatzwierzat.R;

import org.junit.Test;
import org.mockito.Mockito;

public class LibBiometricsTest {
    @Test
    public void checkCanUseBiometricsWithNullContext() {
        LibBiometrics.canUseBiometrics(null);
    }

    @Test
    public void checkShowBiometricDialogWithNullActivity() {
        LibBiometrics.showBiometricsDialog(null, R.string.fingerprint_login_title, R.string.fingerprint_login_description, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
    }

    @Test
    public void checkShowBiometricDialogWithNullCallback() {
        FragmentActivity activity = Mockito.mock(LoginActivity.class);
        LibBiometrics.showBiometricsDialog(activity, R.string.fingerprint_login_title, R.string.fingerprint_login_description, null);
    }

    @Test
    public void checkShowBiometricDialogWithNullActivityAndCallback() {
        LibBiometrics.showBiometricsDialog(null, R.string.fingerprint_login_title, R.string.fingerprint_login_description, null);
    }
}

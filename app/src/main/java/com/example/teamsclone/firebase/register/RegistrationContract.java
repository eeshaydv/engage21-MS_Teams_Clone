package com.example.teamsclone.firebase.register;

import android.app.Activity;

public interface RegistrationContract {
    interface View {
        void onRegistrationSuccess(String message);

        void onRegistrationFailure(String message);
    }

    interface Presenter {
        void register(Activity activity, String email, String password, String confirmPassword, String name);
    }

    interface Intractor {
        void performFirebaseRegistration(Activity activity, String email, String password, String name);

        void validateCredentials(Activity activity, String email, String password, String confirmPassword, String name);

        void storeCredentials(final Activity activity, final String email, final String password, String name);
    }

    interface onRegistrationListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}

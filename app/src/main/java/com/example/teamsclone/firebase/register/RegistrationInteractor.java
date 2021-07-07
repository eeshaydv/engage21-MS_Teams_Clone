package com.example.teamsclone.firebase.register;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationInteractor implements RegistrationContract.Intractor {

    private static final String TAG = RegistrationInteractor.class.getSimpleName();
    private RegistrationContract.onRegistrationListener mOnRegistrationListener;

    public RegistrationInteractor(RegistrationContract.onRegistrationListener onRegistrationListener) {
        this.mOnRegistrationListener = onRegistrationListener;
    }

    @Override
    public void performFirebaseRegistration(Activity activity, String email, String password, String name) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        mOnRegistrationListener.onFailure(task.getException().getMessage());
                    } else {
                        storeCredentials(activity, email, password, name);
                        mOnRegistrationListener.onSuccess("");
                    }
                });
    }

    @Override
    public void validateCredentials(Activity activity, String email, String password, String confirmPassword, String name) {
        if (name.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
            mOnRegistrationListener.onFailure("Please Fill In The Empty Fields");
            return;
        }

        if (password.equals("")) {
            mOnRegistrationListener.onFailure("Invalid Password");
            return;
        }

        if (password.length() < 6) {
            mOnRegistrationListener.onFailure("Short Password");
            return;
        }

        if (!confirmPassword.equals(password)) {
            mOnRegistrationListener.onFailure("Passwords Do Not Match");
            return;
        }

        performFirebaseRegistration(activity, email, password, name);

    }

    @Override
    public void storeCredentials(Activity activity, String email, String password, String name) {
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> details = new HashMap<>();
        // details.put("UID",uid);
        details.put("name", name);
        details.put("uid", uid);

        db.getReference().child("users").child(uid).updateChildren(details)
                .addOnCompleteListener(new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d("", "successfully written!");

                    }
                })
                .addOnFailureListener(e -> Log.w("", "Error writing document", e));

        writeSecuritySettings(uid);
    }

    private void writeSecuritySettings(String uid) {
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        Map<String, Object> stats = new HashMap<>();
        stats.put("fingerprint_unlock", "false");
        stats.put("keep_me_signed_in", "false");

        db.getReference().child("user_settings").child(uid).child("settings").child("security_settings").updateChildren(stats)
                .addOnSuccessListener(aVoid -> Log.d("", " successfully written in DB!"))
                .addOnFailureListener(e -> Log.w("", "Error writing to DB", e));

    }
}

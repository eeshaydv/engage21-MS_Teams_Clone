package com.example.teamsclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.teamsclone.base.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    Switch simpleSwitch;
    Switch simpleSwitch3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        securityPreference();
        Switch simpleSwitch = findViewById(R.id.simpleSwitch1);
        Switch simpleSwitch3 = findViewById(R.id.simpleSwitch3);

        simpleSwitch3.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(simpleSwitch3.isChecked()){
                fingerprintAuthentication2();
            }
            else {
                fingerprintAuthentication();

            }

        });

        simpleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(simpleSwitch.isChecked()){
                requireAuthFalse();
            }
            else {
                requireAuthTrue();
            }

        });

        Button button = findViewById(R.id.signOutButton);
        button.setOnClickListener(v-> {
            mAuth.signOut();
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            finish();
        });

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Button button2 = findViewById(R.id.deleteAccountButton);

        button2.setOnClickListener(v-> {
            assert user != null;
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                    finish();
                } else {

                    task.getException();
                }
            });
        });

    }
    @Override
    public void onBackPressed(){
        finish();
    }

    public void fingerprintAuthentication() {
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        Map<String, Object> Mset = new HashMap<>();
        Mset.put("fingerprint_unlock", "false");
        DatabaseReference Ref =  db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");
        Ref.updateChildren(Mset)
                .addOnSuccessListener(aVoid -> Log.d("", "DataSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("", "Error writing Snapshot", e));

    }

    public void fingerprintAuthentication2() {
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        Map<String, Object> Mset = new HashMap<>();
        Mset.put("fingerprint_unlock", "true");
        DatabaseReference Ref =  db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");
        Ref.updateChildren(Mset)
                .addOnSuccessListener(aVoid -> Log.d("", "DataSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("", "Error writing Snapshot", e));

    }

    public void requireAuthFalse(){
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        Map<String, Object> Mset = new HashMap<>();
        Mset.put("keep_me_signed_in", "true");
        DatabaseReference Ref =  db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");
        Ref.updateChildren(Mset)
                .addOnSuccessListener(aVoid -> Log.d("", "DataSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("", "Error writing Snapshot", e));
    }

    public void requireAuthTrue(){
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        Map<String, Object> Mset = new HashMap<>();
        Mset.put("keep_me_signed_in", "false");
        DatabaseReference Ref =  db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");
        Ref.updateChildren(Mset)
                .addOnSuccessListener(aVoid -> Log.d("", "DataSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w("", "Error writing Snapshot", e));
    }

    public void securityPreference() {
        //FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        showLoadingScreen();
        if(user != null){

            /*FirebaseDatabase db = FirebaseDatabase.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");
            }*/
            DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("user_settings").child(mAuth.getUid()).child("settings").child("security_settings");

            Switch ss = findViewById(R.id.simpleSwitch1);
            Switch ss2 = findViewById(R.id.simpleSwitch3);

            /*Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        if (snapshot.child("keep_me_signed_in").getValue().equals("true")) {
                            Toast.makeText(SettingsActivity.this, "keepme True", Toast.LENGTH_SHORT).show();
                            ss.setChecked(true);
                        }
                        if(snapshot.child("fingerprint_unlock").getValue().equals("true")){

                            ss2.setChecked(true);
                        }

                        hideLoadingScreen();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/


            Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                       if(snapshot.child("keep_me_signed_in").getValue().equals("true")){
                           Toast.makeText(SettingsActivity.this, "keepme True", Toast.LENGTH_SHORT).show();
                           ss.setChecked(true);
                       }
                       if(snapshot.child("fingerprint_unlock").getValue().equals("true")){

                           ss2.setChecked(true);
                       }
                       hideLoadingScreen();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
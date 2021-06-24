package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.teamsclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    public FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (user == null){
            Splash ob=new Splash();
            ob.start();
        }
        else {
            IsEmailVerified();
        }
    }

    private void IsEmailVerified() {
        if(user.isEmailVerified()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                FingerPrint();
            } else {
                noFingerPrint();
            }
        }
        else{
                startActivity(new Intent(SplashScreen.this, VerificationActivity.class));
                finish();
        }
    }

    private class Splash extends Thread
    {
        public void run()
        {
            try
            {
                sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();

        }
    }

    public void securityPreference(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            final FirebaseDatabase db = FirebaseDatabase.getInstance();
            //db.getReference().child("user_settings").child(email).child("settings").child("security_settings").updateChildren(stats)
            DatabaseReference Ref = db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");
            Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child("keep_me_signed_in").equals("false")){
                            if(snapshot.child("fingerprint_unlock").equals("true")){
                                startActivity(new Intent(SplashScreen.this, FingerPrintActivity.class));
                                finish();
                            }
                            else{
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                                finish();
                            }
                        }
                        else{
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SplashScreen.this,error.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void noFingerPrint() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            final FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference Ref = db.getReference().child("user_settings").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("settings").child("security_settings");

            Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        if(snapshot.child("keep_me_signed_in").equals("false")){
                            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                            finish();
                        }
                        else{
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.M)

    private void FingerPrint(){

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if(!fingerprintManager.isHardwareDetected()){

            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        }
        else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }else{

                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }else{

                    if (!keyguardManager.isKeyguardSecure()) {
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                        finish();
                    }else{
                        securityPreference();
                    }
                }
            }
        }
    }
}
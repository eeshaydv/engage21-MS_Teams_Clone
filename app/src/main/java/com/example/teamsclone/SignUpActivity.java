package com.example.teamsclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.teamsclone.base.BaseActivity;
import com.example.teamsclone.firebase.register.RegistrationContract;
import com.example.teamsclone.firebase.register.RegistrationPresenter;

public class SignUpActivity extends BaseActivity implements RegistrationContract.View  {

    private RegistrationPresenter mRegistrationPresenter;

    private String Uname,Uemail,Upassword,UconfirmPassword;
     EditText signInEmail;
     EditText signInPassword;
   EditText signInConfirmPassword;
    EditText Name;
    Button Register;
    TextView LoginText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mRegistrationPresenter = new RegistrationPresenter(this);
        signInEmail = findViewById(R.id.email);
        signInPassword = findViewById(R.id.password);
        signInConfirmPassword = findViewById(R.id.confirm_password);
        Name = findViewById(R.id.name);
        Register = findViewById(R.id.register_button);
        LoginText = findViewById(R.id.login_text);


        LoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });

        Register.setOnClickListener(view -> {
            Uname = Name.getText().toString().trim();

            Uemail = signInEmail.getText().toString().trim();

            Upassword = signInPassword.getText().toString().trim();

            UconfirmPassword = signInConfirmPassword.getText().toString().trim();

            showLoadingScreen();
            mRegistrationPresenter.register(this, Uemail, Upassword, UconfirmPassword, Uname);
        });

    }



    @Override
    public void onRegistrationSuccess(String message) {
        hideLoadingScreen();
        startActivity(new Intent(SignUpActivity.this, VerificationActivity.class));
        finish();
    }

    @Override
    public void onRegistrationFailure(String message) {
        hideLoadingScreen();
        onError(message);
    }

    @Override
    public void onBackPressed(){
        showLoadingScreen();
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
}
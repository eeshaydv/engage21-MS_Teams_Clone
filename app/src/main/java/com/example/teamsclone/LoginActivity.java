package com.example.teamsclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.teamsclone.base.BaseActivity;
import com.example.teamsclone.firebase.login.LoginContract;
import com.example.teamsclone.firebase.login.LoginPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class LoginActivity extends BaseActivity implements LoginContract.View {

    private LoginPresenter mLoginPresenter;
    private FirebaseAuth mAuth;
    private String email,password;

     EditText LoginEmail;
    EditText LoginPassword;
     Button LoginButton;
    TextView Registertext;
    TextView ForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginPresenter = new LoginPresenter(this);


        LoginEmail = findViewById(R.id.login_email);
        LoginPassword = findViewById(R.id.login_password);
        LoginButton = findViewById(R.id.login_button);
        Registertext = findViewById(R.id.register_account_link);
        ForgotPassword = findViewById(R.id.pass);

        Registertext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });




      LoginButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              email = LoginEmail.getText().toString();

              password = LoginPassword.getText().toString();

              initLogin(email, password);
          }
      });

    }

    private void initLogin(String email, String password) {
        mLoginPresenter.checkCredentials(this, email, password);
    }



    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public void onLoginSuccess(String message) {
        hideLoadingScreen();
        IsEmailVerified();
    }

    @Override
    public void onLoginFailure(String message) {
        hideLoadingScreen();
        onError(message);
    }

    private void IsEmailVerified(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser Muser = mAuth.getCurrentUser();
        assert Muser != null;
        if(Muser.isEmailVerified()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(LoginActivity.this, VerificationActivity.class));
            finish();
        }
    }
}
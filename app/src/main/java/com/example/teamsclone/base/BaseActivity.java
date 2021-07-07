package com.example.teamsclone.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamsclone.R;


public abstract class BaseActivity extends AppCompatActivity implements Base {
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onError(String message) {

        AlertDialog.Builder b1 = new AlertDialog.Builder(this);
        b1.setTitle("ERROR");
        b1.setMessage(message);
        b1.setCancelable(true);
        b1.setNegativeButton("Try Again", (dialog, id) -> dialog.cancel());
        AlertDialog a1 = b1.create();
        a1.show();
    }

    @Override
    public void showLoadingScreen() {
        ProgressBar p1 = findViewById(R.id.progress_bar);
        p1.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingScreen() {
        ProgressBar p1 = findViewById(R.id.progress_bar);
        p1.setVisibility(View.GONE);
    }
}

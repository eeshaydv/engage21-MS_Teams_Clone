package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.teamsclone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private CircleImageView GIcon;
    private EditText GName, GDescription;
    private FloatingActionButton GCreate;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .90), (int) (height * .65));
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.60f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        GIcon = findViewById(R.id.groupIcon);
        GName = findViewById(R.id.groupName);
        GDescription = findViewById(R.id.groupDescription);
        GCreate = findViewById(R.id.createGroup);


        mAuth = FirebaseAuth.getInstance();
        //checkUser();

        GCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateGroup();
                finish();
            }
        });


    }

    private void CreateGroup() {

        String name = GName.getText().toString().trim();
        String description = GDescription.getText().toString().trim();
        String timeStamp = "" + System.currentTimeMillis();
        String currUserId = mAuth.getCurrentUser().getUid();

        if (TextUtils.isEmpty(name)) {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Enter Group Name", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(CreateGroupActivity.this, R.color.red));
            snackbar.setTextColor(ContextCompat.getColor(CreateGroupActivity.this,R.color.white));
            snackbar.show();
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", currUserId + timeStamp);
        hashMap.put("groupName", name);
        hashMap.put("groupDescription", description);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("createdBy", currUserId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(currUserId + timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("uid", currUserId);
                        hashMap1.put("role", "creator");
                        hashMap1.put("timeStamp", timeStamp);

                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                        ref1.child(currUserId + timeStamp).child("Participants").child(currUserId)
                                .setValue(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Group Created", Snackbar.LENGTH_SHORT);
                                        snackbar.setBackgroundTint(ContextCompat.getColor(CreateGroupActivity.this, R.color.green));
                                        snackbar.setTextColor(ContextCompat.getColor(CreateGroupActivity.this,R.color.black));
                                        snackbar.show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CreateGroupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(ContextCompat.getColor(CreateGroupActivity.this, R.color.red));
                        snackbar.setTextColor(ContextCompat.getColor(CreateGroupActivity.this,R.color.white));
                        snackbar.show();
                    }
                });


    }

    private void checkUser() {
        FirebaseUser user = mAuth.getCurrentUser();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
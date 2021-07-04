package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private FirebaseAuth mAuth;
    private CircleImageView GIcon;
    private EditText GName,GDescription;
    private FloatingActionButton GCreate;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.90),(int) (height*.65));
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.60f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        GIcon =findViewById(R.id.groupIcon);
        GName =findViewById(R.id.groupName);
        GDescription =findViewById(R.id.groupDescription);
        GCreate =findViewById(R.id.createGroup);


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
       // mProgressDialog = new ProgressDialog(this);
        //mProgressDialog.setMessage("Creating Group - Please Wait");

         String name = GName.getText().toString().trim();
         String description = GDescription.getText().toString().trim();
         String timeStamp = ""+System.currentTimeMillis();
         String currUserId = mAuth.getCurrentUser().getUid();

         if(TextUtils.isEmpty(name)){
             Toast.makeText(this,"Enter Group Name",Toast.LENGTH_SHORT).show();
             return;
         }

        // mProgressDialog.show();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("groupId",currUserId+timeStamp);
        hashMap.put("groupName",name);
        hashMap.put("groupDescription",description);
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("createdBy",currUserId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(currUserId+timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        HashMap<String,String> hashMap1 = new HashMap<>();
                        hashMap1.put("uid",currUserId);
                        hashMap1.put("role","creator");
                        hashMap1.put("timeStamp",timeStamp);

                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                        ref1.child(currUserId+timeStamp).child("Participants").child(currUserId)
                                .setValue(hashMap1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                      //  mProgressDialog.dismiss();
                                        Toast.makeText(CreateGroupActivity.this,"Group Created!",Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                      //  mProgressDialog.dismiss();
                                        Toast.makeText(CreateGroupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(CreateGroupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
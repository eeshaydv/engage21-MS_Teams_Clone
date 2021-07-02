package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.example.teamsclone.Adapters.AdapterCallParticipants;
import com.example.teamsclone.Adapters.AdapterParticipantsAdd;
import com.example.teamsclone.R;
import com.example.teamsclone.models.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InviteCallParticipant extends AppCompatActivity {

    private RecyclerView rv;
    private ActionBar actionBar;
    private FirebaseAuth mAuth;
    private String userId,userName,roomId;
    private ArrayList<Friends> userList;
    private AdapterCallParticipants mAdapterParticipantAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_call_participant);
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        getWindow().setLayout((int)(width*.90),(int) (height*.65));
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.60f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        rv = findViewById(R.id.participant_call_add_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        rv.setLayoutManager(new LinearLayoutManager(InviteCallParticipant.this));
        userId = getIntent().getStringExtra("userId");
        roomId = getIntent().getStringExtra("roomId");
        getAllUsersList();
    }

    private void getAllUsersList() {
        userList = new ArrayList<>();
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("friends").child(userId);
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("users");

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    String friendId = ds.getKey().toString();

                     ref2.child(friendId).addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull DataSnapshot snapshot1) {
                             Friends friends  = snapshot1.getValue(Friends.class);
                             if(!mAuth.getUid().equals(friends.getUid()))userList.add(friends);
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError error) {

                         }
                     });


                }
                mAdapterParticipantAdd = new AdapterCallParticipants(InviteCallParticipant.this,userList,userId,roomId);
                rv.setAdapter(mAdapterParticipantAdd);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

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

public class GroupParticipantsAdd extends AppCompatActivity {

    private RecyclerView rv;
    private ActionBar actionBar;
    private FirebaseAuth mAuth;
    private String groupId, Gname, Grole;
    private ArrayList<Friends> userList;
    private AdapterParticipantsAdd mAdapterParticipantAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participants_add);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .90), (int) (height * .65));
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.60f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        rv = findViewById(R.id.groups_participant_add_recycler_view);
        groupId = getIntent().getStringExtra("groupId");
        Gname = getIntent().getStringExtra("groupName");
        Grole = getIntent().getStringExtra("groupRole");
        mAuth = FirebaseAuth.getInstance();
        rv.setLayoutManager(new LinearLayoutManager(GroupParticipantsAdd.this));

        String title = Gname + "(" + Grole + ")";

        //Toast.makeText(GroupParticipantsAdd.this,title+"(",Toast.LENGTH_SHORT).show();


        getAllUsersList();


    }

    private void getAllUsersList() {

        userList = new ArrayList<>();
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("users");

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Friends friends = ds.getValue(Friends.class);

                    if (!mAuth.getUid().equals(friends.getUid())) userList.add(friends);
                }
                mAdapterParticipantAdd = new AdapterParticipantsAdd(GroupParticipantsAdd.this, userList, groupId, Grole);
                rv.setAdapter(mAdapterParticipantAdd);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
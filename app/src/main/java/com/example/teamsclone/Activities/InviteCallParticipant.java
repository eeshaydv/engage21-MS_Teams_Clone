package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

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
    private String userId, roomId;
    private ArrayList<Friends> userList;
    private AdapterCallParticipants mAdapterParticipantAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_call_participant);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .90), (int) (height * .65));
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.60f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        rv = findViewById(R.id.participant_call_add_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(InviteCallParticipant.this));
        userId = getIntent().getStringExtra("userId");
        roomId = getIntent().getStringExtra("roomId");
        userList = new ArrayList<>();
        mAdapterParticipantAdd = new AdapterCallParticipants(InviteCallParticipant.this, userList, userId, roomId);
        rv.setAdapter(mAdapterParticipantAdd);
        rv.setHasFixedSize(true);
        getAllUsersList();
    }

    private void getAllUsersList() {

        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(userId);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        final long[] i = {0};
        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                long count = snapshot.getChildrenCount();

                Toast.makeText(InviteCallParticipant.this, "" + count, Toast.LENGTH_SHORT).show();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String friendId = ds.getKey().toString();

                    usersRef.child(friendId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            Friends friends = snapshot1.getValue(Friends.class);
                            userList.add(new Friends(friends.getName(), friends.getUid()));
                            i[0]++;
                            if (i[0] == count) {
                                mAdapterParticipantAdd.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
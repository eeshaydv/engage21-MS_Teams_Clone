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

import com.example.teamsclone.Adapters.AdapterCallParticipants;
import com.example.teamsclone.Adapters.AdapterScheduleCall;
import com.example.teamsclone.R;
import com.example.teamsclone.models.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleCallActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FirebaseAuth mAuth;
    private String userId;
    private ArrayList<Friends> userList;
    private AdapterScheduleCall mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_call);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .90), (int) (height * .65));
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.60f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        rv = findViewById(R.id.schedule_call_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid().toString();
        rv.setLayoutManager(new LinearLayoutManager(ScheduleCallActivity.this));
        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");
        userList = new ArrayList<>();
        mAdapter = new AdapterScheduleCall(ScheduleCallActivity.this, userList, userId);
        rv.setAdapter(mAdapter);
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

                Toast.makeText(ScheduleCallActivity.this, "" + count, Toast.LENGTH_SHORT).show();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String friendId = ds.getKey().toString();

                    usersRef.child(friendId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            Friends friends = snapshot1.getValue(Friends.class);
                            userList.add(new Friends(friends.getName(), friends.getUid()));
                            i[0]++;
                            if (i[0] == count) {
                                mAdapter.notifyDataSetChanged();

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
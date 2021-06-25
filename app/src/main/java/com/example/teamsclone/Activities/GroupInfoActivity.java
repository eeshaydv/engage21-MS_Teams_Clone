package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
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

public class GroupInfoActivity extends AppCompatActivity {

    private String groupId,grpName;
    private String myGrouprole = "";
    private ImageView groupIcon;
    private TextView editGroup,addParticipant,leaveGroup,grp_title,grp_desc;
    private RecyclerView participantsRecyclerView;
    private FirebaseAuth mAuth;
    private TextDrawable mDrawableBuilder;
    private ArrayList usersList;
    private AdapterParticipantsAdd mAdapterAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        groupId = getIntent().getStringExtra("groupId");
        myGrouprole = getIntent().getStringExtra("groupRole");
        groupIcon = findViewById(R.id.group_info_image_view);
        editGroup = findViewById(R.id.edit_group_text_view);
        grp_title = findViewById(R.id.group_title_text_view);
        grp_desc = findViewById(R.id.group_description_text_view);
        addParticipant = findViewById(R.id.group_add_participant_text_view);
        leaveGroup = findViewById(R.id.group_leave_text_view);
        participantsRecyclerView = findViewById(R.id.groups_participants_recycler_view);
        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(GroupInfoActivity.this));

        mAuth = FirebaseAuth.getInstance();

        loadGroupInfo();
        loadParticipants();

        addParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( myGrouprole.equals("admin") || myGrouprole.equals("creator")){
                    Intent intent1 = new Intent(GroupInfoActivity.this,GroupParticipantsAdd.class);
                    intent1.putExtra("groupId",groupId);
                    intent1.putExtra("groupName",grpName);
                    intent1.putExtra("groupRole",myGrouprole);
                    startActivity(intent1);
                }
                else Toast.makeText(GroupInfoActivity.this,"NULL",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadParticipants() {
        usersList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot ds : snapshot.getChildren()){
                    String uid = ds.child("uid").getValue().toString();
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("users");
                    ref2.orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            for(DataSnapshot d : snapshot1.getChildren()){
                                Friends user = d.getValue(Friends.class);
                                usersList.add(user);
                            }
                            mAdapterAdd = new AdapterParticipantsAdd(GroupInfoActivity.this,usersList,groupId,myGrouprole);
                            participantsRecyclerView.setAdapter(mAdapterAdd);
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


    private void loadGroupInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String gTitle = ds.child("groupName").getValue().toString();
                    String gDesc = ds.child("groupDescription").getValue().toString();
                    grpName = gTitle;
                    grp_title.setText(gTitle);
                    grp_desc.setText(gDesc);
                    char letter = gTitle.charAt(0);
                    letter = Character.toUpperCase(letter);
                    mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), R.color.colorAccent);
                    groupIcon.setImageDrawable(mDrawableBuilder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.teamsclone.Adapters.AdapterParticipantsAdd;
import com.example.teamsclone.R;
import com.example.teamsclone.models.Friends;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupInfoActivity extends AppCompatActivity {

    private String groupId, grpName;
    private String myGrouprole = "";
    private ImageView groupIcon, editTitle, editDesc;
    private TextView addParticipant, leaveGroup, grp_title, grp_desc;
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
        grp_title = findViewById(R.id.group_title_text_view);
        editDesc = findViewById(R.id.edit_desc);
        editTitle = findViewById(R.id.edit_title);
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
                if (myGrouprole.equals("admin") || myGrouprole.equals("creator")) {
                    Intent intent1 = new Intent(GroupInfoActivity.this, GroupParticipantsAdd.class);
                    intent1.putExtra("groupId", groupId);
                    intent1.putExtra("groupName", grpName);
                    intent1.putExtra("groupRole", myGrouprole);
                    startActivity(intent1);
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "You do not have the permission to add participants to the Group", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.red));
                    snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.white));
                    snackbar.show();
                }
            }
        });

        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dTitle = "", dDesc = "", dPosButton = "";

                if (myGrouprole.equals("creator")) {
                    dTitle = "Delete Group";
                    dDesc = "Are you Sure? The Group will be DELETED Permanently";
                    dPosButton = "DELETE";
                } else {
                    dTitle = "Leave Group";
                    dDesc = "Are you Sure you want to leave the Group Permanently";
                    dPosButton = "LEAVE";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle(dTitle)
                        .setMessage(dDesc)
                        .setPositiveButton(dPosButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (myGrouprole.equals("creator")) {
                                    deleteGroup();
                                } else {
                                    LeaveGroup();
                                }

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                            }
                        })
                        .show();
            }
        });

        editTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle("Edit Group Name");

                final EditText inputfield = new EditText(GroupInfoActivity.this);
                builder.setView(inputfield);
                builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (myGrouprole.equals("admin") || myGrouprole.equals("creator")) {

                            if ((inputfield.getText().toString()).isEmpty()) {
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Enter Name", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.red));
                                snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.white));
                                snackbar.show();
                            } else {

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(groupId).child("groupName").setValue(inputfield.getText().toString());

                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Name Updated", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.green));
                                snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.black));
                                snackbar.show();
                            }

                        }
                    }
                })
                        .setNegativeButton("CANCEl", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();

            }
        });

        editDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupInfoActivity.this);
                builder.setTitle("Edit Group Description");

                final EditText inputfield = new EditText(GroupInfoActivity.this);
                builder.setView(inputfield);
                builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (myGrouprole.equals("admin") || myGrouprole.equals("creator")) {

                            if ((inputfield.getText().toString()).isEmpty()) {
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Enter Description", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.red));
                                snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.white));
                                snackbar.show();
                            } else {

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                                ref.child(groupId).child("groupDescription").setValue(inputfield.getText().toString());

                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Description Updated", Snackbar.LENGTH_SHORT);
                                snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.green));
                                snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.black));
                                snackbar.show();
                            }

                        }
                    }
                })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();

            }
        });


    }

    private void LeaveGroup() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(mAuth.getUid())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Left Successfully", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.green));
                        snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.black));
                        snackbar.show();
                        startActivity(new Intent(GroupInfoActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.red));
                        snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.white));
                        snackbar.show();
                    }
                });
    }

    private void deleteGroup() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Group Deleted Successfully", Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.green));
                        snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.black));
                        snackbar.show();startActivity(new Intent(GroupInfoActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(ContextCompat.getColor(GroupInfoActivity.this, R.color.red));
                        snackbar.setTextColor(ContextCompat.getColor(GroupInfoActivity.this,R.color.white));
                        snackbar.show(); }
                });

    }

    private void loadParticipants() {
        usersList = new ArrayList<>();

        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef.child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uid = ds.child("uid").getValue().toString();
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                    usersRef.orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            for (DataSnapshot d : snapshot1.getChildren()) {
                                Friends user = d.getValue(Friends.class);
                                usersList.add(user);
                            }
                            mAdapterAdd = new AdapterParticipantsAdd(GroupInfoActivity.this, usersList, groupId, myGrouprole);
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

        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
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
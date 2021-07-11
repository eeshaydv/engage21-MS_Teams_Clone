package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.teamsclone.Adapters.AdapterGroupChat;
import com.example.teamsclone.R;
import com.example.teamsclone.base.BaseActivity;
import com.example.teamsclone.models.ModelGroupChat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;


public class GroupChatActivity extends AppCompatActivity {

    private String groupId, myGroupRole, groupname;
    private TextView groupName;
    private ImageView groupIcon, addButton, backButton;
    private EditText messageEditText;
    private FloatingActionButton sendButton;
    private TextDrawable mDrawableBuilder;
    private FirebaseAuth mAuth;
    private RecyclerView group_chat_activity_rv;
    private ArrayList<ModelGroupChat> groupChatList;
    private AdapterGroupChat adapterGroupChat;
    DatabaseReference groupsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupId = getIntent().getStringExtra("groupId");

        groupName = findViewById(R.id.group_name);
        groupIcon = findViewById(R.id.group_icon);
        addButton = findViewById(R.id.toolbar_add_button);
        messageEditText = findViewById(R.id.group_edit_text);
        sendButton = findViewById(R.id.group_send_fab);
        group_chat_activity_rv = findViewById(R.id.Group_chat_rv);
        mAuth = FirebaseAuth.getInstance();
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        group_chat_activity_rv.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));

        loadGroupInfo();
        loadGroupMessages();
        getinfo();

        backButton = findViewById(R.id.tool_grp_back_button);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(GroupChatActivity.this, "Type your Message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(message);
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (myGroupRole.equals("admin") || myGroupRole.equals("creator")) {
                    Intent intent1 = new Intent(GroupChatActivity.this, GroupParticipantsAdd.class);
                    intent1.putExtra("groupId", groupId);
                    intent1.putExtra("groupName", groupname);
                    intent1.putExtra("groupRole", myGroupRole);
                    startActivity(intent1);
                } else Toast.makeText(GroupChatActivity.this, "NULL", Toast.LENGTH_SHORT).show();
            }
        });

        groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(GroupChatActivity.this, GroupInfoActivity.class);
                intent1.putExtra("groupId", groupId);
                intent1.putExtra("groupRole", myGroupRole);
                startActivity(intent1);
            }
        });

    }

    private void getinfo() {
        String curr = mAuth.getCurrentUser().getUid();
        Toast.makeText(GroupChatActivity.this, curr, Toast.LENGTH_SHORT).show();

        groupsRef.child(groupId).child("Participants").child(curr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) myGroupRole = snapshot.child("role").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupChatActivity.this, "NULLy", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void loadGroupMessages() {
        groupChatList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        groupChatList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelGroupChat model = ds.getValue(ModelGroupChat.class);
                            groupChatList.add(model);
                        }

                        adapterGroupChat = new AdapterGroupChat(GroupChatActivity.this, groupChatList);
                        group_chat_activity_rv.setAdapter(adapterGroupChat);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void sendMessage(String message) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String saveCurrentTime = currentTime.format(calendar.getTime());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", mAuth.getCurrentUser().getUid());
        hashMap.put("message", message);
        hashMap.put("time", saveCurrentTime);
        hashMap.put("date", saveCurrentDate);
        hashMap.put("type", "text");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(saveCurrentDate + saveCurrentTime + mAuth.getCurrentUser().getUid())
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        messageEditText.setText("");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(GroupChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");

        ref.orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            groupname = ds.child("groupName").getValue().toString();
                            String groupdescription = ds.child("groupDescription").getValue().toString();
                            String timestamp = ds.child("timeStamp").getValue().toString();

                            groupName.setText(groupname);
                            char letter = groupname.charAt(0);
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

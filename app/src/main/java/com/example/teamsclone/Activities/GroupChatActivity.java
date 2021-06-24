package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

    private String groupId;
    private Toolbar mtoolBar;
    private TextView groupName;
    private ImageView attachIcon,groupIcon;
    private EditText messageEditText;
    private FloatingActionButton sendButton;
    private TextDrawable mDrawableBuilder;
    private FirebaseAuth mAuth;
    private RecyclerView group_chat_activity_rv;
    private ArrayList<ModelGroupChat>groupChatList;
    private AdapterGroupChat adapterGroupChat;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");

        mtoolBar = findViewById(R.id.toolbar_chat_room);
        groupName = findViewById(R.id.group_name);
        attachIcon = findViewById(R.id.group_attach_file);
        groupIcon = findViewById(R.id.group_icon);
        messageEditText = findViewById(R.id.group_edit_text);
        sendButton = findViewById(R.id.group_send_fab);
        group_chat_activity_rv = findViewById(R.id.Group_chat_rv);
        mAuth = FirebaseAuth.getInstance();
         ref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);

        loadGroupInfo();
       // loadGroupMessages();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString().trim();

                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this,"Type your Message",Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(message);
                }
            }
        });


    }
    /*

    public void loadGroupMessages() {
        groupChatList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        groupChatList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){
                            ModelGroupChat model = ds.getValue(ModelGroupChat.class);
                            groupChatList.add(model);
                        }

                        adapterGroupChat = new AdapterGroupChat(GroupChatActivity.this,groupChatList);
                        group_chat_activity_rv.setAdapter(adapterGroupChat);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

     */

    @Override
    protected void onStart() {
        super.onStart();
    FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<ModelGroupChat>()
                        .setQuery(ref, ModelGroupChat.class)
                        .build();


        final FirebaseRecyclerAdapter<ModelGroupChat, MessagesViewHolder> adapter
                = new FirebaseRecyclerAdapter<ModelGroupChat, MessagesViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull final MessagesViewHolder holder, int position, @NonNull ModelGroupChat model)
            {
                //final String userIDs = getRef(position).getKey();

                ref.child("Messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {

                            String message = dataSnapshot.child("message").getValue().toString();
                            String senderId = dataSnapshot.child("sender").getValue().toString();
                            String timeStamp = dataSnapshot.child("timeStamp").getValue().toString();

                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("users");
                            ref1.child(senderId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String User_name = snapshot.child("name").getValue().toString();
                                    holder.Name.setText(User_name);
                                    char letter = User_name.charAt(0);
                                    letter = Character.toUpperCase(letter);
                                    mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), R.color.colorAccent);
                                    holder.Icon.setImageDrawable(mDrawableBuilder);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            holder.Message.setText(message);
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timeStamp));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm:ss",cal).toString();
                            holder.Date.setText(dateTime);


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_message_layout, viewGroup, false);
                MessagesViewHolder viewHolder = new MessagesViewHolder(view);
                return viewHolder;
            }
        };

        group_chat_activity_rv.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name,Date,Message,likesCount,downVotesCount,commentsCount;
        AppCompatImageView Likes,DownVotes,Comments,More;
        ImageView Icon;


        public MessagesViewHolder(@NonNull View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.text_name);
            Message = itemView.findViewById(R.id.text_question);
            Date = itemView.findViewById(R.id.text_date);
            likesCount = itemView.findViewById(R.id.text_likes_count);
            downVotesCount = itemView.findViewById(R.id.text_downVotes_count);
            commentsCount = itemView.findViewById(R.id.text_chat_count);
            Likes = itemView.findViewById(R.id.view_likes);
            DownVotes = itemView.findViewById(R.id.view_downVotes);
            Comments = itemView.findViewById(R.id.view_chat);
            More = itemView.findViewById(R.id.view_settings);
            Icon = itemView.findViewById(R.id.avatar);
        }
    }

    private void sendMessage(String message) {
        String timestamp = ""+System.currentTimeMillis();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",mAuth.getCurrentUser().getUid());
        hashMap.put("message",message);
        hashMap.put("timeStamp",timestamp);
        hashMap.put("type","text");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(timestamp + mAuth.getCurrentUser().getUid())
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

                        Toast.makeText(GroupChatActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");

            ref.orderByChild("groupId").equalTo(groupId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()){
                                String groupname = ds.child("groupName").getValue().toString();
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

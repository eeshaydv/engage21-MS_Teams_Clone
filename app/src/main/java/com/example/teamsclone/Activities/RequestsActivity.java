package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.teamsclone.R;
import com.example.teamsclone.models.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestsActivity extends AppCompatActivity {

    private RecyclerView myRequestsList;
    private DatabaseReference ChatRequestsRef, UsersRef, ContactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private TextDrawable mDrawableBuilder;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("requests");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("friends");


        myRequestsList = findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(RequestsActivity.this));

        mToolbar = (Toolbar) findViewById(R.id.req_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("REQUESTS");
        mToolbar.setNavigationOnClickListener(view -> onBackPressed());


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(ChatRequestsRef.child(currentUserID), Friends.class)
                        .build();


        FirebaseRecyclerAdapter<Friends, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Friends, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestsViewHolder holder, int position, @NonNull Friends model) {
                        holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);


                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String type = dataSnapshot.getValue().toString();

                                    if (type.equals("received")) {
                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                final String requestUserName = dataSnapshot.child("name").getValue().toString();

                                                holder.userName.setText(requestUserName);
                                                char letter = requestUserName.charAt(0);
                                                int color = ColorGenerator.MATERIAL.getRandomColor();
                                                mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), color);
                                                holder.profileImage.setImageDrawable(mDrawableBuilder);

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence options[] = new CharSequence[]
                                                                {
                                                                        "Accept",
                                                                        "Cancel"
                                                                };

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(RequestsActivity.this);
                                                        builder.setTitle(requestUserName + " Request");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                if (i == 0) {
                                                                    ContactsRef.child(currentUserID).child(list_user_id).child("friends")
                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                ContactsRef.child(list_user_id).child(currentUserID).child("friends")
                                                                                        .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    Toast.makeText(RequestsActivity.this, "New Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                                if (i == 1) {
                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            Toast.makeText(RequestsActivity.this, "Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else if (type.equals("sent")) {
                                        Button request_sent_btn = holder.itemView.findViewById(R.id.request_accept_btn);
                                        request_sent_btn.setText("Request Sent");

                                        holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);

                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {


                                                final String requestUserName = dataSnapshot.child("name").getValue().toString();

                                                holder.userName.setText(requestUserName);
                                                char letter = requestUserName.charAt(0);
                                                letter = Character.toUpperCase(letter);
                                                int color = ColorGenerator.MATERIAL.getRandomColor();
                                                mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), color);
                                                holder.profileImage.setImageDrawable(mDrawableBuilder);


                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        CharSequence options[] = new CharSequence[]
                                                                {
                                                                        "Cancel Chat Request"
                                                                };

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(RequestsActivity.this);
                                                        builder.setTitle("Already Sent Request");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                if (i == 0) {
                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            Toast.makeText(RequestsActivity.this, "you have cancelled the chat request.", Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                                        builder.show();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        RequestsViewHolder holder = new RequestsViewHolder(view);
                        return holder;
                    }
                };

        myRequestsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView profileImage;
        Button AcceptButton, CancelButton;


        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);


            userName = itemView.findViewById(R.id.user_profile_name);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            AcceptButton = itemView.findViewById(R.id.request_accept_btn);
            CancelButton = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}
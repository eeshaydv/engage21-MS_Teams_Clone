package com.example.teamsclone.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.teamsclone.Activities.ChatActivity;
import com.example.teamsclone.R;
import com.example.teamsclone.models.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ChatsFragment extends Fragment {

    private View PrivateChatsView;
    private RecyclerView chatsList;
    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID = "";
    private TextDrawable mDrawableBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        PrivateChatsView = inflater.inflate(R.layout.chat_frgament, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("users");
        chatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return PrivateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(ChatsRef, Friends.class)
                        .build();


        FirebaseRecyclerAdapter<Friends, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Friends, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Friends model) {
                        final String usersIDs = getRef(position).getKey();


                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    final String retName = dataSnapshot.child("name").getValue().toString();

                                    holder.userName.setText(retName);
                                    char letter = retName.charAt(0);
                                    letter = Character.toUpperCase(letter);
                                    int color = ColorGenerator.MATERIAL.getRandomColor();
                                    mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), color);
                                    holder.profileImage.setImageDrawable(mDrawableBuilder);


                                    if (dataSnapshot.child("userState").hasChild("state")) {
                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if (state.equals("online")) {
                                            holder.onlineIcon.setVisibility(View.VISIBLE);
                                        } else if (state.equals("offline")) {
                                            holder.onlineIcon.setVisibility(View.GONE);
                                        }
                                    } else {
                                        holder.onlineIcon.setVisibility(View.GONE);
                                        ;
                                    }

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", usersIDs);
                                            chatIntent.putExtra("visit_user_name", retName);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

        chatsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        ImageView onlineIcon;
        TextView userName;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.users_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
            onlineIcon = itemView.findViewById(R.id.user_online_status);
        }
    }

}
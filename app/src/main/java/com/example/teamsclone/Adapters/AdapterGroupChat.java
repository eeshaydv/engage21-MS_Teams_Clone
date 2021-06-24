package com.example.teamsclone.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.teamsclone.R;
import com.example.teamsclone.models.ModelGroupChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderGroupChat> {

    private Context context;
    private ArrayList<ModelGroupChat>modelGroupChatList;
    private FirebaseAuth mAuth;
    private TextDrawable mDrawableBuilder;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> modelGroupChatList) {
        this.context = context;
        this.modelGroupChatList = modelGroupChatList;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.group_message_layout,parent,false);

        return new HolderGroupChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {

        ModelGroupChat model = modelGroupChatList.get(position);
        String message = model.getMessage();
        String timestamp = model.getTimestamp();
        String senderUid = model.getSender();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm:ss",cal).toString();


        holder.Message.setText(message);
        holder.Date.setText(dateTime);
        setUserName(model,holder);

    }

    private void setUserName(ModelGroupChat model, HolderGroupChat holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            String name = ds.child("name").getValue().toString();
                            holder.Name.setText(name);
                            char letter = name.charAt(0);
                            letter = Character.toUpperCase(letter);
                            mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), R.color.colorAccent);
                            holder.Icon.setImageDrawable(mDrawableBuilder);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return modelGroupChatList.size();
    }

    class HolderGroupChat extends RecyclerView.ViewHolder{

        TextView Name,Date,Message,likesCount,downVotesCount,commentsCount;
        AppCompatImageView Likes,DownVotes,Comments,More;
        ImageView Icon;

        public HolderGroupChat(@NonNull View itemView) {
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

}

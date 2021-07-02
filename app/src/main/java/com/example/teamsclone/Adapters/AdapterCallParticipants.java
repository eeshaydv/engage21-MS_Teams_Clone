package com.example.teamsclone.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.teamsclone.Activities.ChatActivity;
import com.example.teamsclone.R;
import com.example.teamsclone.models.Friends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdapterCallParticipants extends RecyclerView.Adapter<AdapterCallParticipants.HolderCallParticipants> {

    private Context context;
    private ArrayList<Friends> arrayList;
    private String userId,roomId;
    private TextDrawable mDrawableBuilder;
    private DatabaseReference rootRef;

    public AdapterCallParticipants(Context context, ArrayList<Friends> arrayList, String userId,String roomId) {
        this.context = context;
        this.arrayList = arrayList;
        this.userId = userId;
        this.roomId = roomId;
    }

    @NonNull
    @Override
    public HolderCallParticipants onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout_new,parent,false);
        return new AdapterCallParticipants.HolderCallParticipants(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCallParticipants holder, int position) {

        Friends friends = arrayList.get(position);
        String user = friends.getName();
        String uid = friends.getUid();

        holder.UserName.setText(user);
        char letter = user.charAt(0);
        letter = Character.toUpperCase(letter);
        int color = ColorGenerator.MATERIAL.getRandomColor();
        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter),color);
        holder.proPic.setImageDrawable(mDrawableBuilder);
        holder.UserStatus.setVisibility(View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage(uid);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class HolderCallParticipants extends RecyclerView.ViewHolder{

        private ImageView proPic;
        private TextView UserName,UserStatus;

        public HolderCallParticipants(@NonNull View itemView) {
            super(itemView);

            proPic = itemView.findViewById(R.id.users_profile_image_new);
            UserName = itemView.findViewById(R.id.user_profile_name_new);
            UserStatus = itemView.findViewById(R.id.user_profile_status_new);
        }
    }

    private void SendMessage(String uid) {
        String messageText  = " Hi,\n" + "\n join the Meet now with Room Id \n" + " "+roomId+" : ";
        String saveCurrentTime,saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());


            String messageSenderRef = "Messages/" + userId + "/" + uid;
            String messageReceiverRef = "Messages/" + uid + "/" + userId;

        rootRef = FirebaseDatabase.getInstance().getReference();

            DatabaseReference userMessageKeyRef = rootRef.child("Messages")
                    .child(userId).child(uid).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", userId);
            messageTextBody.put("to", uid);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(context,"Invite Sent Successfully!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context,"Error!",Toast.LENGTH_SHORT).show();
                    }

                }
            });

    }
}

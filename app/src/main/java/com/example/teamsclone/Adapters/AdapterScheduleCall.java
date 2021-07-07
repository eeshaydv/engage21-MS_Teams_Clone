package com.example.teamsclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.teamsclone.Activities.ScheduleDetailsActivity;
import com.example.teamsclone.R;
import com.example.teamsclone.models.Friends;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class AdapterScheduleCall extends RecyclerView.Adapter<AdapterScheduleCall.HolderScheduleCall> {

    private Context context;
    private ArrayList<Friends> arrayList;
    private String userId, roomId;
    private TextDrawable mDrawableBuilder;
    private DatabaseReference ScheduleCallRef;

    public AdapterScheduleCall(Context context, ArrayList<Friends> arrayList, String userId) {
        this.context = context;
        this.arrayList = arrayList;
        this.userId = userId;

    }

    @NonNull
    @Override
    public HolderScheduleCall onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout_new, parent, false);
        return new AdapterScheduleCall.HolderScheduleCall(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderScheduleCall holder, int position) {

        Friends friends = arrayList.get(position);
        String user = friends.getName();
        String uid = friends.getUid();

        holder.UserName.setText(user);
        char letter = user.charAt(0);
        letter = Character.toUpperCase(letter);
        int color = ColorGenerator.MATERIAL.getRandomColor();
        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), color);
        holder.proPic.setImageDrawable(mDrawableBuilder);
        holder.UserStatus.setVisibility(View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(context, ScheduleDetailsActivity.class);
                intent1.putExtra("receiverId", uid);
                intent1.putExtra("receiverName", user);
                context.startActivity(intent1);

            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class HolderScheduleCall extends RecyclerView.ViewHolder {

        private ImageView proPic;
        private TextView UserName, UserStatus;

        public HolderScheduleCall(@NonNull View itemView) {
            super(itemView);

            proPic = itemView.findViewById(R.id.users_profile_image_new);
            UserName = itemView.findViewById(R.id.user_profile_name_new);
            UserStatus = itemView.findViewById(R.id.user_profile_status_new);
        }
    }

}
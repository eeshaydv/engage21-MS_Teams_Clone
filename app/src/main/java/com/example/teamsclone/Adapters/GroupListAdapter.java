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
import com.example.teamsclone.Activities.GroupChatActivity;
import com.example.teamsclone.R;
import com.example.teamsclone.models.GroupList;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.HolderGroupList> {

    private Context context;
    private ArrayList<GroupList> groupChatLists;
    private TextDrawable mDrawableBuilder;

    public GroupListAdapter(Context context, ArrayList<GroupList> groupChatLists) {
        this.context = context;
        this.groupChatLists = groupChatLists;
    }

    @NonNull
    @Override
    public HolderGroupList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.group_item_view, parent, false);

        return new HolderGroupList(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupList holder, int position) {

        GroupList model = groupChatLists.get(position);
        String grp_name = model.getGroupName();
        String grp_id = model.getGroupId();

        holder.groupName.setText(grp_name);
        char letter = grp_name.charAt(0);
        letter = Character.toUpperCase(letter);
        int color = ColorGenerator.MATERIAL.getRandomColor();
        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), color);
        holder.groupIcon.setImageDrawable(mDrawableBuilder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupId", grp_id);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    class HolderGroupList extends RecyclerView.ViewHolder {

        private TextView groupName;
        private ImageView groupIcon;

        public HolderGroupList(@NonNull View itemView) {
            super(itemView);

            groupIcon = itemView.findViewById(R.id.group_icon_pic);
            groupName = itemView.findViewById(R.id.group_title);


        }
    }

}

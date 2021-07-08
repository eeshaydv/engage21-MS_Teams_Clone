package com.example.teamsclone.Adapters;

import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.teamsclone.R;
import com.example.teamsclone.models.CallHistory;
import com.example.teamsclone.models.Schedule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.HolderCallHistory>{

    private Context context;
    private ArrayList<CallHistory> callHistories;
    private TextDrawable mDrawableBuilder;
    private DatabaseReference CallRef;

    public CallHistoryAdapter(Context context, ArrayList<CallHistory> callHistories) {
        this.context = context;
        this.callHistories = callHistories;
    }

    @NonNull
    @Override
    public HolderCallHistory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.past_calls, parent, false);

        return new CallHistoryAdapter.HolderCallHistory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCallHistory holder, int position) {

        CallHistory model = callHistories.get(position);
        String calltype = model.getCallType();
        String callerId = model.getCallerid();
        String startDate = model.getStartdate();
        String startTime = model.getStarttime();

        if(calltype.equals("Incoming")){
            holder.callType.setImageResource(R.drawable.ic_baseline_call_received_24);
        }
        else{
            holder.callType.setImageResource(R.drawable.ic_baseline_call_made_24);
        }

        holder.dateandtime.setText(startDate +" "+startTime+" ");


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(callerId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    String name = snapshot.child("name").getValue().toString();
                    holder.callerName.setText(name);

                    char letter = name.charAt(0);
                    letter = Character.toUpperCase(letter);
                    int color = ColorGenerator.MATERIAL.getRandomColor();
                    mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter),color);
                    holder.callerIcon.setImageDrawable(mDrawableBuilder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

    @Override
    public int getItemCount() {
        return callHistories.size();
    }


    class HolderCallHistory extends RecyclerView.ViewHolder{

        ImageView callerIcon,callType;
        TextView callerName,dateandtime;

        public HolderCallHistory(@NonNull View itemView) {
            super(itemView);

            callerIcon = itemView.findViewById(R.id.caller_icon_pic);
            callType = itemView.findViewById(R.id.callType);
            callerName = itemView.findViewById(R.id.caller_name);
            dateandtime = itemView.findViewById(R.id.start_end_time);

        }
    }

}

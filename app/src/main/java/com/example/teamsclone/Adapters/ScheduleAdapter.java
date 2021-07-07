package com.example.teamsclone.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.teamsclone.Activities.RequestsActivity;
import com.example.teamsclone.R;
import com.example.teamsclone.models.Schedule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.HolderSchedule> {

    private Context context;
    private ArrayList<Schedule> schedules;
    private TextDrawable mDrawableBuilder;
    private DatabaseReference ScheduleRef;

    public ScheduleAdapter(Context context, ArrayList<Schedule> schedules) {
        this.context = context;
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public HolderSchedule onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.schedule_item_view, parent, false);

        return new ScheduleAdapter.HolderSchedule(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSchedule holder, int position) {

        Schedule schedule = schedules.get(position);
        String senderUid = schedule.getFromUid();
        String receiverUid = schedule.getToUid();
        String desc = schedule.getDescription();
        String status = schedule.getStatus();
        int hour = schedule.getHour();
        int min = schedule.getMin();
        int year = schedule.getYear();
        int month = schedule.getMonth();
        int day = schedule.getDay();

        holder.ScheduleItemDate.setText("Date : " + day + " -" +
                " " + month + " -" +
                " " + year + " ");

        holder.ScheduleItemTime.setText("Time : " + hour + " :" +
                " " + min + " ");

        holder.ScheduleItemDesc.setText(desc);
        holder.ScheduleItemStatus.setText(status);

        String currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (currUid.equals(senderUid)) {
            setReceiverUserName(schedule, holder);
        } else if (currUid.equals(receiverUid)) {
            setSenderUserName(schedule, holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String key = receiverUid + year + month + day + hour + min + senderUid;
                ScheduleRef = FirebaseDatabase.getInstance().getReference("schedules");

                if (status.equals("pending")) {
                    if (currUid.equals(senderUid)) {

                        //option of cancel schedule

                        CharSequence options[] = new CharSequence[]
                                {
                                        "DELETE"
                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete From Schedule");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    ScheduleRef.child(senderUid).child(key)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    ScheduleRef.child(receiverUid).child(key)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(context, "Schedule Deleted", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });

                                }
                            }
                        });
                        builder.show();

                    } else if (currUid.equals(receiverUid)) {
                        //option of cancel or accept schedule

                        CharSequence options[] = new CharSequence[]
                                {
                                        "ACCEPT",
                                        "DELETE"
                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("ACCEPT or DELETE MEET Schedule");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {

                                    ScheduleRef.child(senderUid).child(key).child("status")
                                            .setValue("Meet Scheduled")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ScheduleRef.child(receiverUid).child(key).child("status")
                                                                .setValue("Meet Scheduled")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(context, "Meet scheduled", Toast.LENGTH_SHORT).show();
                                                                            holder.ScheduleItemStatus.setText(schedule.getStatus());
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            });

                                } else if (which == 1) {

                                    ScheduleRef.child(senderUid).child(key)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    ScheduleRef.child(receiverUid).child(key)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(context, "Schedule Deleted", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();

                    }
                } else {
                    //cancel opt

                    CharSequence options[] = new CharSequence[]
                            {
                                    "DELETE"
                            };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete From Schedule");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {

                                ScheduleRef.child(senderUid).child(key)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                ScheduleRef.child(receiverUid).child(key)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(context, "Schedule Deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        });

                            }
                        }
                    });
                    builder.show();
                }

            }
        });

    }

    private void setSenderUserName(Schedule schedule, HolderSchedule holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(schedule.getFromUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String name = snapshot.child("name").getValue().toString();
                        holder.ScheduleItemName.setText(name);
                        char letter = name.charAt(0);
                        letter = Character.toUpperCase(letter);
                        int color = ColorGenerator.MATERIAL.getRandomColor();
                        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), color);
                        holder.IconScheduleItem.setImageDrawable(mDrawableBuilder);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setReceiverUserName(Schedule schedule, HolderSchedule holder) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        ref.child(schedule.getToUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String name = snapshot.child("name").getValue().toString();
                        holder.ScheduleItemName.setText(name);
                        char letter = name.charAt(0);
                        letter = Character.toUpperCase(letter);
                        int color = ColorGenerator.MATERIAL.getRandomColor();
                        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), color);
                        holder.IconScheduleItem.setImageDrawable(mDrawableBuilder);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    @Override
    public int getItemCount() {
        return schedules.size();
    }


    class HolderSchedule extends RecyclerView.ViewHolder {

        ImageView IconScheduleItem;
        TextView ScheduleItemName, ScheduleItemDesc, ScheduleItemDate, ScheduleItemTime, ScheduleItemStatus;

        public HolderSchedule(@NonNull View itemView) {
            super(itemView);

            IconScheduleItem = itemView.findViewById(R.id.avatar_schedule_item);
            ScheduleItemName = itemView.findViewById(R.id.Schedule_item_name);
            ScheduleItemStatus = itemView.findViewById(R.id.schedule_item_status);
            ScheduleItemDesc = itemView.findViewById(R.id.schedule_item_description);
            ScheduleItemDate = itemView.findViewById(R.id.schedule_item_date);
            ScheduleItemTime = itemView.findViewById(R.id.schedule_item_time);
        }
    }
}

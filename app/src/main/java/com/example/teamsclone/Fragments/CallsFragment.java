package com.example.teamsclone.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamsclone.Activities.ChatActivity;
import com.example.teamsclone.Activities.DashboardActivity;
import com.example.teamsclone.Activities.LoginActivity;
import com.example.teamsclone.Activities.MainActivity;
import com.example.teamsclone.Activities.ScheduleCallActivity;
import com.example.teamsclone.R;
import com.example.teamsclone.utilities.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class CallsFragment extends Fragment {

    private RecyclerView callsList ;
    private Button scheduledCalls,doneCalls;
    private FloatingActionButton call_fab,add_schedule,add_call;
    private int[] mColors;
    private boolean isRotate = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calls_fragment, null);

        callsList = v.findViewById(R.id.calls_list);
        callsList.setLayoutManager(new LinearLayoutManager(getContext()));
        scheduledCalls = v.findViewById(R.id.scheduled_calls);
        doneCalls = v.findViewById(R.id.done_calls);
        mColors = getResources().getIntArray(R.array.colors);
        call_fab = v.findViewById(R.id.calls_fab);
        add_call = v.findViewById(R.id.add_call_fab);
        add_schedule = v.findViewById(R.id.add_schedule_fab);

        ViewAnimation.init(add_call);
        ViewAnimation.init(add_schedule);


        scheduledCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduledCalls.setBackgroundResource(R.drawable.button_clicked);
                doneCalls.setBackgroundResource(R.drawable.button_unclick);

                scheduledCalls.setTextColor(mColors[38]);
                doneCalls.setTextColor(mColors[11]);
            }
        });

        doneCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneCalls.setBackgroundResource(R.drawable.button_clicked);
                scheduledCalls.setBackgroundResource(R.drawable.button_unclick);

                doneCalls.setTextColor(mColors[38]);
                scheduledCalls.setTextColor(mColors[11]);
            }
        });

        doneCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneCalls.setBackgroundResource(R.drawable.button_clicked);
                scheduledCalls.setBackgroundResource(R.drawable.button_unclick);

                doneCalls.setTextColor(mColors[38]);
                scheduledCalls.setTextColor(mColors[11]);
            }
        });

        call_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v,!isRotate);
                if(isRotate){
                    ViewAnimation.showIn(add_call);
                    ViewAnimation.showIn(add_schedule);
                }else{
                    ViewAnimation.showOut(add_schedule);
                    ViewAnimation.showOut(add_call);
                }
            }
        });

        add_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getContext(), DashboardActivity.class);
                startActivity(chatIntent);
            }
        });

        add_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getContext(), ScheduleCallActivity.class);
                startActivity(chatIntent);
            }
        });


        return v;
    }
}
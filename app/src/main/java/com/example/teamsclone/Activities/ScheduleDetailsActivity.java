package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.teamsclone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.protobuf.StringValue;

import java.util.Calendar;
import java.util.HashMap;

public class ScheduleDetailsActivity extends AppCompatActivity implements  DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button sDate,sTime;
    private TextView receiverName;
    private EditText rDesc;
    private String rId,rName;
    private FloatingActionButton saveSchedule;
    private boolean selectDate = false;
    private int day, month, year, hour, minute;
    private int myday, myMonth, myYear, myHour, myMinute;
    private DatabaseReference ScheduleRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_details_activity);

        sDate = findViewById(R.id.scheduled_date);
        sTime = findViewById(R.id.schedule_time);
        receiverName = findViewById(R.id.receiver_name);
        rDesc = findViewById(R.id.schedule_description);
        saveSchedule = findViewById(R.id.save_schedule);
        rId = getIntent().getStringExtra("receiverId").toString();
        rName = getIntent().getStringExtra("receiverName").toString();
        ScheduleRef = FirebaseDatabase.getInstance().getReference("schedules");

        receiverName.setText(rName);

        sDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleDetailsActivity.this, ScheduleDetailsActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });


        saveSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTheSchedule();
            }
        });


    }

    private void saveTheSchedule() {

        String Desc = ""+rDesc.getText().toString();
        String currUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("fromUid", currUid);
        hashMap.put("toUid",rId);
        hashMap.put("year", myYear);
        hashMap.put("month", myMonth);
        hashMap.put("day",myday);
        hashMap.put("hour",myHour);
        hashMap.put("min",myMinute);
        hashMap.put("description",Desc);
        hashMap.put("status","pending");

        String key = rId+myYear+myMonth+myday+myHour+myMinute+currUid;

        ScheduleRef.child(currUid).child(key).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        ScheduleRef.child(rId).child(key).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ScheduleDetailsActivity.this,"Scheduled!",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ScheduleDetailsActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ScheduleDetailsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ScheduleDetailsActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });


    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleDetailsActivity.this, ScheduleDetailsActivity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        sDate.setText(" " + myday + " -" +
                " " + myMonth + " -" +
                " " + myYear + " " +
                " " + myHour + " :" +
                " " + myMinute+" ");

        selectDate = true;
    }
}
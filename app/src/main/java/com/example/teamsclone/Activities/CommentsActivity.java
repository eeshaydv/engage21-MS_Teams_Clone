package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamsclone.R;
import com.example.teamsclone.base.Base;
import com.example.teamsclone.base.BaseActivity;
import com.example.teamsclone.models.Comments;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class CommentsActivity extends BaseActivity {

    private RecyclerView rv_c;
    private EditText CommentInputText;
    private String Comment_Key, current_user_id;
    private String uName;
    private FirebaseAuth mAuth;
    private DatabaseReference CommentsRef, UserRef;
    private FloatingActionButton cFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .90), (int) (height * .65));
        WindowManager.LayoutParams windowManager = getWindow().getAttributes();
        windowManager.dimAmount = 0.60f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        CommentInputText = findViewById(R.id.comment_input);
        cFab = findViewById(R.id.post_comment_button);


        rv_c = findViewById(R.id.comments_list);
        rv_c.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv_c.setLayoutManager(linearLayoutManager);
        rv_c.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        Comment_Key = getIntent().getExtras().get("commentID").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("users");
        CommentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(Comment_Key);

        UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uName = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        cFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String commentText = CommentInputText.getText().toString();

                if (commentText.isEmpty())
                    Toast.makeText(CommentsActivity.this, "Enter Comment", Toast.LENGTH_SHORT).show();
                else {


                    Calendar calFordDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    final String saveCurrentDate = currentDate.format(calFordDate.getTime());

                    Calendar calFordTime = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                    final String saveCurrentTime = currentTime.format(calFordDate.getTime());
                    int date = calFordDate.get(Calendar.DAY_OF_MONTH);
                    int month = calFordDate.get(Calendar.MONTH);

                    int year = calFordDate.get(Calendar.YEAR);
                    String RandomKey;

                    if (month < 10) {
                        if (date < 10)
                            RandomKey = Integer.toString(year) + "0" + Integer.toString(month) + "0" + Integer.toString(date) + saveCurrentTime;
                        else
                            RandomKey = Integer.toString(year) + "0" + Integer.toString(month) + Integer.toString(date) + saveCurrentTime;
                    } else {
                        if (date < 10)
                            RandomKey = Integer.toString(year) + Integer.toString(month) + "0" + Integer.toString(date) + saveCurrentTime;
                        else
                            RandomKey = Integer.toString(year) + Integer.toString(month) + Integer.toString(date) + saveCurrentTime;
                    }

                    HashMap commentsMap = new HashMap();
                    commentsMap.put("uid", current_user_id);
                    commentsMap.put("comment", commentText);
                    commentsMap.put("date", saveCurrentDate);
                    commentsMap.put("time", saveCurrentTime);
                    commentsMap.put("username", uName);
                    commentsMap.put("cid", RandomKey + current_user_id);
                    CommentsRef.child(RandomKey + current_user_id).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CommentsActivity.this, "Your comment is added successfully", Toast.LENGTH_SHORT).show();
                                CommentInputText.setText("");
                            } else {
                                Toast.makeText(CommentsActivity.this, "Error! Try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(CommentsRef, Comments.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CommentsActivity.CommentsViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_comments_layout, parent, false));

            }

            @Override
            protected void onBindViewHolder(@NonNull CommentsActivity.CommentsViewHolder commentsViewHolder, int i, @NonNull Comments comments) {
                CommentsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String uid = dataSnapshot1.child("uid").getValue().toString();
                            String check = dataSnapshot1.child("cid").getValue().toString();

                            if (uid.equals(current_user_id) && !(check.startsWith("delete"))) {
                                commentsViewHolder.mDelete.setVisibility(View.VISIBLE);
                            } else {
                                commentsViewHolder.mDelete.setVisibility(View.GONE);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                commentsViewHolder.mDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String cid = comments.getCid();

                        CommentsRef.child(cid).child("comment").setValue("The comment has been deleted");
                        CommentsRef.child(cid).child("cid").setValue("delete" + cid);
                        Toast.makeText(CommentsActivity.this, "Comment deleted....", Toast.LENGTH_SHORT).show();


                    }
                });
                commentsViewHolder.setUsername(comments.getUsername());
                commentsViewHolder.setComment(comments.getComment());
                commentsViewHolder.setDate(comments.getDate());
                commentsViewHolder.setTime(comments.getTime());


            }
        };

        rv_c.setAdapter(firebaseRecyclerAdapter);


    }


    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mDelete;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            mDelete = mView.findViewById(R.id.delete_comment);
        }

        public void setCid(String cid) {
            String myCid = cid;
        }

        public void setUsername(String username) {
            TextView myUserName = (TextView) mView.findViewById(R.id.comment_username);
            myUserName.setText(username);
        }

        public void setComment(String comment) {
            TextView myComment = (TextView) mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }

        public void setTime(String time) {
            TextView myTime = (TextView) mView.findViewById(R.id.comment_time);
            myTime.setText(" Time: " + time);
        }

        public void setDate(String date) {
            TextView myDate = (TextView) mView.findViewById(R.id.comment_date);
            myDate.setText(" Date: " + date);
        }


    }
}
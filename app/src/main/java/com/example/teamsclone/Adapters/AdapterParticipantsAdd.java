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
import com.example.teamsclone.R;
import com.example.teamsclone.models.Friends;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterParticipantsAdd extends RecyclerView.Adapter<AdapterParticipantsAdd.HolderParticipantsAdd> {

    private Context context;
    private ArrayList<Friends> arrayList;
    private String groupId, myGroupRole;
    private TextDrawable mDrawableBuilder;

    public AdapterParticipantsAdd(Context context, ArrayList<Friends> arrayList, String groupId, String myGroupRole) {
        this.context = context;
        this.arrayList = arrayList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantsAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_layout_new, parent, false);

        return new AdapterParticipantsAdd.HolderParticipantsAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantsAdd holder, int position) {

        Friends friends = arrayList.get(position);
        String user = friends.getName();
        String uid = friends.getUid();

        holder.UserName.setText(user);
        char letter = user.charAt(0);
        letter = Character.toUpperCase(letter);
        int color = ColorGenerator.MATERIAL.getRandomColor();
        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), color);
        holder.proPic.setImageDrawable(mDrawableBuilder);

        CheckIfUserExists(friends, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupId).child("Participants").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String prevRole = snapshot.child("role").getValue().toString();

                                    String opt[];

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Choose Options");

                                    if (myGroupRole.equals("creator")) {
                                        if (prevRole.equals("admin")) {
                                            opt = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(opt, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {

                                                        removeAdmin(friends);

                                                    } else {
                                                        removeParticipant(friends);
                                                    }
                                                }
                                            }).show();
                                        } else if (prevRole.equals("participant")) {
                                            opt = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(opt, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {

                                                        MakeAdmin(friends);

                                                    } else {
                                                        removeParticipant(friends);
                                                    }
                                                }
                                            }).show();
                                        }
                                    } else if (myGroupRole.equals("admin")) {
                                        if (prevRole.equals("creator")) {
                                            Toast.makeText(context, "Creator of the group", Toast.LENGTH_SHORT).show();
                                        } else if (prevRole.equals("admin")) {
                                            opt = new String[]{"Remove Admin", "Remove User"};
                                            builder.setItems(opt, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {

                                                        removeAdmin(friends);

                                                    } else {
                                                        removeParticipant(friends);
                                                    }
                                                }
                                            }).show();

                                        } else if (prevRole.equals("participant")) {
                                            opt = new String[]{"Make Admin", "Remove User"};
                                            builder.setItems(opt, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    if (which == 0) {

                                                        MakeAdmin(friends);

                                                    } else {
                                                        removeParticipant(friends);
                                                    }
                                                }
                                            }).show();
                                        }
                                    }

                                } else {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Add Participant")
                                            .setMessage("Want to add this User to the Group ?")
                                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    addParticipant(friends);
                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }
        });

    }

    private void addParticipant(Friends friends) {

        String timeStamp = "" + System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", friends.getUid());
        hashMap.put("role", "participant");
        hashMap.put("timeStamp", "" + timeStamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(friends.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void MakeAdmin(Friends friends) {

        String timeStamp = "" + System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", friends.getUid());
        hashMap.put("role", "admin");
        hashMap.put("timeStamp", "" + timeStamp);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(friends.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "User is now Admin", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void removeParticipant(Friends friends) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(friends.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "Removed User", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void removeAdmin(Friends friends) {

        String timeStamp = "" + System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", friends.getUid());
        hashMap.put("role", "participant");
        hashMap.put("timeStamp", "" + timeStamp);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(friends.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "User No longer Admin", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void CheckIfUserExists(Friends friends, HolderParticipantsAdd holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(friends.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {

                            String uRole = snapshot.child("role").getValue().toString();
                            holder.UserStatus.setText(uRole);

                        } else {
                            holder.UserStatus.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class HolderParticipantsAdd extends RecyclerView.ViewHolder {

        private ImageView proPic;
        private TextView UserName, UserStatus;

        public HolderParticipantsAdd(@NonNull View itemView) {
            super(itemView);

            proPic = itemView.findViewById(R.id.users_profile_image_new);
            UserName = itemView.findViewById(R.id.user_profile_name_new);
            UserStatus = itemView.findViewById(R.id.user_profile_status_new);
        }
    }
}

package com.example.teamsclone.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.teamsclone.Activities.LoginActivity;
import com.example.teamsclone.Activities.MainActivity;
import com.example.teamsclone.Activities.SettingsActivity;
import com.example.teamsclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileFragment extends Fragment {

    TextDrawable mDrawableBuilder;
    FirebaseDatabase db;

    FirebaseAuth mAuth;
    ImageView pro_frag;
    ImageView testI;
    TextView btn1,Name_frag,email_frag,logout_frag;
    String myProfileName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        View v = inflater.inflate(R.layout.profile_fragment, null);
        btn1 = (TextView) v.findViewById(R.id.settings);
        Name_frag = (TextView) v.findViewById(R.id.name_profile_frag);
        email_frag = (TextView) v.findViewById(R.id.email_profile_frag);
        logout_frag = v.findViewById(R.id.logout_profile);
        testI = v.findViewById(R.id.test);
        //db = FirebaseDatabase.getInstance();



        pro_frag =  v.findViewById(R.id.thumb_frag);




        mAuth= FirebaseAuth.getInstance();
        String currentUserID=mAuth.getCurrentUser().getUid();

        DatabaseReference userRef =FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());

        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                        myProfileName = String.valueOf(snapshot.child("name").getValue().toString());
                        Name_frag.setText(myProfileName);
//                        Toast.makeText(getContext(), "name", Toast.LENGTH_SHORT).show();
                        char letter = myProfileName.charAt(0);
                        letter = Character.toUpperCase(letter);
                        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), R.color.colorAccent);
                        pro_frag.setImageDrawable(mDrawableBuilder);
                 //       Toast.makeText(getContext(), "drawable", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        email_frag.setText(mAuth.getCurrentUser().getEmail());

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                ((MainActivity) getActivity()).startActivity(intent);

            }
        });

        logout_frag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    mAuth.signOut();
                    startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        return v;
    }
}
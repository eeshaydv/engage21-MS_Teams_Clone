package com.example.teamsclone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileFragment extends Fragment {

    TextDrawable mDrawableBuilder;
    FirebaseDatabase db;
    DatabaseReference userRef;
    FirebaseAuth mAuth;
    CircleImageView pro_frag;
    TextView btn1,Name_frag,email_frag;
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
        //db = FirebaseDatabase.getInstance();



        pro_frag = (CircleImageView) v.findViewById(R.id.thumb_frag);




        mAuth= FirebaseAuth.getInstance();
        String currentUserID=mAuth.getCurrentUser().getUid();

         userRef =FirebaseDatabase.getInstance().getReferenceFromUrl("https://teamsclone-965c9-default-rtdb.firebaseio.com/").child("users").child(currentUserID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                        myProfileName = snapshot.getValue().toString();
                        char letter = myProfileName.charAt(0);
                        letter = Character.toUpperCase(letter);
                        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), R.color.colorAccent);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pro_frag.setImageDrawable(mDrawableBuilder);
        Name_frag.setText(myProfileName);
        email_frag.setText(mAuth.getCurrentUser().getEmail());

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                ((MainActivity) getActivity()).startActivity(intent);

            }
        });

        return v;
    }
}
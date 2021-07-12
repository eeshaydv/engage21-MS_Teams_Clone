package com.example.teamsclone.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.teamsclone.OnBoardingActivities.IntroActivity;
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

import static android.content.Context.MODE_PRIVATE;

public class profileFragment extends Fragment {

    TextDrawable mDrawableBuilder;
    FirebaseAuth mAuth;
    ImageView pro_frag;
    ImageView testI;
    TextView btn1, Name_frag, email_frag, logout_frag,whats_new;
    String myProfileName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.profile_fragment, null);
        btn1 = (TextView) v.findViewById(R.id.settings);
        Name_frag = (TextView) v.findViewById(R.id.name_profile_frag);
        email_frag = (TextView) v.findViewById(R.id.email_profile_frag);
        logout_frag = v.findViewById(R.id.logout_profile);
        whats_new = v.findViewById(R.id.whats_new);
        testI = v.findViewById(R.id.test);

        pro_frag = v.findViewById(R.id.thumb_frag);

        mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getUid());

        userRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    myProfileName = String.valueOf(snapshot.child("name").getValue().toString());
                    Name_frag.setText(myProfileName);
                    char letter = myProfileName.charAt(0);
                    letter = Character.toUpperCase(letter);
                    mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), R.color.colorAccent);
                    pro_frag.setImageDrawable(mDrawableBuilder);

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

                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                ((MainActivity) getActivity()).startActivity(intent);

            }
        });

        whats_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("isIntroOpnend",false);
                editor.commit();
              Intent intent = new Intent(getContext(), IntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        logout_frag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        return v;
    }
}
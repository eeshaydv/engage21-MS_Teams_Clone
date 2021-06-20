package com.example.teamsclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.teamsclone.base.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

     CircleImageView pro;
    private TextDrawable mDrawableBuilder;
   private  FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loadFragment(new GroupsFragment());


        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        pro = findViewById(R.id.thumbnail);

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

         userRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://teamsclone-965c9-default-rtdb.firebaseio.com/");

        userRef.child("users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                        String myProfileName = snapshot.child("name").getValue(String.class);
                        char letter = myProfileName.charAt(0);
                        letter = Character.toUpperCase(letter);
                        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), R.color.white);

                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pro.setImageDrawable(mDrawableBuilder);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new GroupsFragment();
                break;

            case R.id.nav_call:
                fragment = new CallsFragment();
                break;

            case R.id.nav_chat:
                fragment = new ChatsFragment();
                break;

            case R.id.nav_requests:
                fragment = new FriendsFragment();
                break;

            case R.id.nav_profile:
                fragment = new profileFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}

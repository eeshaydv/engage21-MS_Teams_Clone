package com.example.teamsclone.Activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.teamsclone.Fragments.CallsFragment;
import com.example.teamsclone.Fragments.ChatsFragment;
import com.example.teamsclone.Fragments.FriendsFragment;
import com.example.teamsclone.Fragments.GroupsFragment;
import com.example.teamsclone.R;
import com.example.teamsclone.base.BaseActivity;
import com.example.teamsclone.Fragments.profileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener {

     ImageView pro;
    private TextDrawable mDrawableBuilder;
   private  FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference userRef;
    private DatabaseReference RootRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);


        loadFragment(new GroupsFragment());
        navigation.setSelectedItemId(R.id.nav_home);


        pro = findViewById(R.id.thumbnail);

        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID=mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

         userRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://teamsclone-965c9-default-rtdb.firebaseio.com/");

        userRef.child("users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                        String myProfileName = snapshot.child("name").getValue(String.class);
                        char letter = myProfileName.charAt(0);
                        letter = Character.toUpperCase(letter);
                        mDrawableBuilder = TextDrawable.builder().buildRound(String.valueOf(letter), R.color.colorAccent);
                        pro.setImageDrawable(mDrawableBuilder);
                       // Toast.makeText(MainActivity.this, "drawable", Toast.LENGTH_SHORT).show();

                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateUserStatus("online");

    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
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

        if(fragment==null)fragment = new GroupsFragment();

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

    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        RootRef.child("users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }
}

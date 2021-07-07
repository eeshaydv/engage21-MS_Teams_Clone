package com.example.teamsclone.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamsclone.Activities.CreateGroupActivity;
import com.example.teamsclone.Adapters.GroupListAdapter;
import com.example.teamsclone.R;
import com.example.teamsclone.models.GroupList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroupsFragment extends Fragment {
    private View groupFragmentView;
    private RecyclerView rv;
    FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private ArrayList<GroupList> groupChatLists;
    private GroupListAdapter groupListAdapter;

    public GroupsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        groupFragmentView = inflater.inflate(R.layout.home_fragment, container, false);

        rv = groupFragmentView.findViewById(R.id.groups_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        fab = groupFragmentView.findViewById(R.id.fab_group);
        mAuth = FirebaseAuth.getInstance();

        LoadGroupChatsList();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getContext(), CreateGroupActivity.class);
                startActivity(chatIntent);
            }
        });


        return groupFragmentView;
    }

    private void LoadGroupChatsList() {

        groupChatLists = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatLists.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if ((ds.child("Participants").child(mAuth.getCurrentUser().getUid()).exists())) {
                        GroupList model = ds.getValue(GroupList.class);
                        groupChatLists.add(model);
                    }
                }
                groupListAdapter = new GroupListAdapter(getActivity(), groupChatLists);
                rv.setAdapter(groupListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void SearchGroupChatsList(String query) {

        groupChatLists = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatLists.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (!ds.child("Participants").child(mAuth.getCurrentUser().getUid()).exists()) {
                        if (ds.child("groupName").toString().toLowerCase().equals(query.toLowerCase())) {
                            GroupList model = ds.getValue(GroupList.class);
                            groupChatLists.add(model);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}
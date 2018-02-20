package com.example.space.chatapp.ui.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.example.space.chatapp.R;
import com.example.space.chatapp.models.User;
import com.example.space.chatapp.ui.adapters.ListAllUserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewAllUsers;
    private ListAllUserAdapter userListingRecyclerAdapter;
    private DatabaseReference databaseReference;
    private DatabaseReference friendsReference;
    private DatabaseReference friendRequestReference;
    private List<User> users;
    private List<String> friendsListId;
    private String currentUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //map it to all users activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // for enable offline read of the database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("user");
        databaseReference.keepSynced(true);
        friendsReference = FirebaseDatabase.getInstance().getReference().child("friends");
        friendsReference.keepSynced(true);
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendRequestReference.keepSynced(true);

        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        friendsListId = new ArrayList<>();
        users = new ArrayList<>();

        getFriendListUid();
        getUsers();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewAllUsers = findViewById(R.id.all_users_recycler_view);
        recyclerViewAllUsers.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        userListingRecyclerAdapter = new ListAllUserAdapter(this, users, friendsListId, currentUid);
        recyclerViewAllUsers.setAdapter(userListingRecyclerAdapter);

    }

    private void getFriendListUid() {
        friendsReference.child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Iterator<DataSnapshot> keyList = dataSnapshot.getChildren().iterator();
                    while (keyList.hasNext()) {
                        DataSnapshot child = keyList.next();
                        friendsListId.add(child.getKey().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUsers() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
                while (dataSnapshotIterator.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshotIterator.next();
                    User user = new User();
                    user.setName(dataSnapshotChild.child("name").getValue(String.class));
                    user.setEmail(dataSnapshotChild.child("email").getValue(String.class));
                    user.setAvatar(dataSnapshotChild.child("avatar").getValue(String.class));
                    user.setBioText(dataSnapshotChild.child("bio").getValue(String.class));
                    user.setUid(dataSnapshotChild.getKey().toString());

                    // to view other users not the current user
                    if (!TextUtils.equals(user.getUid(), FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }
                userListingRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRefresh() {
        friendsListId.clear();
        users.clear();
        userListingRecyclerAdapter.notifyDataSetChanged();
        getFriendListUid();
        getUsers();
        userListingRecyclerAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
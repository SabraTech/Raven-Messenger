package com.example.space.ravenmessenger.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.example.space.ravenmessenger.R;
import com.example.space.ravenmessenger.models.User;
import com.example.space.ravenmessenger.ui.adapters.ListAllUserAdapter;
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

    public static final String ACTION_UPDATE_LIST = "com.example.space.chatapp.UPDATE_LIST";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewAllUsers;
    private ListAllUserAdapter userListingRecyclerAdapter;
    private DatabaseReference databaseReference;
    private DatabaseReference friendsReference;
    private DatabaseReference friendRequestReference;
    private List<User> users;
    private List<String> friendsListId, requestSentId, requestReceivedId;
    private String currentUid;
    private BroadcastReceiver updateLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //map it to all users activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        getSupportActionBar().setTitle(R.string.find_users);
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
        requestSentId = new ArrayList<>();
        requestReceivedId = new ArrayList<>();

        getNotifications();
        getFriendListUid();
        getUsers();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewAllUsers = findViewById(R.id.all_users_recycler_view);
        recyclerViewAllUsers.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        userListingRecyclerAdapter = new ListAllUserAdapter(this, users, friendsListId, requestSentId, requestReceivedId, currentUid);
        recyclerViewAllUsers.setAdapter(userListingRecyclerAdapter);

        updateLists = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String listType = intent.getStringExtra("type");
                String idupdated = intent.getExtras().getString("id");
                if (listType.equals("friends")) {
                    for (String id : friendsListId) {
                        if (idupdated.equals(id)) {
                            friendsListId.remove(id);
                            break;
                        }
                    }
                } else if (listType.equals("sent")) {
                    for (String id : requestSentId) {
                        if (idupdated.equals(id)) {
                            requestSentId.remove(id);
                            break;
                        }
                    }
                } else if (listType.equals("received")) {
                    for (String id : requestReceivedId) {
                        if (idupdated.equals(id)) {
                            requestReceivedId.remove(id);
                            break;
                        }
                    }
                } else if (listType.equals("friendAdd")) {
                    friendsListId.add(idupdated);
                } else if (listType.equals("sentAdd")) {
                    requestSentId.add(idupdated);
                }
                userListingRecyclerAdapter.notifyDataSetChanged();
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_LIST);
        this.registerReceiver(updateLists, intentFilter);

    }

    private void getNotifications() {
        friendRequestReference.child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Iterator<DataSnapshot> keyList = dataSnapshot.getChildren().iterator();
                    while (keyList.hasNext()) {
                        DataSnapshot child = keyList.next();
                        String requestType = child.child("request_type").getValue().toString();
                        if (requestType.equals("sent")) {
                            requestSentId.add(child.getKey().toString());
                        } else {
                            requestReceivedId.add(child.getKey().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                    user.setBioText(dataSnapshotChild.child("bioText").getValue(String.class));
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
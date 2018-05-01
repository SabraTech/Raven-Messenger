package com.example.space.ravenmessenger.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.space.ravenmessenger.R;
import com.example.space.ravenmessenger.data.FriendDB;
import com.example.space.ravenmessenger.data.StaticConfig;
import com.example.space.ravenmessenger.models.Friend;
import com.example.space.ravenmessenger.models.FriendList;
import com.example.space.ravenmessenger.models.Message;
import com.example.space.ravenmessenger.ui.adapters.ListFriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static int ACTION_START_CHAT = 1;
    private RecyclerView recyclerView;
    private ListFriendsAdapter adapter;
    private FriendList friendList = null;
    private ArrayList<String> friendsListId = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CountDownTimer detectFriendOnline;
    private LovelyProgressDialog progressDialog;
    private String currentUid;
    private DatabaseReference friendsReference;
    private DatabaseReference usersReference;
//    public static final String ACTION_DELETE_FRIEND;
//    private BroadcastReceiver deleteFriendReceiver;

    public FriendsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friendsReference = FirebaseDatabase.getInstance().getReference().child("friends");
        friendsReference.keepSynced(true);
        usersReference = FirebaseDatabase.getInstance().getReference().child("user");
        usersReference.keepSynced(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        detectFriendOnline = new CountDownTimer(System.currentTimeMillis(), StaticConfig.TIME_TO_REFRESH) {
            @Override
            public void onTick(long l) {
//                ServiceUtils.updateFriendStatus(getContext(), friendList);
//                ServiceUtils.updateUserStatus(getContext());
            }

            @Override
            public void onFinish() {

            }
        };

        if (friendList == null) {
            friendList = FriendDB.getInstance(getContext()).getFriendList();
            if (friendList.getFriendsList().size() > 0) {
                friendsListId = new ArrayList<>();
                for (Friend friend : friendList.getFriendsList()) {
                    friendsListId.add(friend.id);
                }
                detectFriendOnline.start();
            }
        }

        View layout = inflater.inflate(R.layout.fragment_friends, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = layout.findViewById(R.id.recycleFriendsList);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = layout.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new ListFriendsAdapter(getContext(), friendList, this);
        recyclerView.setAdapter(adapter);
        progressDialog = new LovelyProgressDialog(getContext());

        if (friendsListId == null) {
            friendsListId = new ArrayList<>();
            progressDialog.setCancelable(false)
                    .setIcon(getResources().getDrawable(R.drawable.ic_add_friend))
                    .setTitle("Get all friends...")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            getFriendListUid();
        }

        // here the broadcast of delete to remove the delete friends from the fragment


        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // disable the broadcast of deleted
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTION_START_CHAT == requestCode && data != null && ListFriendsAdapter.markMap != null) {
            ListFriendsAdapter.markMap.put(data.getStringExtra("idFriend"), false);
        }
    }

    @Override
    public void onRefresh() {
        friendsListId.clear();
        friendList.getFriendsList().clear();
        adapter.notifyDataSetChanged();
        FriendDB.getInstance(getContext()).dropDB();
        detectFriendOnline.cancel();
        getFriendListUid();
        swipeRefreshLayout.setRefreshing(false);
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
                    getAllFriendInfo(0);
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllFriendInfo(final int index) {
        if (index == friendsListId.size()) {
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
            detectFriendOnline.start();
        } else {
            final String id = friendsListId.get(index);
            usersReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        final Friend user = new Friend();
                        HashMap userInfoMap = (HashMap) dataSnapshot.getValue();
                        user.setName((String) userInfoMap.get("name"));
                        user.setEmail((String) userInfoMap.get("email"));
                        user.setAvatar((String) userInfoMap.get("avatar"));
                        HashMap messageMap = (HashMap) userInfoMap.get("message");
                        Message message = new Message();
                        message.idSender = (String) messageMap.get("idSender");
                        message.idReceiver = (String) messageMap.get("idReceiver");
                        message.idReceiverRoom = (String) messageMap.get("idReceiverRoom");
                        message.text = (String) messageMap.get("text");
                        message.type = (long) messageMap.get("type");
                        message.timestamp = (long) messageMap.get("timestamp");
                        user.setMessage(message);
                        user.id = id;
                        user.idRoom = id.compareTo(currentUid) > 0 ? (currentUid + id).hashCode() + "" : "" + (id + currentUid).hashCode();
                        friendList.getFriendsList().add(user);
                        FriendDB.getInstance(getContext()).addFriend(user);
                    }
                    getAllFriendInfo(index + 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}

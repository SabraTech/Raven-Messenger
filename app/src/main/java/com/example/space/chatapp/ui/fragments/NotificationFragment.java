package com.example.space.chatapp.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.space.chatapp.R;
import com.example.space.chatapp.models.Notifications;
import com.example.space.chatapp.models.Request;
import com.example.space.chatapp.ui.adapters.RequestAdapter;
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

public class NotificationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ACTION_REQUEST_REMOVED = "com.example.space.chatapp.REQUEST_REMOVED";
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private Notifications notifications;
    private ArrayList<String> requestUsersId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LovelyProgressDialog progressDialog;
    private String currentUid;
    private BroadcastReceiver requestRemoved;
    private DatabaseReference requestReference;
    private DatabaseReference usersRefererence;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestReference = FirebaseDatabase.getInstance().getReference().child("friend_request");
        requestReference.keepSynced(true);

        usersRefererence = FirebaseDatabase.getInstance().getReference().child("user");
        usersRefererence.keepSynced(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (notifications == null && requestUsersId == null) {
            requestUsersId = new ArrayList<>();
            notifications = new Notifications();
            getNotifications();
        }

        View layout = inflater.inflate(R.layout.fragment_notification, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView = layout.findViewById(R.id.requests_recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        swipeRefreshLayout = layout.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new RequestAdapter(getContext(), notifications, this);
        recyclerView.setAdapter(adapter);
        progressDialog = new LovelyProgressDialog(getContext());

        requestRemoved = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String idRemoved = intent.getExtras().getString("requestId");
                for (Request request : notifications.getRequests()) {
                    if (idRemoved.equals(request.getUid())) {
                        ArrayList<Request> requests = notifications.getRequests();
                        requests.remove(request);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_REQUEST_REMOVED);
        getContext().registerReceiver(requestRemoved, intentFilter);

        return layout;
    }

    @Override
    public void onRefresh() {
        requestUsersId.clear();
        notifications.getRequests().clear();
        adapter.notifyDataSetChanged();
        getNotifications();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(requestRemoved);
    }

    private void getNotifications() {
        requestReference.child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Iterator<DataSnapshot> keyList = dataSnapshot.getChildren().iterator();
                    while (keyList.hasNext()) {
                        DataSnapshot child = keyList.next();
                        String requestType = child.child("request_type").getValue().toString();
                        if (requestType.equals("received")) {
                            requestUsersId.add(child.getKey().toString());
                        }
                        getAllRequestUsersInfo(0);
                    }
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllRequestUsersInfo(final int index) {
        if (index == requestUsersId.size()) {
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            final String id = requestUsersId.get(index);
            usersRefererence.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Request request = new Request();
                        HashMap userInfoMap = (HashMap) dataSnapshot.getValue();
                        request.setAvatar((String) userInfoMap.get("avatar"));
                        request.setName((String) userInfoMap.get("name"));
                        request.setStatus((String) userInfoMap.get("bio"));
                        request.setUid(id);
                        notifications.getRequests().add(request);
                    }
                    getAllRequestUsersInfo(index + 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}

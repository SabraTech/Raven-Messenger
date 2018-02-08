package com.example.space.chatapp.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.space.chatapp.R;
import com.example.space.chatapp.models.User;
import com.example.space.chatapp.ui.activities.VisitedProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class NotificationFragment extends Fragment {

    private RecyclerView requestRecyclerView ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View mainView;
    private DatabaseReference requestsReference ;
    private DatabaseReference friendsReference;

    private String currentUid ,visitUserId;
    private List<User> users = new ArrayList<>();

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       mainView= inflater.inflate(R.layout.fragment_notification, container, false);

       //get recycler view
       requestRecyclerView =mainView.findViewById(R.id.requests_recycler_view);
       requestRecyclerView.setHasFixedSize(true);
       LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
       linearLayoutManager.setReverseLayout(true);
       linearLayoutManager.setStackFromEnd(true);
       requestRecyclerView.setLayoutManager(linearLayoutManager);

       //get swipeRefreshLayout
        swipeRefreshLayout = mainView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        Log.e(NotificationFragment.class.getName(), "********* in on create " );

        //get current user
        currentUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.e(NotificationFragment.class.getName(), "current : "+currentUid );
        //get his requests
        friendsReference=FirebaseDatabase.getInstance().getReference().child("friends");
        requestsReference= FirebaseDatabase.getInstance()
                .getReference()
                .child("friend_request")
                .child(currentUid);
        requestsReference.addListenerForSingleValueEvent(requestListener);
        return mainView ;
    }


         private ValueEventListener requestListener = new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(NotificationFragment.class.getName(), "in listener");
                if (dataSnapshot.exists()){
                //get children(other ids)
                Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();

                while (dataSnapshotIterator.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshotIterator.next();
                    String requestType = dataSnapshotChild.child("request_type").getValue(String.class);
                    Log.e(NotificationFragment.class.getName(), "in Requests type " + requestType);
                    if (requestType.equals("received")) {
                        String requestUser = dataSnapshotChild.getKey().toString();
                        Log.e(NotificationFragment.class.getName(), "in Requests id " + requestUser);

                        //get data of this user
                        FirebaseDatabase.getInstance().getReference()
                                .child("user")
                                .child(requestUser)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = new User();
                                        user.setName(dataSnapshot.child("name").getValue(String.class));
                                        Log.e(NotificationFragment.class.getName(), "in get username " + user.getName());
                                        user.setEmail(dataSnapshot.child("email").getValue(String.class));
                                        user.setAvatar(dataSnapshot.child("avatar").getValue(String.class));
                                        user.setUid(dataSnapshot.getKey().toString());
                                        users.add(user);

                                        swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        });

                                        RequestRecyclerAdapter requestRecyclerAdapter = new RequestRecyclerAdapter(users);
                                        requestRecyclerView.setAdapter(requestRecyclerAdapter);
                                        requestRecyclerAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }
                }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };




    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    /*
       adapter class
       I define it here to move between activities
        */
    public class RequestRecyclerAdapter extends RecyclerView.Adapter<RequestRecyclerAdapter.RequestViewHolder> {

        private List<User> users;

        public RequestRecyclerAdapter(List<User> users) {
            this.users = users;
        }

        public void add(User user) {
            users.add(user);
            notifyItemInserted(users.size() - 1);
        }

        private void setProfileImage(ImageView profileImage, String img) {
            try {
                if (img.equals("default")) {
                    profileImage.setImageResource(R.drawable.default_avatar);
                } else {
                    Bitmap src;
                    byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                    src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    Drawable d = new BitmapDrawable(src);
                    profileImage.setImageDrawable(d);
                }

            } catch (Exception e) {
            }

        }

        @Override
        public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //set to new item layout
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
            return new RequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RequestViewHolder holder, final int position) {
            User user = users.get(position);
            //get data
            holder.txtUsername.setText(user.getName());
            //todo holder.txtStatus.setText();
            setProfileImage(holder.imageViewProfile, user.getAvatar());
            //when click on this item -> go to his profile
            visitUserId = getUser(position).getUid();
            holder.holderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent visitedProfileIntent = new Intent(getActivity(), VisitedProfileActivity.class);
                    visitedProfileIntent.putExtra("visit", visitUserId);
                    startActivity(visitedProfileIntent);
                }
            });
            //handle buttons
            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptFriendRequestMethod(visitUserId ,holder.acceptBtn ,holder.cancelBtn);
                }
            });
            holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelFriendRequestMethod(visitUserId ,holder.acceptBtn ,holder.cancelBtn);
                }
            });



        }

        @Override
        public int getItemCount() {
            if (users != null) {
                return users.size();
            }
            return 0;
        }

        public User getUser(int position) {
            return users.get(position);
        }


        public  class RequestViewHolder extends RecyclerView.ViewHolder{

            TextView txtStatus, txtUsername;
            ImageView imageViewProfile;
            View holderView;
            Button acceptBtn,cancelBtn ;

            public RequestViewHolder(View itemView) {
                super(itemView);
                holderView = itemView.findViewById(R.id.request_view);
                txtUsername = itemView.findViewById(R.id.request_profile_user_name);
                imageViewProfile = itemView.findViewById(R.id.request_profile_image);
                acceptBtn=itemView.findViewById(R.id.request_accept_btn);
                cancelBtn=itemView.findViewById(R.id.request_cancel_btn);
            }
        }
    }
    private void acceptFriendRequestMethod(final String receiverUid , final Button acceptBtn , final Button cancelBtn) {
        //get current date to save it as the friendship date
        Calendar friendsDate = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        final String saveCurrentDate = currentDate.format(friendsDate.getTime());
        //make first sender-Receiver node
        friendsReference.child(currentUid)
                .child(receiverUid)
                .setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //make Second sender-Receiver node
                        friendsReference.child(receiverUid)
                                .child(currentUid)
                                .setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //delete Friend request
                                        FirebaseDatabase.getInstance().getReference().child("friend_request").child(currentUid)
                                                .child(receiverUid)
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //2-remove receiver->sender node
                                                            FirebaseDatabase.getInstance().getReference().child("friend_request").child(receiverUid)
                                                                    .child(currentUid)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            acceptBtn.setEnabled(true);
                                                                            acceptBtn.setText("friend");
                                                                            //hide and disable decline btn in general
                                                                            cancelBtn.setVisibility(View.INVISIBLE);
                                                                            cancelBtn.setEnabled(false);
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });

                                    }
                                });

                    }
                });



    }
    private void cancelFriendRequestMethod(final String receiverUid , final Button acceptBtn , final Button cancelBtn) {
        //1-remove sender->receiver node
        FirebaseDatabase.getInstance().getReference().child("friend_request").child(currentUid)
                .child(receiverUid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //2-remove receiver->sender node
                            FirebaseDatabase.getInstance().getReference().child("friend_request").child(receiverUid)
                                    .child(currentUid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            acceptBtn.setVisibility(View.INVISIBLE);
                                            cancelBtn.setVisibility(View.INVISIBLE);

                                            Log.d(NotificationFragment.class.getName(), "onComplete: delete friend request");
                                        }
                                    });
                        }
                    }
                });
    }

}

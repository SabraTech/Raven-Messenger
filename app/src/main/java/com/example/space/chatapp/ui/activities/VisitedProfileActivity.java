package com.example.space.chatapp.ui.activities;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.space.chatapp.R;
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
import java.util.Calendar;
import java.util.HashMap;

public class VisitedProfileActivity extends AppCompatActivity {

    private Button sendRequestBtn, declineRequestbtn;
    private TextView profileName, profileStatus;
    private ImageView profileImage;

    private DatabaseReference userReference;
    private DatabaseReference friendRequestReference;
    private DatabaseReference friendsReference;
    private DatabaseReference notificationsReference;


    private String currentState; // {notFriends, requestSent,requestReceived or friends}
    private String senderUid, receiverUid;
    /*
    current -> sender
    visited -> receiver
     */
    private String personName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_profile);
        currentState = "notFriends";

        //get visited user id
        receiverUid = getIntent().getExtras().get("visit").toString();
        userReference = FirebaseDatabase.getInstance().getReference().child("user").child(receiverUid);

        //get current user id
        senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //get objects from layout
        sendRequestBtn = findViewById(R.id.send_request_btn);
        declineRequestbtn = findViewById(R.id.decline_request_btn);
        profileImage = findViewById(R.id.visited_profile_image);
        profileName = findViewById(R.id.visited_profile_name);
        profileStatus = findViewById(R.id.visited_profile_status);

        //get friend request and friends node from firebase
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendsReference = FirebaseDatabase.getInstance().getReference().child("friends");
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        notificationsReference.keepSynced(true);

        //hide and disable decline btn in general
        declineRequestbtn.setVisibility(View.INVISIBLE);
        declineRequestbtn.setEnabled(false);

        //clicking on sent request button
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //first hide it
                sendRequestBtn.setEnabled(false);
                if (currentState.equals("notFriends")) {
                    sendRequestBtn.setText("Send Friend Request");
                    sendFriendRequestMethod();
                } else if (currentState.equals("requestSent")) {
                    cancelFriendRequestMethod();
                } else if (currentState.equals("requestReceived")) {
                    acceptFriendRequestMethod();
                } else if (currentState.equals("friends")) {
                    unfriendMethod();
                }

            }
        });


        //get visited user data from fire base to app
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name and image and put them
                personName = dataSnapshot.child("name").getValue(String.class);
                String image = dataSnapshot.child("avatar").getValue(String.class);
                //todo String status =dataSnapshot.child("status").getValue(String.class);
                profileName.setText(personName);
                setProfileImage(image);
                setActionBarTitle(personName);

                /*handle send friend request button changing */
                //1-If there is Friend request between them
                friendRequestReference.child(senderUid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //if there is friend request with this user
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild(receiverUid)) {
                                        String requestType = dataSnapshot.child(receiverUid)
                                                .child("request_type").getValue().toString();
                                        if (requestType.equals("sent")) {
                                            currentState = "requestSent";
                                            sendRequestBtn.setText("Cancel Friend Request");
                                            //hide and disable decline btn in general
                                            declineRequestbtn.setVisibility(View.INVISIBLE);
                                            declineRequestbtn.setEnabled(false);
                                        } else if (requestType.equals("received")) {
                                            currentState = "requestReceived";
                                            sendRequestBtn.setText("Accept Friend Request");
                                            //only here decline will be visible
                                            //hide and disable decline btn in general
                                            declineRequestbtn.setVisibility(View.VISIBLE);
                                            declineRequestbtn.setEnabled(true);
                                            declineRequestbtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    declineFriendRequestMethod();
                                                }
                                            });
                                        }

                                    }
                                }//2-If there are friends
                                else {
                                    //to handle unfriend
                                    friendsReference.child(senderUid)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(receiverUid)) {
                                                        currentState = "friends";
                                                        sendRequestBtn.setText("unfriend " + personName);
                                                        //hide and disable decline btn
                                                        declineRequestbtn.setVisibility(View.INVISIBLE);
                                                        declineRequestbtn.setEnabled(false);

                                                    } else {
                                                        sendRequestBtn.setText("Send Friend Request");

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(personName);
    }

    private void setProfileImage(String img) {
        //decode from string ->bitmap->drawable
        try {
            Resources res = getResources();
            Bitmap src;
            if (!img.equals("default")) {

                byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Drawable d = new BitmapDrawable(getResources(), src);
                profileImage.setImageDrawable(d);
            }

        } catch (Exception e) {
        }
    }

    private void sendFriendRequestMethod() {
        //1-send request and create first node
        sendRequestBtn.setText("Send Friend Request");
        friendRequestReference.child(senderUid)
                .child(receiverUid)
                .child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //2-tell the receiver and create second node
                            friendRequestReference.child(receiverUid)
                                    .child(senderUid)
                                    .child("request_type")
                                    .setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //notifications
                                                HashMap<String,String> notificationsData = new HashMap<>();
                                                notificationsData.put("from",senderUid);
                                                notificationsData.put("type ","request");
                                                notificationsReference.child(receiverUid)
                                                        .push() //will be given unique random key
                                                        .setValue(notificationsData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //update the UI buttons
                                                                if(task.isSuccessful()) {
                                                                    sendRequestBtn.setEnabled(true);
                                                                    currentState = "requestSent";
                                                                    sendRequestBtn.setText("Cancel Friend Request");
                                                                    //hide and disable decline btn
                                                                    declineRequestbtn.setVisibility(View.INVISIBLE);
                                                                    declineRequestbtn.setEnabled(false);
                                                               }
                                                            }
                                                        });

                                            }
                                        }
                                    });

                        }
                    }
                });
    }

    private void cancelFriendRequestMethod() {
        //1-remove sender->receiver node
        friendRequestReference.child(senderUid)
                .child(receiverUid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //2-remove receiver->sender node
                            friendRequestReference.child(receiverUid)
                                    .child(senderUid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            sendRequestBtn.setEnabled(true);
                                            currentState = "notFriends";
                                            sendRequestBtn.setText("Send Friend Request");
                                            //hide and disable decline btn in general
                                            declineRequestbtn.setVisibility(View.INVISIBLE);
                                            declineRequestbtn.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptFriendRequestMethod() {
        //get current date to save it as the friendship date
        Calendar friendsDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        final String saveCurrentDate = currentDate.format(friendsDate.getTime());
        //make first sender-Receiver node
        friendsReference.child(senderUid)
                .child(receiverUid)
                .setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //make Second sender-Receiver node
                        friendsReference.child(receiverUid)
                                .child(senderUid)
                                .setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //delete Friend request
                                        friendRequestReference.child(senderUid)
                                                .child(receiverUid)
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //2-remove receiver->sender node
                                                            friendRequestReference.child(receiverUid)
                                                                    .child(senderUid)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            sendRequestBtn.setEnabled(true);
                                                                            currentState = "friends";
                                                                            sendRequestBtn.setText("unfriend " + personName);
                                                                            //hide and disable decline btn in general
                                                                            declineRequestbtn.setVisibility(View.INVISIBLE);
                                                                            declineRequestbtn.setEnabled(false);
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

    private void declineFriendRequestMethod() {
        cancelFriendRequestMethod();

    }

    private void unfriendMethod() {
        //remove first node
        friendsReference.child(senderUid).child(receiverUid).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //rempve second node
                            friendsReference
                                    .child(receiverUid)
                                    .child(senderUid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            sendRequestBtn.setEnabled(true);
                                            currentState = "notFriends";
                                            sendRequestBtn.setText("Send Friend Request");
                                            //hide and disable decline btn
                                            declineRequestbtn.setVisibility(View.INVISIBLE);
                                            declineRequestbtn.setEnabled(false);

                                        }
                                    });
                        }

                    }
                });
    }

}
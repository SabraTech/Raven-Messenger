package com.example.space.chatapp.ui.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.models.Chat;
import com.example.space.chatapp.ui.adapters.ChatRecyclerAdapter;
import com.example.space.chatapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements TextView.OnEditorActionListener {

    private static final String TAG = "ChatInteractor";

    private RecyclerView recyclerViewChat;
    private EditText txtMessage;
    private ProgressDialog progressDialog;
    private ChatRecyclerAdapter chatRecyclerAdapter;

    public static ChatFragment newInstance(String receiver, String receiverUid, String firebaseToken) {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_RECEIVER, receiver);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        // link the event here
    }

    @Override
    public void onStop() {
        super.onStop();
        // unlink the event here
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerViewChat = fragmentView.findViewById(R.id.recycler_view_chat);
        txtMessage = fragmentView.findViewById(R.id.edit_text_message);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setIndeterminate(true);

        txtMessage.setOnEditorActionListener(this);

        getMessageFromFirebaseUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), getArguments().getString(Constants.ARG_RECEIVER_UID));
    }


    @Override
    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        String message = txtMessage.getText().toString();
        String receiver = getArguments().getString(Constants.ARG_RECEIVER);
        String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender, receiver, senderUid, receiverUid, message, System.currentTimeMillis());

        sendMessageToFirebaseUser(getActivity().getApplicationContext(), chat, receiverFirebaseToken);
    }

    private void sendMessageToFirebaseUser(final Context context, final Chat chat, final String receiverToken) {
        final String room1 = chat.getSenderUid() + "_" + chat.getReceiverUid();
        final String room2 = chat.getReceiverUid() + "_" + chat.getSenderUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room1 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room1).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                } else if (dataSnapshot.hasChild(room2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room2 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room2).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room1).child(String.valueOf(chat.getTimestamp())).setValue(chat);
                    getMessageFromFirebaseUser(chat.getSenderUid(), chat.getReceiverUid());
                }

                //send push notification to the receiver

                txtMessage.setText("");
                Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Unable to send message: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room1 = senderUid + "_" + receiverUid;
        final String room2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            if (chatRecyclerAdapter == null) {
                                chatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
                                recyclerViewChat.setAdapter(chatRecyclerAdapter);
                            }
                            chatRecyclerAdapter.add(chat);
                            recyclerViewChat.smoothScrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Unable to get message: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (dataSnapshot.hasChild(room2)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            if (chatRecyclerAdapter == null) {
                                chatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
                                recyclerViewChat.setAdapter(chatRecyclerAdapter);
                            }
                            chatRecyclerAdapter.add(chat);
                            recyclerViewChat.smoothScrollToPosition(chatRecyclerAdapter.getItemCount() - 1);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Unable to get message: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Unable to get message: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

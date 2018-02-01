package com.example.space.chatapp.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.SharedPreferenceHelper;
import com.example.space.chatapp.data.StaticConfig;
import com.example.space.chatapp.models.Conversation;
import com.example.space.chatapp.models.Message;
import com.example.space.chatapp.ui.adapters.ListMessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    public static HashMap<String, Bitmap> bitmapAvatarFriend;
    public Bitmap bitmapAvataruser;

    private RecyclerView recyclerChat;
    private ListMessageAdapter adapter;
    private String roomId;
    private ArrayList<CharSequence> idFriend;
    private Conversation conversation;
    private ImageButton btnSend;
    private EditText editTextMessage;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Intent intentData = getIntent();
        idFriend = intentData.getCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID);
        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        String friendName = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND);

        conversation = new Conversation();
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        // debug this if the return works
        String base64AvatarUser = SharedPreferenceHelper.getInstance(this).getUserInfo().getAvatar();
        if (!base64AvatarUser.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(base64AvatarUser, Base64.DEFAULT);
            bitmapAvataruser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            bitmapAvataruser = null;
        }

        editTextMessage = findViewById(R.id.edit_write_message);

        if (idFriend != null && friendName != null) {
            getSupportActionBar().setTitle(friendName);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = findViewById(R.id.recycler_chat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, conversation, bitmapAvatarFriend, bitmapAvataruser);
            FirebaseDatabase.getInstance().getReference().child("message").child(roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap messageMap = (HashMap) dataSnapshot.getValue();
                        Message message = new Message();
                        message.idSender = (String) messageMap.get("idSender");
                        message.idReceiver = (String) messageMap.get("idReceiver");
                        message.text = (String) messageMap.get("text");
                        message.timestamp = (long) messageMap.get("timestamp");
                        conversation.getMessages().add(message);
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(conversation.getMessages().size() - 1);
                    }
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

                }
            });
            recyclerChat.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent result = new Intent();
            result.putExtra("idFriend", idFriend.get(0));
            setResult(RESULT_OK, result);
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("idFriend", idFriend.get(0));
        setResult(RESULT_OK, result);
        this.finish();
    }


    @Override
    public void onClick(View view) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (view.getId() == R.id.btn_send) {
            String content = editTextMessage.getText().toString().trim();
            if (content.length() > 0) {
                editTextMessage.setText("");
                Message message = new Message();
                message.text = content;
                message.idSender = currentUid;
                message.idReceiver = roomId;
                message.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message").child(roomId).push().setValue(message);
            }
        }
    }
}

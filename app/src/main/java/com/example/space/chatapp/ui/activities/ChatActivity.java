package com.example.space.chatapp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.SharedPreferenceHelper;
import com.example.space.chatapp.data.StaticConfig;
import com.example.space.chatapp.models.Conversation;
import com.example.space.chatapp.models.Message;
import com.example.space.chatapp.ui.adapters.ListMessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    public static final int IMAGE_GALLERY = 0;
    public static final int IMAGE_CAPTURE = 1;
    public static HashMap<String, Bitmap> bitmapAvatarFriend;
    public Bitmap bitmapAvataruser;

    private RecyclerView recyclerChat;
    private ListMessageAdapter adapter;
    private String roomId;
    private ArrayList<CharSequence> idFriend;
    private Conversation conversation;
    private ImageButton btnSend, btnEmoij, btnImage;
    private EmojiconEditText editTextMessage;
    private EmojIconActions emojIconActions;
    private View rootView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference messageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Intent intentData = getIntent();
        idFriend = intentData.getCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID);
        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        String friendName = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND);

        conversation = new Conversation();
        rootView = findViewById(R.id.root_view);
        btnSend = findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        btnEmoij = findViewById(R.id.btn_emoji);
        editTextMessage = findViewById(R.id.edit_write_message);

        emojIconActions = new EmojIconActions(this, rootView, editTextMessage, btnEmoij);
        emojIconActions.ShowEmojIcon();

        btnImage = findViewById(R.id.btn_add_image);
        btnImage.setOnClickListener(this);

        messageReference = FirebaseDatabase.getInstance().getReference().child("message");
        messageReference.keepSynced(true);

        // debug this if the return works
        String base64AvatarUser = SharedPreferenceHelper.getInstance(this).getUserInfo().getAvatar();
        if (!base64AvatarUser.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(base64AvatarUser, Base64.DEFAULT);
            bitmapAvataruser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            bitmapAvataruser = null;
        }

        if (idFriend != null && friendName != null) {
            getSupportActionBar().setTitle(friendName);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = findViewById(R.id.recycler_chat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, conversation, bitmapAvatarFriend, bitmapAvataruser);
            messageReference.child(roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap messageMap = (HashMap) dataSnapshot.getValue();
                        Message message = new Message();
                        message.idSender = (String) messageMap.get("idSender");
                        message.idReceiver = (String) messageMap.get("idReceiver");
                        message.text = (String) messageMap.get("text");
                        message.type = (long) messageMap.get("type");
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
                message.type = Message.TEXT;
                message.idSender = currentUid;
                message.idReceiver = roomId;
                message.timestamp = System.currentTimeMillis();
                messageReference.child(roomId).push().setValue(message);
            }
        } else if (view.getId() == R.id.btn_add_image) {
            final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setTitle("Add Photo!");
            builder.setIcon(R.drawable.photo);
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (options[i].equals("Take Photo")) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, IMAGE_CAPTURE);
                        }
                    } else if (options[i].equals("Choose from Gallery")) {
                        Intent galleryIntent = new Intent();
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, IMAGE_GALLERY);
                    } else if (options.equals("Cancel")) {
                        dialogInterface.dismiss();
                    }
                }
            });
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // add here loading bar for the uploading the image
            Bitmap image = (Bitmap) data.getExtras().get("data");
            UploadPostTask uploadPostTask = new UploadPostTask();
            uploadPostTask.execute(image);
        }

        if (requestCode == IMAGE_GALLERY && resultCode == RESULT_OK) {
            // add here loading bar for the uploading the image
            // decide what should done to delete and not upload the duplicate !!!!!!!!!!!
            Uri ImageUri = data.getData();
            FirebaseStorage.getInstance().getReference().child("Pictures_Messages").child(UUID.randomUUID().toString() + ".jpg").putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();
                        Message message = new Message();
                        message.text = downloadUrl;
                        message.type = Message.IMAGE;
                        message.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        message.idReceiver = roomId;
                        message.timestamp = System.currentTimeMillis();
                        messageReference.child(roomId).push().setValue(message);
                        Toast.makeText(ChatActivity.this, "Picture Sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Error: Picture not sent. Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private class UploadPostTask
            extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            FirebaseStorage.getInstance().getReference().child("Pictures_Messages").child(UUID.randomUUID().toString() + ".jpg").putBytes(
                    byteArrayOutputStream.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();
                        Message message = new Message();
                        message.text = downloadUrl;
                        message.type = Message.IMAGE;
                        message.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        message.idReceiver = roomId;
                        message.timestamp = System.currentTimeMillis();
                        messageReference.child(roomId).push().setValue(message);
                        Toast.makeText(ChatActivity.this, "Picture Sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Error: Picture not sent. Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return null;
        }
    }
}


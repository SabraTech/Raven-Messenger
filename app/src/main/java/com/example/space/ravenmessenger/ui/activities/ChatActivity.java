package com.example.space.ravenmessenger.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.example.space.ravenmessenger.R;
import com.example.space.ravenmessenger.data.SharedPreferenceHelper;
import com.example.space.ravenmessenger.data.StaticConfig;
import com.example.space.ravenmessenger.encryption.CipherHandler;
import com.example.space.ravenmessenger.models.Conversation;
import com.example.space.ravenmessenger.models.Message;
import com.example.space.ravenmessenger.ui.adapters.EmojiAdapter;
import com.example.space.ravenmessenger.ui.adapters.ListMessageAdapter;
import com.google.android.gms.tasks.Continuation;
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
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static com.example.space.ravenmessenger.data.EmojiPrediction.emojiMap;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    public static final int IMAGE_GALLERY = 0;
    public static final int IMAGE_CAPTURE = 1;
    public static final String FILE_PROVIDER_AUTHORITIES = "com.example.space.ravenmessenger.fileprovider";
    public static final String ACTION_EMOJI_CHOSEN = "com.example.space.raven.emoji_chosen";

    public static HashMap<String, Bitmap> bitmapAvatarFriend;
    public Bitmap bitmapAvataruser;

    private RecyclerView recyclerChat, recyclerEmoji;
    private ListMessageAdapter adapter;
    private String roomId;
    private ArrayList<CharSequence> idFriend;
    private Conversation conversation;
    private ImageButton btnSend, btnEmoij, btnImage, btnChooseEmoji;
    private EmojiconEditText editTextMessage;
    private EmojIconActions emojIconActions;
    private View rootView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference messageReference;
    private DatabaseReference usersReference;
    private LovelyProgressDialog uploadDialog;
    private Uri camPhoto;
    private BroadcastReceiver chooseEmoji;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        bitmapAvatarFriend = new HashMap<>();

        uploadDialog = new LovelyProgressDialog(this);
        Intent intentData = getIntent();
        idFriend = intentData.getCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID);
        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        String friendName = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND);
        String friendAvatar = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_AVATAR);
        if (!friendAvatar.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(friendAvatar, Base64.DEFAULT);
            bitmapAvatarFriend.put(idFriend.get(0).toString(), BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        } else {
            bitmapAvatarFriend.put(idFriend.get(0).toString(), BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar));
        }

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

        btnChooseEmoji = findViewById(R.id.btn_predict_emoji);
        btnChooseEmoji.setOnClickListener(this);

        recyclerEmoji = findViewById(R.id.emoji_recycler_view);

        messageReference = FirebaseDatabase.getInstance().getReference().child("message");
        messageReference.keepSynced(true);
        usersReference = FirebaseDatabase.getInstance().getReference().child("user");
        usersReference.keepSynced(true);

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
                        message.idReceiverRoom = (String) messageMap.get("idReceiverRoom");
                        message.text = (String) messageMap.get("text");
                        message.type = (long) messageMap.get("type");
                        message.timestamp = (long) messageMap.get("timestamp");
                        DatabaseReference user1 = usersReference.child((String) messageMap.get("idSender"));
                        DatabaseReference user2 = usersReference.child((String) messageMap.get("idReceiver"));
                        user1.child("message").setValue(message);
                        user2.child("message").setValue(message);
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


        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);


        chooseEmoji = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String emojiCode = intent.getStringExtra("code");
                editTextMessage.append(" " + emojiCode);
                // remove the view of emoji here
                recyclerEmoji.setVisibility(View.GONE);
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_EMOJI_CHOSEN);
        this.registerReceiver(chooseEmoji, intentFilter);


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
    public void onClick(final View view) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (view.getId() == R.id.btn_send) {
            String content = editTextMessage.getText().toString().trim();
            if (content.length() > 0) {
                editTextMessage.setText("");
                Message message = new Message();
                message.text = CipherHandler.encrypt(content);
                message.type = Message.TEXT;
                message.idSender = currentUid;
                message.idReceiver = idFriend.get(0).toString();
                message.idReceiverRoom = roomId;
                message.timestamp = System.currentTimeMillis();
                messageReference.child(roomId).push().setValue(message);
            }
        } else if (view.getId() == R.id.btn_predict_emoji) {
            String content = editTextMessage.getText().toString().trim();
            if (content.length() > 0) {
                content = content.replaceAll(" ", "+");

                // or this
                // RequestQueue queue = Volley.newRequestQueue(this);

                // Start the queue
                requestQueue.start();
                String url = "http://10.10.10.39:8080/emoji?message=" + content;

                // Formulate the request and handle the response.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                String indexOfEmoji = "[2 4  6  20 7]";
                                StringBuilder sb = new StringBuilder(response);
                                sb.deleteCharAt(indexOfEmoji.length() - 1);
                                sb.deleteCharAt(0);
                                String index = sb.toString();
                                String[] parts = index.split("\\s+");
                                List<String> emojis = new ArrayList<>();
                                for (String s : parts) {
                                    emojis.add(emojiMap.get(s));
                                }

                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                recyclerEmoji.setVisibility(View.VISIBLE);
                                recyclerEmoji.setLayoutManager(linearLayoutManager);
                                EmojiAdapter emojiAdapter = new EmojiAdapter(ChatActivity.this, emojis);
                                recyclerEmoji.setAdapter(emojiAdapter);
                                // view each emoji on textView clickable
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error
                                // log the error here with the server
                            }
                        });

                // Add the request to the RequestQueue.
                requestQueue.add(stringRequest);

            } else {
                Toast.makeText(ChatActivity.this, "No message to predict!", Toast.LENGTH_SHORT).show();
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
                        File photo = createTemporaryFile("picture", ".jpg");
                        if (photo != null) {
                            camPhoto = FileProvider.getUriForFile(view.getContext(),
                                    FILE_PROVIDER_AUTHORITIES,
                                    photo);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, camPhoto);
                        }
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
            uploadDialog.setCancelable(false)
                    .setTitle("Image uploading....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            Uri imageUri = camPhoto;
            String imageName = UUID.randomUUID().toString() + ".jpg";
            FirebaseStorage.getInstance().getReference().child("Pictures_Messages").child(imageName).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        uploadDialog.dismiss();
                        task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return FirebaseStorage.getInstance().getReference().child("Pictures_Messages").child(imageName).getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    final String downloadUrl = task.getResult().toString();
                                    Message message = new Message();
                                    message.text = CipherHandler.encrypt(downloadUrl);
                                    message.type = Message.IMAGE;
                                    message.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    message.idReceiver = idFriend.get(0).toString();
                                    message.idReceiverRoom = roomId;
                                    message.timestamp = System.currentTimeMillis();
                                    messageReference.child(roomId).push().setValue(message);
                                    Toast.makeText(ChatActivity.this, "Picture Sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle failures
                                    uploadDialog.dismiss();
                                    Toast.makeText(ChatActivity.this, "Error: Picture not sent. Try Again!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        uploadDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Error: Picture not sent. Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            UploadPostTask uploadPostTask = new UploadPostTask();
//            uploadPostTask.execute(image);
        }

        if (requestCode == IMAGE_GALLERY && resultCode == RESULT_OK) {
            // add here loading bar for the uploading the image
            // decide what should done to delete and not upload the duplicate !!!!!!!!!!!
            uploadDialog.setCancelable(false)
                    .setTitle("Image uploading....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            Uri ImageUri = data.getData();
            String imageName = UUID.randomUUID().toString() + ".jpg";
            FirebaseStorage.getInstance().getReference().child("Pictures_Messages").child(imageName).putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        uploadDialog.dismiss();
                        task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return FirebaseStorage.getInstance().getReference().child("Pictures_Messages").child(imageName).getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    final String downloadUrl = task.getResult().toString();
                                    Message message = new Message();
                                    message.text = CipherHandler.encrypt(downloadUrl);
                                    message.type = Message.IMAGE;
                                    message.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    message.idReceiver = idFriend.get(0).toString();
                                    message.idReceiverRoom = roomId;
                                    message.timestamp = System.currentTimeMillis();
                                    messageReference.child(roomId).push().setValue(message);
                                    Toast.makeText(ChatActivity.this, "Picture Sent!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Handle failures
                                    uploadDialog.dismiss();
                                    Toast.makeText(ChatActivity.this, "Error: Picture not sent. Try Again!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        uploadDialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Error: Picture not sent. Try Again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // log print in file method
    private void appendText(String text) {
        File logFile = new File("/storage/emulated/0/log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createTemporaryFile(String part, String format) {
        File tempDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        tempDir = new File(tempDir.getAbsolutePath() + "/.ravenTemp/");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File returnFile = null;
        try {
            returnFile = File.createTempFile(part, format, tempDir);
            returnFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnFile;
    }


    private class UploadPostTask
            extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected Void doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            Log.e(ChatActivity.class.getName(), "************ \n image dimen:: " + bitmap.getWidth() + " " + bitmap.getHeight());
            FirebaseStorage.getInstance().getReference().child("Pictures_Messages").child(UUID.randomUUID().toString() + ".jpg").putBytes(
                    byteArrayOutputStream.toByteArray()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        final String downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                        Message message = new Message();
                        message.text = CipherHandler.encrypt(downloadUrl);
                        message.type = Message.IMAGE;
                        message.idSender = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        message.idReceiver = idFriend.get(0).toString();
                        message.idReceiverRoom = roomId;
                        message.timestamp = System.currentTimeMillis();
                        messageReference.child(roomId).push().setValue(message);
                        adapter.notifyDataSetChanged();
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


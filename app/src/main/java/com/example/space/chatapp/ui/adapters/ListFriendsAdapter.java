package com.example.space.chatapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.StaticConfig;
import com.example.space.chatapp.encryption.CipherHandler;
import com.example.space.chatapp.models.FriendList;
import com.example.space.chatapp.models.Message;
import com.example.space.chatapp.ui.activities.ChatActivity;
import com.example.space.chatapp.ui.fragments.FriendsFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ListFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static Map<String, Query> queryMap;
    public static Map<String, DatabaseReference> databaseReferenceMap;
    public static Map<String, ChildEventListener> childEventListenerMap;
    public static Map<String, ChildEventListener> childEventListenerOnlineMap;
    public static Map<String, Boolean> markMap;
    LovelyProgressDialog progressDialog;
    private FriendList friendList;
    private Context context;
    private FriendsFragment friendsFragment;

    public ListFriendsAdapter(Context context, FriendList friendList, FriendsFragment friendsFragment) {
        this.context = context;
        this.friendList = friendList;
        this.friendsFragment = friendsFragment;

        queryMap = new HashMap<>();
        databaseReferenceMap = new HashMap<>();
        childEventListenerMap = new HashMap<>();
        childEventListenerOnlineMap = new HashMap<>();
        markMap = new HashMap<>();

        progressDialog = new LovelyProgressDialog(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_friend, parent, false);
        return new ItemFriendViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final String name = friendList.getFriendsList().get(position).getName();
        final String id = friendList.getFriendsList().get(position).id;
        final String idRoom = friendList.getFriendsList().get(position).idRoom;
        final String avatar = friendList.getFriendsList().get(position).getAvatar();
        final Message message = friendList.getFriendsList().get(position).getMessage();
        final String messageText = CipherHandler.decrypt(friendList.getFriendsList().get(position).getMessage().text);

        ((ItemFriendViewHolder) holder).txtName.setText(name);
        ((View) ((ItemFriendViewHolder) holder).txtName.getParent().getParent().getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT);
                ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, name);
                ArrayList<CharSequence> idFriend = new ArrayList<>();
                idFriend.add(id);
                intent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriend);
                intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, idRoom);
                intent.putExtra(StaticConfig.INTENT_KEY_CHAT_AVATAR, avatar);

                markMap.put(id, null);
                friendsFragment.startActivityForResult(intent, FriendsFragment.ACTION_START_CHAT);
            }
        });


        // here the delete friend done by the long click
        // we need to talk about how will do it


        if (messageText.length() > 0) {
            ((ItemFriendViewHolder) holder).txtMessage.setVisibility(View.VISIBLE);
            ((ItemFriendViewHolder) holder).txtTime.setVisibility(View.VISIBLE);

            if (!messageText.startsWith(id)) {
                if (message.type == Message.IMAGE) {
                    ((ItemFriendViewHolder) holder).txtMessage.setText("Image");
                } else {
                    ((ItemFriendViewHolder) holder).txtMessage.setText(messageText);
                }
                ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT);
                ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT);
            } else {
                if (message.type == Message.IMAGE) {
                    ((ItemFriendViewHolder) holder).txtMessage.setText("Image");
                } else {
                    ((ItemFriendViewHolder) holder).txtMessage.setText(messageText.substring((id + "").length()));
                }
                ((ItemFriendViewHolder) holder).txtMessage.setTypeface(Typeface.DEFAULT_BOLD);
                ((ItemFriendViewHolder) holder).txtName.setTypeface(Typeface.DEFAULT_BOLD);
            }

            String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(friendList.getFriendsList().get(position).getMessage().timestamp));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));

            if (today.equals(time)) {
                ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("HH:mm").format(new Date(friendList.getFriendsList().get(position).getMessage().timestamp)));
            } else {
                ((ItemFriendViewHolder) holder).txtTime.setText(new SimpleDateFormat("MMM d").format(new Date(friendList.getFriendsList().get(position).getMessage().timestamp)));
            }
        } else {
            ((ItemFriendViewHolder) holder).txtMessage.setVisibility(View.GONE);
            ((ItemFriendViewHolder) holder).txtTime.setVisibility(View.GONE);

            if (queryMap.get(id) == null && childEventListenerMap.get(id) == null) {
                queryMap.put(id, FirebaseDatabase.getInstance().getReference().child("message").child(idRoom).limitToLast(1));
                childEventListenerMap.put(id, new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        HashMap messageMap = (HashMap) dataSnapshot.getValue();
                        if (markMap.get(id) != null) {
                            if (!markMap.get(id)) {
                                friendList.getFriendsList().get(position).getMessage().text = id + messageMap.get("text");
                            } else {
                                friendList.getFriendsList().get(position).getMessage().text = (String) messageMap.get("text");
                            }
                            notifyDataSetChanged();
                            markMap.put(id, false);
                        } else {
                            friendList.getFriendsList().get(position).getMessage().text = (String) messageMap.get("text");
                            notifyDataSetChanged();
                        }
                        friendList.getFriendsList().get(position).getMessage().timestamp = (long) messageMap.get("timestamp");
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

                queryMap.get(id).addChildEventListener(childEventListenerMap.get(id));
                markMap.put(id, true);
            } else {
                queryMap.get(id).removeEventListener(childEventListenerMap.get(id));
                queryMap.get(id).addChildEventListener(childEventListenerMap.get(id));
                markMap.put(id, true);
            }
        }

        if (friendList.getFriendsList().get(position).getAvatar().equals(StaticConfig.STR_DEFAULT_BASE64)) {
            ((ItemFriendViewHolder) holder).avatar.setImageResource(R.drawable.default_avatar);
        } else {
            byte[] decodedString = Base64.decode(friendList.getFriendsList().get(position).getAvatar(), Base64.DEFAULT);
            Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ((ItemFriendViewHolder) holder).avatar.setImageBitmap(src);
        }

        if (databaseReferenceMap.get(id) == null && childEventListenerOnlineMap.get(id) == null) {
            databaseReferenceMap.put(id, FirebaseDatabase.getInstance().getReference().child("user").child(id).child("status"));
            childEventListenerOnlineMap.put(id, new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("isOnline")) {
                        Log.d("FriendsFragment add" + id, (boolean) dataSnapshot.getValue() + "");
                        friendList.getFriendsList().get(position).getStatus().isOnline = (boolean) dataSnapshot.getValue();
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("isOnline")) {
                        Log.d("FriendsFragment change" + id, (boolean) dataSnapshot.getValue() + "");
                        friendList.getFriendsList().get(position).getStatus().isOnline = (boolean) dataSnapshot.getValue();
                        notifyDataSetChanged();
                    }

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
            databaseReferenceMap.get(id).addChildEventListener(childEventListenerOnlineMap.get(id));
        }

        if (friendList.getFriendsList().get(position).getStatus().isOnline) {
            ((ItemFriendViewHolder) holder).avatar.setBorderColor(context.getResources().getColor(R.color.green_500));
            ((ItemFriendViewHolder) holder).avatar.setBorderWidth(10);
        } else {
            ((ItemFriendViewHolder) holder).avatar.setBorderWidth(0);
        }

    }

    @Override
    public int getItemCount() {
        return friendList.getFriendsList() != null ? friendList.getFriendsList().size() : 0;
    }
}

class ItemFriendViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView avatar;
    public TextView txtName, txtTime, txtMessage;
    private Context context;

    ItemFriendViewHolder(Context context, View itemView) {
        super(itemView);
        avatar = itemView.findViewById(R.id.icon_avatar);
        txtName = itemView.findViewById(R.id.txtName);
        txtTime = itemView.findViewById(R.id.txtTime);
        txtMessage = itemView.findViewById(R.id.txtMessage);
        this.context = context;
    }
}

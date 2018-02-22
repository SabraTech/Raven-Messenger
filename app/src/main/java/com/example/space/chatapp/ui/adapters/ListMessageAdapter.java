package com.example.space.chatapp.ui.adapters;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.StaticConfig;
import com.example.space.chatapp.models.Conversation;
import com.example.space.chatapp.models.Message;
import com.example.space.chatapp.ui.activities.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Conversation conversation;
    private HashMap<String, Bitmap> bitmapHashMapAvatar;
    private HashMap<String, DatabaseReference> referenceHashMapAvatar;
    private Bitmap bitmapAvatarUser;
    private DatabaseReference usersReference;

    public ListMessageAdapter(Context context, Conversation conversation, HashMap<String, Bitmap> bitmapHashMapAvatar, Bitmap bitmapAvatarUser) {
        this.context = context;
        this.conversation = conversation;
        this.bitmapHashMapAvatar = bitmapHashMapAvatar;
        this.bitmapAvatarUser = bitmapAvatarUser;
        referenceHashMapAvatar = new HashMap<>();
        usersReference = FirebaseDatabase.getInstance().getReference().child("user");
        usersReference.keepSynced(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChatActivity.VIEW_TYPE_FRIEND_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
            return new ItemMessageFriendHolder(view);
        } else if (viewType == ChatActivity.VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
            return new ItemMessageUserHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemMessageFriendHolder) {
            if (conversation.getMessages().get(position).type == Message.TEXT) {
                ((ItemMessageFriendHolder) holder).imageContent.setVisibility(View.INVISIBLE);
                ((ItemMessageFriendHolder) holder).txtContent.setText(conversation.getMessages().get(position).text);
                ((ItemMessageFriendHolder) holder).txtContent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("message", conversation.getMessages().get(position).text);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Message Copied!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                Bitmap currentAvatar = bitmapHashMapAvatar.get(conversation.getMessages().get(position).idSender);
                if (currentAvatar != null) {
                    ((ItemMessageFriendHolder) holder).avatar.setImageBitmap(currentAvatar);
                } else {
                    final String id = conversation.getMessages().get(position).idSender;
                    if (referenceHashMapAvatar.get(id) == null) {
                        referenceHashMapAvatar.put(id, usersReference.child(id).child("avatar"));
                        referenceHashMapAvatar.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String avatarString = (String) dataSnapshot.getValue();
                                    if (!avatarString.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                                        byte[] decodedString = Base64.decode(avatarString, Base64.DEFAULT);
                                        ChatActivity.bitmapAvatarFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                    } else {
                                        ChatActivity.bitmapAvatarFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar));
                                    }
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            } else if (conversation.getMessages().get(position).type == Message.IMAGE) {
                ((ItemMessageFriendHolder) holder).txtContent.setVisibility(View.INVISIBLE);
                ((ItemMessageFriendHolder) holder).txtContent.setPadding(0, 0, 0, 0);

                Picasso.with(context).load(conversation.getMessages().get(position).text).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(((ItemMessageFriendHolder) holder).imageContent, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(conversation.getMessages().get(position).text).placeholder(R.drawable.default_image).into(((ItemMessageFriendHolder) holder).imageContent);
                    }
                });

                Bitmap currentAvatar = bitmapHashMapAvatar.get(conversation.getMessages().get(position).idSender);
                if (currentAvatar != null) {
                    ((ItemMessageFriendHolder) holder).avatar.setImageBitmap(currentAvatar);
                } else {
                    final String id = conversation.getMessages().get(position).idSender;
                    if (referenceHashMapAvatar.get(id) == null) {
                        referenceHashMapAvatar.put(id, usersReference.child(id).child("avatar"));
                        referenceHashMapAvatar.get(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    String avatarString = (String) dataSnapshot.getValue();
                                    if (!avatarString.equals(StaticConfig.STR_DEFAULT_BASE64)) {
                                        byte[] decodedString = Base64.decode(avatarString, Base64.DEFAULT);
                                        ChatActivity.bitmapAvatarFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                                    } else {
                                        ChatActivity.bitmapAvatarFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar));
                                    }
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

        } else if (holder instanceof ItemMessageUserHolder) {
            if (conversation.getMessages().get(position).type == Message.TEXT) {
                ((ItemMessageUserHolder) holder).imageContent.setVisibility(View.INVISIBLE);
                ((ItemMessageUserHolder) holder).txtContent.setText(conversation.getMessages().get(position).text);
                ((ItemMessageUserHolder) holder).txtContent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("message", conversation.getMessages().get(position).text);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Message Copied!", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                if (bitmapAvatarUser != null) {
                    ((ItemMessageUserHolder) holder).avatar.setImageBitmap(bitmapAvatarUser);
                }
            } else if (conversation.getMessages().get(position).type == Message.IMAGE) {
                ((ItemMessageUserHolder) holder).txtContent.setVisibility(View.INVISIBLE);
                ((ItemMessageUserHolder) holder).txtContent.setPadding(0, 0, 0, 0);

                Picasso.with(context).load(conversation.getMessages().get(position).text).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_image).into(((ItemMessageUserHolder) holder).imageContent, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context).load(conversation.getMessages().get(position).text).placeholder(R.drawable.default_image).into(((ItemMessageUserHolder) holder).imageContent);
                    }
                });

                if (bitmapAvatarUser != null) {
                    ((ItemMessageUserHolder) holder).avatar.setImageBitmap(bitmapAvatarUser);
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return conversation.getMessages().size();
    }

    @Override
    public int getItemViewType(int position) {
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (conversation.getMessages().get(position).idSender.equals(currentUid)) {
            return ChatActivity.VIEW_TYPE_USER_MESSAGE;
        } else {
            return ChatActivity.VIEW_TYPE_FRIEND_MESSAGE;
        }
    }
}

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avatar;
    public ImageView imageContent;

    public ItemMessageUserHolder(View view) {
        super(view);
        txtContent = view.findViewById(R.id.textContentUser);
        imageContent = view.findViewById(R.id.imageContentUser);
        avatar = view.findViewById(R.id.image_view_user);
    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avatar;
    public ImageView imageContent;

    public ItemMessageFriendHolder(View view) {
        super(view);
        txtContent = view.findViewById(R.id.textContentFriend);
        imageContent = view.findViewById(R.id.imageContentFriend);
        avatar = view.findViewById(R.id.image_view_friend);
    }
}

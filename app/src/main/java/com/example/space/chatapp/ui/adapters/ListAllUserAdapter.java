package com.example.space.chatapp.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.StaticConfig;
import com.example.space.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sabra on 20/02/18.
 */

public class ListAllUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> users;
    private List<String> firendsId;
    private Context context;
    private DatabaseReference friendRequestReference;
    private DatabaseReference notificationsReference;
    private String senderUid;

    public ListAllUserAdapter(Context context, List<User> users, List<String> firendsId, String currentUid) {
        this.context = context;
        this.users = users;
        this.firendsId = firendsId;
        this.senderUid = currentUid;

        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("friend_request");
        friendRequestReference.keepSynced(true);
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        notificationsReference.keepSynced(true);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //set to new item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_all_user, parent, false);
        return new ItemAllUserViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final User user = users.get(position);
        final String name = user.getName();
        final String status = user.getBioText();
        final String avatar = user.getAvatar();

        // set the image
        if (avatar.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            ((ItemAllUserViewHolder) holder).imageViewProfile.setImageResource(R.drawable.default_avatar);
        } else {
            byte[] decodedString = Base64.decode(avatar, Base64.DEFAULT);
            Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ((ItemAllUserViewHolder) holder).imageViewProfile.setImageBitmap(src);
        }

        // set the name
        ((ItemAllUserViewHolder) holder).txtUsername.setText(name);

        // set the status
        ((ItemAllUserViewHolder) holder).txtStatus.setText(status);

        // set the button text and action

        if (firendsId.contains(user.getUid())) {
            ((ItemAllUserViewHolder) holder).actionBtn.setText("Un Friend");
            ((ItemAllUserViewHolder) holder).actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // un friend method and then change the text of button
                    ((ItemAllUserViewHolder) holder).actionBtn.setText("Add Friend");
                }
            });
        } else {
            ((ItemAllUserViewHolder) holder).actionBtn.setText("Add Friend");
            ((ItemAllUserViewHolder) holder).actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // add friend method and then change the text of button
                    sendFriendRequestMethod(user.getUid());
                    ((ItemAllUserViewHolder) holder).actionBtn.setText("Cancel Request");
                    notifyDataSetChanged();
                }
            });
        }
        // the remaining is the cancel request action
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }

    private void sendFriendRequestMethod(final String receiverUid) {
        //1-send request and create first node
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
                                                HashMap<String, String> notificationsData = new HashMap<>();
                                                notificationsData.put("from", senderUid);
                                                notificationsData.put("type ", "request");
                                                notificationsReference.child(receiverUid)
                                                        .push() //will be given unique random key
                                                        .setValue(notificationsData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //update the UI buttons
                                                                if (task.isSuccessful()) {

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
}

class ItemAllUserViewHolder extends RecyclerView.ViewHolder {
    TextView txtStatus, txtUsername;
    CircleImageView imageViewProfile;
    Button actionBtn;
    private Context context;

    ItemAllUserViewHolder(Context context, View itemView) {
        super(itemView);
        txtStatus = itemView.findViewById(R.id.all_users_status);
        txtUsername = itemView.findViewById(R.id.all_users_username);
        imageViewProfile = itemView.findViewById(R.id.all_users_profile_image);
        actionBtn = itemView.findViewById(R.id.all_user_btn);
        this.context = context;
    }
}

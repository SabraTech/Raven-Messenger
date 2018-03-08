package com.example.space.raven.ui.adapters;


import android.content.Context;
import android.content.Intent;
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

import com.example.space.raven.R;
import com.example.space.raven.data.StaticConfig;
import com.example.space.raven.models.Notifications;
import com.example.space.raven.ui.fragments.NotificationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LovelyProgressDialog progressDialog;
    private Notifications notifications;
    private Context context;
    private NotificationFragment notificationFragment;
    private DatabaseReference requestReference;
    private DatabaseReference friendsReference;
    private DatabaseReference notificationsReference;

    public RequestAdapter(Context context, Notifications notifications, NotificationFragment notificationFragment) {
        this.context = context;
        this.notifications = notifications;
        this.notificationFragment = notificationFragment;

        // maps here

        progressDialog = new LovelyProgressDialog(context);
        friendsReference = FirebaseDatabase.getInstance().getReference().child("friends");
        friendsReference.keepSynced(true);
        requestReference = FirebaseDatabase.getInstance().getReference().child("friend_request");
        requestReference.keepSynced(true);
        notificationsReference = FirebaseDatabase.getInstance().getReference().child("notifications");
        notificationsReference.keepSynced(true);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_request, parent, false);
        return new ItemRequestViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // when click go to profile not implemented yet
        final String name = notifications.getRequests().get(position).getName();
        final String avatar = notifications.getRequests().get(position).getAvatar();
        final String status = notifications.getRequests().get(position).getStatus();

        ((ItemRequestViewHolder) holder).txtName.setText(name);

        if (avatar.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            ((ItemRequestViewHolder) holder).avatar.setImageResource(R.drawable.default_avatar);
        } else {
            byte[] decodedString = Base64.decode(avatar, Base64.DEFAULT);
            Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ((ItemRequestViewHolder) holder).avatar.setImageBitmap(src);
        }

        ((ItemRequestViewHolder) holder).txtStatus.setText(status);

        ((ItemRequestViewHolder) holder).accept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String receiverUid = notifications.getRequests().get(holder.getAdapterPosition()).getUid();
                Calendar friendsDate = Calendar.getInstance();
                final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
                final String saveCurrentDate = currentDate.format(friendsDate.getTime());
                friendsReference.child(currentUid).child(receiverUid)
                        .setValue(saveCurrentDate)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friendsReference.child(receiverUid).child(currentUid)
                                        .setValue(saveCurrentDate)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                requestReference
                                                        .child(currentUid).child(receiverUid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    requestReference
                                                                            .child(receiverUid).child(currentUid)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    // now they are friends
                                                                                    // so we should add them friends to each other so they can
                                                                                    // appear in the friends fragment
                                                                                    // and remove the request from the list.
                                                                                    //remove notification node
                                                                                    notificationsReference
                                                                                            .child(receiverUid)
                                                                                            .child(currentUid)
                                                                                            .removeValue();
                                                                                    notificationsReference
                                                                                            .child(currentUid)
                                                                                            .child(receiverUid)
                                                                                            .removeValue();

                                                                                    Intent intentRemoved = new Intent(NotificationFragment.ACTION_REQUEST_REMOVED);
                                                                                    intentRemoved.putExtra("requestId", receiverUid);
                                                                                    context.sendBroadcast(intentRemoved);
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
        });


        ((ItemRequestViewHolder) holder).cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final String receiverUid = notifications.getRequests().get(holder.getAdapterPosition()).getUid();
                requestReference
                        .child(currentUid).child(receiverUid)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    requestReference
                                            .child(receiverUid).child(currentUid)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    // remove the request from the fragment and the list
                                                    // how to refresh the adapter

                                                    //remove notification node
                                                    notificationsReference
                                                            .child(receiverUid)
                                                            .child(currentUid)
                                                            .removeValue();
                                                    notificationsReference
                                                            .child(currentUid)
                                                            .child(receiverUid)
                                                            .removeValue();

                                                    Intent intentRemoved = new Intent(NotificationFragment.ACTION_REQUEST_REMOVED);
                                                    intentRemoved.putExtra("requestId", receiverUid);
                                                    context.sendBroadcast(intentRemoved);
                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.getRequests() != null ? notifications.getRequests().size() : 0;
    }
}

class ItemRequestViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView avatar;
    public TextView txtName, txtStatus;
    public Button accept, cancel;
    private Context context;

    ItemRequestViewHolder(Context context, View itemView) {
        super(itemView);
        avatar = itemView.findViewById(R.id.request_profile_image);
        txtName = itemView.findViewById(R.id.request_profile_user_name);
        txtStatus = itemView.findViewById(R.id.request_profile_status);
        accept = itemView.findViewById(R.id.request_accept_btn);
        cancel = itemView.findViewById(R.id.request_cancel_btn);
        this.context = context;
    }
}

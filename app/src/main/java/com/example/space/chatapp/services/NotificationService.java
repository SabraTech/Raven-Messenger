package com.example.space.chatapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.space.chatapp.R;
import com.example.space.chatapp.data.StaticConfig;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

/*
 * service to get fore ground notification
 * it's called automatic when notification added in database
 * and must be added in manifest
 */

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        //get from index.js
        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();
        String clickAction = remoteMessage.getNotification().getClickAction();

        //define sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //define builder
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationBody)
                        .setSound(notificationSound)
                        .setOngoing(true);//to open app when it is closed but not working

        Intent resultIntent = new Intent(clickAction);

        // get the notification type
        if (notificationTitle.equals("New Message")) {
            String fromSenderId = remoteMessage.getData().get("from_sender_id").toString();
            String avatar = remoteMessage.getData().get("avatar").toString();
            String name = remoteMessage.getData().get("name").toString();
            String idRoom = remoteMessage.getData().get("id_room").toString();

            resultIntent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, name);
            ArrayList<CharSequence> idFriend = new ArrayList<>();
            idFriend.add(fromSenderId);
            resultIntent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriend);
            resultIntent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, idRoom);
            resultIntent.putExtra(StaticConfig.INTENT_KEY_CHAT_AVATAR, avatar);
        } else {
            String fromSenderId = remoteMessage.getData().get("from_sender_id").toString();
            resultIntent.putExtra("visit", fromSenderId);
        }

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        //to close notification when click on it
        mBuilder.setAutoCancel(true);


        // Sets an unique ID for the notification
        int mNotificationId = (int) System.currentTimeMillis();
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }
}
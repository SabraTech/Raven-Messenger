package com.example.space.chatapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.space.chatapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/*
 * service to get fore ground notification
 * it's called automatic when notification added in database
 * and must be added in manifest
 */

public class FBMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        //get from index.js
        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();
        String fromSenderId = remoteMessage.getData().get("from_sender_id").toString();
        String clickAction = remoteMessage.getNotification().getClickAction();

        //define sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //define builder
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationBody)
                        .setSound(notificationSound);

        //go to visited profile on click
        //it knows to go to visited profile activity from manifest
        Intent resultIntent = new Intent(clickAction);
        resultIntent.putExtra("visit", fromSenderId);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


        // Sets an unique ID for the notification
        int mNotificationId = (int) System.currentTimeMillis();
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }
}
package com.example.space.ravenmessenger.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;

import com.example.space.ravenmessenger.R;
import com.example.space.ravenmessenger.data.StaticConfig;
import com.example.space.ravenmessenger.ui.activities.ChatActivity;
import com.example.space.ravenmessenger.ui.activities.TabsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;


/*
 * service to get fore ground notification
 * it's called automatic when notification added in database
 * and must be added in manifest
 */

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        String notificationTitle = data.get("title");
        String notificationBody = data.get("body");
        String notificationAvatar = data.get("avatar");
        byte[] decodedString = Base64.decode(notificationAvatar, Base64.DEFAULT);
        Bitmap notificationImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        Intent resultIntent;
        if (notificationTitle.equals("New Message")) {
            resultIntent = new Intent(this, ChatActivity.class);
            String fromSenderId = data.get("from_user_id");
            String avatar = data.get("avatar");
            String name = data.get("name");
            String idRoom = data.get("id_room");
            resultIntent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, name);
            ArrayList<CharSequence> idFriend = new ArrayList<>();
            idFriend.add(fromSenderId);
            resultIntent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriend);
            resultIntent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, idRoom);
            resultIntent.putExtra(StaticConfig.INTENT_KEY_CHAT_AVATAR, avatar);
        } else if (notificationTitle.equals("New Friend request")) {
            resultIntent = new Intent(this, TabsActivity.class);
            resultIntent.putExtra("selected_index", "1");
        } else {
//            notificationTitle.equals("Accept Friend request")
            resultIntent = new Intent(this, TabsActivity.class);
            resultIntent.putExtra("selected_index", "0");
        }

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Sets an unique ID for the notification
        String channelId = "fcm_default_channel";
        int notificationId = (int) System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, notificationTitle,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // define sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //define builder
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId);
        mBuilder.setSmallIcon(R.drawable.ic_notification);

        mBuilder.setLargeIcon(notificationImage);
        mBuilder.setContentTitle(notificationTitle);
        mBuilder.setContentText(notificationBody);
        // mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mBuilder.setSound(notificationSound);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setBadgeIconType(R.drawable.ic_notification);
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mBuilder.setLights(Color.BLUE, 3000, 3000);
        Notification notification = mBuilder.build();

        // Builds the notification and issues it.
        notificationManager.notify(notificationId, notification);

    }
}
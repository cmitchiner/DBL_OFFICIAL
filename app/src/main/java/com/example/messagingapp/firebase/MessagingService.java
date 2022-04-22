package com.example.messagingapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.messagingapp.R;
import com.example.messagingapp.activities.MessagesActivity;
import com.example.messagingapp.model.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    /**
     * Listener to see if a user has gotten a new token
     *
     * @param token the new token to be set
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token: " + token);
    }

    /**
     * A listener to see if a notifcation was receieved on the firebase side, if so, builds an android
     * notification and sends it to the user
     *
     * @param message the remote message from the firebase cloud messaging service
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        //Create a user class with the sending users token
        User user = new User();
        //set all needed values for user class
        user.fullName = message.getData().get("name");
        user.token = message.getData().get("token");
        user.UID = message.getData().get("userId");

        //Setup the notification channel
        int notificationId = new Random().nextInt();
        String channelId = "chat_message";

        //Setup intent for when the notification is clicked on
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //Build the notification itself
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        //Put a nice notification icon
        builder.setSmallIcon(R.drawable.ic_baseline_circle_notifications_24);
        //Set the color of the icon
        builder.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        //Set the title of the notification to the senders full name
        builder.setContentTitle(user.fullName);
        //Put the message text into the notification as well with proper sizing
        builder.setContentText(message.getData().get("message"));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message.getData().get("message")));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        //Send the notification properly based on the users android version
        //FOR OREO or HIGHER NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Chat Message";
            String channelDescription = "This notification channel is used for chat messages";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        //Send the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
    }
}

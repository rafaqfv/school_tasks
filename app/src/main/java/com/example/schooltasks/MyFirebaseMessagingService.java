package com.example.schooltasks;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        getFirebaseMessage(message.getNotification().getTitle(), message.getNotification().getBody());

    }

    @SuppressLint("MissingPermission")
    private void getFirebaseMessage(String title, String body) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify")
                .setSmallIcon(R.drawable.add_admin)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(102,builder.build());

    }
}

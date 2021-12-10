package com.example.quiz.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.example.quiz.R;
import com.example.quiz.views.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "Alarm";
    @Override
    public void onReceive(Context context, Intent intent) {
        String testName = intent.getStringExtra("test name");

        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Alarm notification channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );


        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(serviceChannel);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d("test name", testName);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.quiz)
                .setContentIntent(pendingIntent)
                .setContentText(testName + " has started!")
                .setAutoCancel(true)
                .build();

        notificationManager.notify(0, notification);
    }
}

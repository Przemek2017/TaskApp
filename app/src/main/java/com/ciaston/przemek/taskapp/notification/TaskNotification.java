package com.ciaston.przemek.taskapp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.ciaston.przemek.taskapp.MainActivity;
import com.ciaston.przemek.taskapp.R;

import java.util.Date;

/**
 * Created by Przemek on 2018-02-01.
 */

public class TaskNotification {

    public static void createNotification(Context context, String title, CharSequence description) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.alarm64)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();
        long pause = 120;
        long shortVib = 150;
        long longVib = 400;
        notification.vibrate =
                new long[]{pause, shortVib, pause, longVib, pause, shortVib, pause, longVib, pause, shortVib, pause, longVib};
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int unique = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE) * title.length();
        notificationManager.notify(unique, notification);

        try {
            Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, defaultUri);
            ringtone.play();
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
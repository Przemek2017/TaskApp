package com.ciaston.przemek.taskapp.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.ciaston.przemek.taskapp.R;

/**
 * Created by Przemek on 2018-02-01.
 */

public class TaskNotification {

    //DataBaseManager dataBaseManager;

    public static void createNotification(Context context, int title, CharSequence description) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.juvepoland.com"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(""+title);
        builder.setContentText(description);
        builder.setSmallIcon(R.drawable.alarm64);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        //create the notification
        notification.vibrate = new long[]{150, 250, 150, 250, 150, 250};
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(R.drawable.alarm64, notification);

        //create a vibration
        try {

            Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, defaultUri);
            ringtone.play();
        } catch (Exception e) {
            e.getMessage();
        }
    }

//    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Calendar calendar = Calendar.getInstance();
//
//            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            //
//            String setTime1 = timeFormat.format(calendar.getTime());
//            String setDate1 = dateFormat.format(calendar.getTime());
//
//            List<TaskModel> myTaskList = dataBaseManager.getData();
//            for (TaskModel t : myTaskList) {
//                String notiTime = t.getTime().toString();
//                String notiDate = t.getDate().toString();
//                if (notiTime.equals(setTime1) && notiDate.equals(setDate1)) {
//                    TaskNotification.createNotification(context, "Its TIME!", t.getTask());
//                }
//            }
//        }
//    };
}
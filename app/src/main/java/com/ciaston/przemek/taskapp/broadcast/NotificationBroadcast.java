package com.ciaston.przemek.taskapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ciaston.przemek.taskapp.MainActivity;
import com.ciaston.przemek.taskapp.R;
import com.ciaston.przemek.taskapp.db.DataBaseManager;
import com.ciaston.przemek.taskapp.model.TaskModel;
import com.ciaston.przemek.taskapp.notification.TaskNotification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Przemek on 2018-02-17.
 */

public class NotificationBroadcast extends BroadcastReceiver {

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        DataBaseManager dataBaseManager = new DataBaseManager(context);

        String title = MainActivity.resources.getString(R.string.its_high_time_to);

        String getTimeFromCalendar = timeFormat.format(calendar.getTime());
        String getDateFromCalendar = dateFormat.format(calendar.getTime());

        List<TaskModel> taskList = dataBaseManager.getTask();
        for (TaskModel taskModel : taskList) {
            String notificationTime = taskModel.getTime().toString();
            String notificationDate = taskModel.getDate().toString();
            if (notificationTime.equals(getTimeFromCalendar) && notificationDate.equals(getDateFromCalendar)) {
                TaskNotification.createNotification(context, title, taskModel.getTask());
            } else if (notificationTime.equals(getTimeFromCalendar) && notificationDate.isEmpty()) {
                TaskNotification.createNotification(context, title, taskModel.getTask());
            }
        }
    }
}

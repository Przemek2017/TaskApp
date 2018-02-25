package com.ciaston.przemek.taskapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ciaston.przemek.taskapp.broadcast.TaskBroadcast;

/**
 * Created by Przemek on 2018-02-25.
 */

public class TaskService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(new TaskBroadcast(), intentFilter);

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

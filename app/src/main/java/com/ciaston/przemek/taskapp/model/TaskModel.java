package com.ciaston.przemek.taskapp.model;

/**
 * Created by Przemek on 2018-01-31.
 */

public class TaskModel {

    String task;
    String time;
    String date;

    public TaskModel(String task, String time, String date) {
        this.task = task;
        this.time = time;
        this.date = date;
    }
    public TaskModel(){}

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

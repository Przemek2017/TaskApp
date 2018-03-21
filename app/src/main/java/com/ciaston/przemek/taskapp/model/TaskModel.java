package com.ciaston.przemek.taskapp.model;

/**
 * Created by Przemek on 2018-01-31.
 */

public class TaskModel {

    private String id;
    private String task;
    private String time;
    private String date;
    private boolean isComplete;

    public TaskModel(String task, String time, String date, boolean isComplete) {
        this.task = task;
        this.time = time;
        this.date = date;
        this.isComplete = isComplete;
    }

    public TaskModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}

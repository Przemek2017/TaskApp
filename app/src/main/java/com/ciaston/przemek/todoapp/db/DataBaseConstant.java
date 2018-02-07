package com.ciaston.przemek.todoapp.db;

/**
 * Created by Przemek on 2018-02-01.
 */

public class DataBaseConstant {

    public static final String DATABASE = "task.db";
    public static final String TABLE = "todo_table";
    public static final String TASK = "task";
    public static final String TIME = "time";
    public static final String DATE = "date";

    public static final int VERSION = 1;

    public static final String createTable = "CREATE TABLE " + TABLE + " ("
            + TASK + " TEXT, "
            + TIME + " TEXT, "
            + DATE + " TEXT);";

    public static final String dropTable = "DROP TABLE IF EXISTS " + TABLE + ";";
}

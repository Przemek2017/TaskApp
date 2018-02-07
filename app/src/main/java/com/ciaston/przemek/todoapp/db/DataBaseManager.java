package com.ciaston.przemek.todoapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ciaston.przemek.todoapp.model.TaskModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Przemek on 2018-02-01.
 */

public class DataBaseManager extends SQLiteOpenHelper {

    public DataBaseManager(Context context) {
        super(context, DataBaseConstant.DATABASE, null, DataBaseConstant.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DataBaseConstant.createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DataBaseConstant.dropTable);
        onCreate(sqLiteDatabase);
    }

    public List<TaskModel> getData() {
        List<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DataBaseConstant.TABLE + " ORDER BY " + DataBaseConstant.DATE + " ASC, " + DataBaseConstant.TIME + " ASC;", null);
        StringBuffer stringBuffer = new StringBuffer();
        TaskModel taskModel;

        while (cursor.moveToNext()) {
            String todoCursor = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstant.TASK));
            String timeCursor = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstant.TIME));
            String dateCursor = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstant.DATE));

            taskModel = new TaskModel();
            taskModel.setTask(todoCursor);
            taskModel.setTime(timeCursor);
            taskModel.setDate(dateCursor);

            stringBuffer.append(taskModel);
            taskList.add(taskModel);
        }
        return taskList;
    }

    public void insertData(String todo, String time, String date) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseConstant.TASK, todo);
        contentValues.put(DataBaseConstant.TIME, time);
        contentValues.put(DataBaseConstant.DATE, date);

        sqLiteDatabase.insert(DataBaseConstant.TABLE, null, contentValues);
    }

    public boolean deleteData(String todo, String time, String date) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String where = DataBaseConstant.TASK + "=? AND "
                    + DataBaseConstant.TIME + "=? AND "
                    + DataBaseConstant.DATE + "=?";
        int result = sqLiteDatabase.delete(DataBaseConstant.TABLE, where, new String[]{todo, time, date});
        if (result > 0) {
            return true;
        }
        return false;
    }

    public void updateData(String task, String time, String date, String oldTask, String oldTime, String oldDate) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseConstant.TASK, task);
        contentValues.put(DataBaseConstant.TIME, time);
        contentValues.put(DataBaseConstant.DATE, date);

        String where = DataBaseConstant.TASK + "=? AND "
                    + DataBaseConstant.TIME + "=? AND "
                    + DataBaseConstant.DATE + "=?";
        sqLiteDatabase.update(DataBaseConstant.TABLE, contentValues, where, new String[]{oldTask, oldTime, oldDate});
    }
}

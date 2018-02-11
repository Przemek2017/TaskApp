package com.ciaston.przemek.taskapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ciaston.przemek.taskapp.model.TaskModel;

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

    public List<TaskModel> getTask() {
        List<TaskModel> taskList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + DataBaseConstant.TABLE + " ORDER BY " + DataBaseConstant.DATE + " ASC, " + DataBaseConstant.TIME + " ASC;", null);
        StringBuffer stringBuffer = new StringBuffer();
        TaskModel taskModel;

        while (cursor.moveToNext()) {
            String idCursor = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstant.ID));
            String todoCursor = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstant.TASK));
            String timeCursor = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstant.TIME));
            String dateCursor = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseConstant.DATE));

            taskModel = new TaskModel();
            taskModel.setId(idCursor);
            taskModel.setTask(todoCursor);
            taskModel.setTime(timeCursor);
            taskModel.setDate(dateCursor);

            stringBuffer.append(taskModel);
            taskList.add(taskModel);
        }
        return taskList;
    }

    public void insertTask(TaskModel taskModel) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseConstant.TASK, taskModel.getTask());
        contentValues.put(DataBaseConstant.TIME, taskModel.getTime());
        contentValues.put(DataBaseConstant.DATE, taskModel.getDate());

        sqLiteDatabase.insert(DataBaseConstant.TABLE, null, contentValues);
    }

    public void updateTask(TaskModel taskModel) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseConstant.TASK, taskModel.getTask());
        contentValues.put(DataBaseConstant.TIME, taskModel.getTime());
        contentValues.put(DataBaseConstant.DATE, taskModel.getDate());

        String where = DataBaseConstant.ID + "=?";
        sqLiteDatabase.update(DataBaseConstant.TABLE, contentValues, where, new String[]{taskModel.getId()});
    }

    public boolean deleteTask(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String where = DataBaseConstant.ID + "=?";
        int result = sqLiteDatabase.delete(DataBaseConstant.TABLE, where, new String[]{id});
        if (result > 0) {
            return true;
        }
        return false;
    }
}

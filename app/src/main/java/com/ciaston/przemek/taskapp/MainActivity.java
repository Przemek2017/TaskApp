package com.ciaston.przemek.taskapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ciaston.przemek.taskapp.adapter.TaskAdapter;
import com.ciaston.przemek.taskapp.broadcast.TaskNotification;
import com.ciaston.przemek.taskapp.db.DataBaseManager;
import com.ciaston.przemek.taskapp.model.TaskModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView task, time, date, addTime, addDate;
    private RecyclerView recyclerView;

    private List<TaskModel> taskList = new ArrayList<>();
    private TaskAdapter taskAdapter;

    private DataBaseManager dataBaseManager;

    private Calendar getDate = Calendar.getInstance();

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String setTime = "czas";
    private String setDate = "data";

    private Paint paint = new Paint();

    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBaseManager = new DataBaseManager(this);

        intentForNotification();

        findViewById();
        initDataFromDB();
        initRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void intentForNotification() {
        intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();

            String setTime1 = timeFormat.format(calendar.getTime());
            String setDate1 = dateFormat.format(calendar.getTime());

            List<TaskModel> taskList = dataBaseManager.getData();
            for (TaskModel taskModel : taskList) {
                String notificationTime = taskModel.getTime().toString();
                String notificationDate = taskModel.getDate().toString();
                if (notificationTime.equals(setTime1) && notificationDate.equals(setDate1)) {
                    TaskNotification.createNotification(getApplicationContext(), R.string.its_high_time_to, taskModel.getTask());
                }
            }
        }
    };

    private void updateDate() {
        new DatePickerDialog(this, dateListener, getDate.get(Calendar.YEAR), getDate.get(Calendar.MONTH), getDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateTime() {
        new TimePickerDialog(this, timeListener, getDate.get(Calendar.HOUR_OF_DAY), getDate.get(Calendar.MINUTE), true).show();
    }

    DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            getDate.set(Calendar.YEAR, year);
            getDate.set(Calendar.MONTH, monthOfYear);
            getDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDate = dateFormat.format(getDate.getTime());
            addDate.setText(setDate.toString());
        }
    };

    TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            getDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            getDate.set(Calendar.MINUTE, minute);
            setTime = timeFormat.format(getDate.getTime());
            addTime.setText(setTime.toString());
        }
    };

    private void addTask() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.add_task);
        alertDialog.setIcon(R.drawable.list64);

        LayoutInflater inflater = getLayoutInflater();
        View addTaskView = inflater.inflate(R.layout.alert_dialog_view, null);
        addTaskView.setBackgroundResource(R.drawable.bg_add_item_color);

        final EditText addTodo = addTaskView.findViewById(R.id.addTask);
        final ImageButton addDateImage = addTaskView.findViewById(R.id.imageAddDate);
        final ImageButton addTimeImage = addTaskView.findViewById(R.id.imageAddTime);

        addDate = addTaskView.findViewById(R.id.textViewDate);
        addTime = addTaskView.findViewById(R.id.textViewTime);

        addTimeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTime();
            }
        });
        addDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDate();
            }
        });

        alertDialog.setView(addTaskView);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String todo = addTodo.getText().toString();
                String time = addTime.getText().toString();
                String date = addDate.getText().toString();

                dataBaseManager.insertData(todo, time, date);
                initDataFromDB();
            }
        });
        alertDialog.show();
    }

    private void editTask(final String task, final String time, final String date) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.edit_task);
        alertDialog.setIcon(R.drawable.edit64);

        LayoutInflater inflater = getLayoutInflater();
        View editView = inflater.inflate(R.layout.alert_dialog_view, null);
        editView.setBackgroundResource(R.drawable.bg_edit_item_color);

        final EditText taskEdit = editView.findViewById(R.id.addTask);
        final ImageButton addDateImage = editView.findViewById(R.id.imageAddDate);
        final ImageButton addTimeImage = editView.findViewById(R.id.imageAddTime);

        addDate = editView.findViewById(R.id.textViewDate);
        addTime = editView.findViewById(R.id.textViewTime);

        taskEdit.setText(task);
        addTime.setText(time);
        addDate.setText(date);

        addDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDate();
            }
        });

        addTimeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTime();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.setView(editView);
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initDataFromDB();
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String getTask = taskEdit.getText().toString();
                String getTime = addTime.getText().toString();
                String getDate = addDate.getText().toString();

                dataBaseManager.updateData(getTask, getTime, getDate, task, time, date);
                initDataFromDB();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initDataFromDB() {
        taskList = dataBaseManager.getData();
        taskAdapter = new TaskAdapter(taskList, getApplicationContext());
        recyclerView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();

    }

    private void findViewById() {
        recyclerView = findViewById(R.id.recyclerView);

        task = findViewById(R.id.inputTask);
        time = findViewById(R.id.inputTime);
        date = findViewById(R.id.inputDate);
        initSwipe();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                TaskModel taskModel = taskAdapter.getData(position);
                final String task = taskModel.getTask();
                final String time = taskModel.getTime();
                final String date = taskModel.getDate();

                if (direction == ItemTouchHelper.LEFT) {
                    boolean check = dataBaseManager.deleteData(task, time, date);
                    taskAdapter.removeItem(position);
                    taskAdapter.notifyDataSetChanged();
                    if (check) {
                        Toast.makeText(getApplicationContext(), R.string.task_deleted, Toast.LENGTH_SHORT).show();
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    editTask(task, time, date);
                }
            }

            @Override
            public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    int padding = 15;

                    if (dX < -1) {
                        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorDeleteItem);
                        paint.setColor(color);
                        RectF background = new RectF((float) itemView.getLeft() - dX,
                                (float) itemView.getTop(),
                                (float) itemView.getRight(),
                                (float) itemView.getBottom());

                        canvas.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete64);
                        RectF deleteIcon = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + padding,
                                (float) itemView.getRight(),
                                (float) itemView.getBottom() - padding);

                        canvas.drawBitmap(icon, null, deleteIcon, paint);
                    } else if (dX >= 1) {
                        int color = ContextCompat.getColor(getApplicationContext(), R.color.colorEditItem);
                        paint.setColor(color);
                        RectF background = new RectF((float) itemView.getLeft(),
                                (float) itemView.getTop(),
                                (float) itemView.getRight() - dX,
                                (float) itemView.getBottom());

                        canvas.drawRect(background, paint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.edit64);
                        RectF editIcon = new RectF(itemView.getLeft() + padding,
                                itemView.getTop() + padding,
                                itemView.getRight() - padding * width - (padding * 4),
                                itemView.getBottom() - padding);

                        canvas.drawBitmap(icon, null, editIcon, paint);
                    }
                }
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_task:
                addTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


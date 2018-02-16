package com.ciaston.przemek.taskapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ciaston.przemek.taskapp.adapter.TaskAdapter;
import com.ciaston.przemek.taskapp.controller.SwipeController;
import com.ciaston.przemek.taskapp.controller.SwipeControllerActions;
import com.ciaston.przemek.taskapp.notification.TaskNotification;
import com.ciaston.przemek.taskapp.db.DataBaseManager;
import com.ciaston.przemek.taskapp.model.TaskModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Resources resources;

    TextView task, time, date, addTime, addDate;
    private RecyclerView recyclerView;
    private List<TaskModel> taskList = new ArrayList<>();
    private TaskAdapter taskAdapter;

    private DataBaseManager dataBaseManager;

    private Calendar getDate = Calendar.getInstance();
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String setTime = "";
    private String setDate = "";

    private IntentFilter intentFilter;
    private SwipeController swipeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = getResources();

        final String appName = getResources().getString(R.string.app_name);
        showAppNameCollapsing(appName);

        dataBaseManager = new DataBaseManager(this);
        intentForNotification();

        findViewById();
        initDataFromDB();
        initRecyclerView();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                TaskModel taskModel = taskAdapter.getData(position);
                dataBaseManager.deleteTask(taskModel.getId());
                initDataFromDB();
            }

            @Override
            public void onLeftClicked(int position) {
                TaskModel taskModel = taskAdapter.getData(position);
                editTask(taskModel);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

    }

    private void showAppNameCollapsing(final String appName) {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean showAppName = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(appName);
                    showAppName = true;
                } else if (showAppName) {
                    collapsingToolbarLayout.setTitle(" ");
                    showAppName = false;
                }
            }
        });
    }

    private void intentForNotification() {
        intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();

            String title = getResources().getString(R.string.its_high_time_to);

            String getTimeFromCalendar = timeFormat.format(calendar.getTime());
            String getDateFromCalendar = dateFormat.format(calendar.getTime());

            List<TaskModel> taskList = dataBaseManager.getTask();
            for (TaskModel taskModel : taskList) {
                String notificationTime = taskModel.getTime().toString();
                String notificationDate = taskModel.getDate().toString();
                if (notificationTime.equals(getTimeFromCalendar) && notificationDate.equals(getDateFromCalendar)) {
                    TaskNotification.createNotification(getApplicationContext(), title, taskModel.getTask());
                } else if (notificationTime.equals(getTimeFromCalendar) && notificationDate.isEmpty()) {
                    TaskNotification.createNotification(getApplicationContext(), title, taskModel.getTask());
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
        final View addTaskView = inflater.inflate(R.layout.alert_dialog_view, null);
        addTaskView.setBackgroundResource(R.drawable.bg_add_item_color);

        final EditText addTask = addTaskView.findViewById(R.id.addTask);
        addTask.setHint(R.string.add_task);
        final ImageButton addDateImage = addTaskView.findViewById(R.id.imageAddDate);
        final ImageButton addTimeImage = addTaskView.findViewById(R.id.imageAddTime);

        addDate = addTaskView.findViewById(R.id.textViewDate);
        addTime = addTaskView.findViewById(R.id.textViewTime);

        onClickImages(addDateImage, addTimeImage);

        alertDialog.setView(addTaskView);
        alertDialog.setCancelable(false);

        alertDialog.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String taskAdd = addTask.getText().toString().trim();
                final String timeAdd = addTime.getText().toString();
                final String dateAdd = addDate.getText().toString();

                TaskModel taskModel = new TaskModel(taskAdd, timeAdd, dateAdd);
                dataBaseManager.insertTask(taskModel);
                initDataFromDB();
            }
        });

        alertDialog.setNeutralButton(R.string.clear, null);

        final AlertDialog alert = alertDialog.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask.setText("");
                addTime.setText("");
                addDate.setText("");
            }
        });

        isEnabledPositiveButton(addTask, alert);
    }

    private void editTask(final TaskModel taskModel) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(R.string.edit_task);
        alertDialog.setIcon(R.drawable.edit64);

        LayoutInflater inflater = getLayoutInflater();
        View editView = inflater.inflate(R.layout.alert_dialog_view, null);
        editView.setBackgroundResource(R.drawable.bg_edit_item_color);

        final EditText taskEdit = editView.findViewById(R.id.addTask);
        taskEdit.setHint(R.string.add_task);
        final ImageButton addDateImage = editView.findViewById(R.id.imageAddDate);
        final ImageButton addTimeImage = editView.findViewById(R.id.imageAddTime);

        addDate = editView.findViewById(R.id.textViewDate);
        addTime = editView.findViewById(R.id.textViewTime);

        taskEdit.setText(taskModel.getTask());
        addDate.setText(taskModel.getDate());
        addTime.setText(taskModel.getTime());
        emptyClockWatcher(addDateImage, addTimeImage);

        onClickImages(addDateImage, addTimeImage);

        alertDialog.setCancelable(false);
        alertDialog.setView(editView);
        alertDialog.setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initDataFromDB();
                dialogInterface.dismiss();
            }
        });

        alertDialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                taskModel.setTask(taskEdit.getText().toString().trim());
                taskModel.setTime(addTime.getText().toString());
                taskModel.setDate(addDate.getText().toString());

                dataBaseManager.updateTask(taskModel);
                initDataFromDB();
            }
        });

        alertDialog.setNeutralButton(R.string.clear, null);

        final AlertDialog alert = alertDialog.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskEdit.setText("");
                addTime.setText("");
                addDate.setText("");
            }
        });
        isEnabledPositiveButton(taskEdit, alert);
    }

    // onClick czyści text, onLongClick wyświetla kalendarz/zegar
    private void onClickImages(ImageButton addDateImage, ImageButton addTimeImage) {
        addTimeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTime.setText("");
            }
        });
        addTimeImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                updateTime();
                return true;
            }
        });

        addDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDate.setText("");
            }
        });

        addDateImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                updateDate();
                return true;
            }
        });
    }

    // nasłuchuje czy zegar != "" i ustawia widok kalendarz lub kalendarz/zegar
    private void emptyClockWatcher(final ImageButton addDateImage, final ImageButton addTimeImage) {
        addTime.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int timeLength = addTime.getText().length();
                switch (timeLength){
                    case 0:
                        layoutClock(addDateImage, addTimeImage);
                        break;
                    default:
                        layoutClockCalendar(addTimeImage, addDateImage);
                        break;
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // ukrywa kalendarz gdy zegar = ""
    private void layoutClock(ImageButton addDateImage, ImageButton addTimeImage) {
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);

        addDateImage.setVisibility(View.GONE);
        addDate.setText("");
        addDate.setVisibility(View.GONE);

        addDateImage.setLayoutParams(layoutParams);
        addDate.setLayoutParams(layoutParams);
        addTimeImage.setLayoutParams(layoutParams);
        addTime.setLayoutParams(layoutParams);
    }

    // ustawia widoczny kalendarz gdy zegar != ""
    private void layoutClockCalendar(ImageButton addTimeImage, ImageButton addDateImage) {
        int margin = getResources().getDimensionPixelSize(R.dimen.margin_between_clock_calendar);

        LinearLayout.LayoutParams layoutParamsTime =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsTime.setMargins(margin, 0, 0, 0);
        addTimeImage.setLayoutParams(layoutParamsTime);
        addTime.setLayoutParams(layoutParamsTime);

        LinearLayout.LayoutParams layoutParamsDate =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsDate.setMargins(0, 0, margin, 0);
        addDateImage.setLayoutParams(layoutParamsDate);
        addDate.setLayoutParams(layoutParamsDate);

        addDateImage.setVisibility(View.VISIBLE);
        addDate.setVisibility(View.VISIBLE);
    }

    // nasłuchuje pole task, jeśli != "" ustawia widoczny positive/neutral button
    private void isEnabledPositiveButton(final EditText taskWatcher, final AlertDialog alert) {
        taskWatcher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    alert.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
                }
            }
        });
    }

    // inicjalizacja recyclerView
    private void initRecyclerView() {
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initDataFromDB() {
        taskList = dataBaseManager.getTask();
        taskAdapter = new TaskAdapter(taskList, getApplicationContext());
        recyclerView.setAdapter(taskAdapter);
        taskAdapter.notifyDataSetChanged();
    }

    private void findViewById() {
        recyclerView = findViewById(R.id.recyclerView);
        task = findViewById(R.id.inputTask);
        time = findViewById(R.id.inputTime);
        date = findViewById(R.id.inputDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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


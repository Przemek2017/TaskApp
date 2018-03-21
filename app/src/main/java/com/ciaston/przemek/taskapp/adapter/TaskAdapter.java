package com.ciaston.przemek.taskapp.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ciaston.przemek.taskapp.R;
import com.ciaston.przemek.taskapp.model.TaskModel;

import java.util.List;

/**
 * Created by Przemek on 2018-01-31.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyHolder> {

    List<TaskModel> taskList;
    Context context;

    public TaskAdapter(List<TaskModel> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        final TaskModel item = taskList.get(position);

        holder.task.setText(item.getTask());
        holder.time.setText(item.getTime().toString());
        holder.date.setText(item.getDate().toString());


        if (item.getDate().isEmpty()) {

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.time.getLayoutParams();
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            holder.time.setLayoutParams(params);
            holder.time.setTextSize(20);
        }
<<<<<<< HEAD

        holder.task.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TaskModel taskModel = getData(position);
                if (taskModel.isComplete()){

                }
                return false;
            }
        });

        if (item.isComplete()){
            holder.task.setPaintFlags(holder.task.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.task.setPaintFlags(holder.task.getPaintFlags() &
                    Paint.STRIKE_THRU_TEXT_FLAG);
        }
=======
>>>>>>> parent of a02dd09... - refactoring v8
    }

    public TaskModel getData(int position) {
        TaskModel item = taskList.get(position);
        return item;
    }

//    public void removeItem(int position){
//        taskList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemChanged(position, taskList.size());
//    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public TextView task;
        public TextView time;
        public TextView date;

        public View layout;

        public MyHolder(View itemView) {
            super(itemView);
            layout = itemView;
            task = layout.findViewById(R.id.inputTask);
            time = layout.findViewById(R.id.inputTime);
            date = layout.findViewById(R.id.inputDate);

        }

    }
}

package com.ciaston.przemek.todoapp.adapter;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ciaston.przemek.todoapp.utils.ItemTouchHelperAdapter;
import com.ciaston.przemek.todoapp.R;
import com.ciaston.przemek.todoapp.model.TaskModel;
import com.ciaston.przemek.todoapp.utils.ItemTouchHelperViewHolder;

import java.util.List;

/**
 * Created by Przemek on 2018-01-31.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyHolder> implements ItemTouchHelperAdapter, View.OnTouchListener {

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
    public void onBindViewHolder(MyHolder holder, int position) {
        TaskModel item = taskList.get(position);

        holder.task.setText(item.getTask());
        holder.time.setText(item.getTime().toString());
        holder.date.setText(item.getDate().toString());
    }

    public TaskModel getData(int position){
        TaskModel item = taskList.get(position);
        return item;
    }

    public void removeItem(int position){
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, taskList.size());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        TaskModel todoPosition = taskList.remove(fromPosition);
        taskList.add(toPosition > fromPosition ? toPosition - 1 : toPosition, todoPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        ClipData clipData = ClipData.newPlainText("", "");
        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(clipData, dragShadowBuilder, view, 0);
        return true;
    }



    public class MyHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

        public TextView task;
        public TextView time;
        public TextView date;

        public MyHolder(View itemView) {
            super(itemView);

            task = itemView.findViewById(R.id.inputTask);
            time = itemView.findViewById(R.id.inputTime);
            date = itemView.findViewById(R.id.inputDate);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.BLUE);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.GRAY);
        }
    }
}

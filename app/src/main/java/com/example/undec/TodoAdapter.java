package com.example.undec;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskViewHolder> {
    private final Context context;
    private final List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(int position);
    }

    // Constructor with context and tasks
    public TodoAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    // Constructor with tasks and listener
    public TodoAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.context = null;
        this.tasks = tasks;
        this.listener = listener;
    }

    // Constructor with context, tasks, and listener
    public TodoAdapter(Context context, List<Task> tasks, OnTaskClickListener listener) {
        this.context = context;
        this.tasks = tasks;
        this.listener = listener;
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(position);
            }
        });
        
        holder.taskCheckbox.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView taskTitle;
        private final TextView taskTime;
        private final View priorityIndicator;
        protected final MaterialCheckBox taskCheckbox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskTime = itemView.findViewById(R.id.taskTime);
            priorityIndicator = itemView.findViewById(R.id.priorityIndicator);
            taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
        }

        void bind(Task task) {
            taskTitle.setText(task.getName());
            taskCheckbox.setChecked(task.isCompleted());
            taskTime.setText(task.getFormattedDate());

            // Strike through text if task is completed
            if (task.isCompleted()) {
                taskTitle.setPaintFlags(taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                taskTime.setPaintFlags(taskTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                taskTitle.setPaintFlags(taskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                taskTime.setPaintFlags(taskTime.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            int priorityColor;
            switch (task.getPriority().toLowerCase()) {
                case "high":
                    priorityColor = itemView.getContext().getResources().getColor(R.color.priority_high);
                    break;
                case "medium":
                    priorityColor = itemView.getContext().getResources().getColor(R.color.priority_medium);
                    break;
                default:
                    priorityColor = itemView.getContext().getResources().getColor(R.color.priority_low);
            }
            priorityIndicator.setBackgroundColor(priorityColor);
        }
    }
}
package com.example.undec;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnTaskClickListener listener;

    public interface OnTaskClickListener {
        void onTaskClick(int position);
    }

    public TodoAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
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
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private RadioButton taskCheckbox;
        private TextView taskTitle;
        private TextView taskTime;
        private View priority;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskTime = itemView.findViewById(R.id.taskTime);
            priority = itemView.findViewById(R.id.statusIndicator);
            taskCheckbox = itemView.findViewById(R.id.taskRadio);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onTaskClick(position);
                }
            });
        }

        public void bind(Task task) {
            taskTitle.setText(task.getName());
            taskCheckbox.setChecked(task.isCompleted());
            taskTime.setText(task.getFormattedDate());

            // Set priority color
            switch (task.getPriority().toLowerCase()) {
                case "high":
                    priority.setBackgroundColor(Color.RED);
                    break;
                case "medium":
                    priority.setBackgroundColor(Color.YELLOW);
                    break;
                default: // low
                    priority.setBackgroundColor(Color.GREEN);
            }
        }
    }
}
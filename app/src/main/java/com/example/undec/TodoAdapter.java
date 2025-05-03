package com.example.undec;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private List<Task> taskList;

    public TodoAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public static class TodoViewHolder extends RecyclerView.ViewHolder {
        RadioButton taskRadio;
        TextView taskTitle;
        CardView cardView;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            taskRadio = itemView.findViewById(R.id.taskRadio);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            cardView = itemView.findViewById(R.id.cardView); // CardView must have this ID in XML
        }

        public void bind(Task task) {
            taskTitle.setText(task.title);
            taskRadio.setChecked(task.isCompleted);
            setStrikeThrough(taskTitle, task.isCompleted);
            setCardBackground(task.isCompleted);

            taskRadio.setOnClickListener(v -> {
                task.isCompleted = !task.isCompleted;
                taskRadio.setChecked(task.isCompleted);
                setStrikeThrough(taskTitle, task.isCompleted);
                setCardBackground(task.isCompleted);
            });
        }

        private void setStrikeThrough(TextView textView, boolean isStriked) {
            if (isStriked) {
                textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        private void setCardBackground(boolean isCompleted) {
            int color = isCompleted
                    ? Color.parseColor("#EEEEEE")  // Gray background when completed
                    : Color.WHITE;                // Default white
            cardView.setCardBackgroundColor(color);
        }
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo_task, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.bind(taskList.get(position));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}

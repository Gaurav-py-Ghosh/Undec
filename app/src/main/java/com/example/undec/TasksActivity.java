package com.example.undec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.widget.Button;

public class TasksActivity extends BaseActivity {
    // Views
    private RecyclerView recyclerView;
    private FloatingActionButton addTaskButton;
    private BottomNavView bottomNavigation;
    private StreakCalendarView calendarView;
    private static final String TAG = "TasksActivity";

    // Data
    private final List<Task> tasks = new ArrayList<>();
    private StreakManager streakManager;
    private String currentDate;
    private TaskAdapter taskAdapter;

    // Activity result launcher
    private final ActivityResultLauncher<Intent> addTaskLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadTasks();
                    updateStreakCalendar();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_tasks);
            Log.d(TAG, "TasksActivity onCreate started");

            // Initialize StreakManager here after context is available
            streakManager = new StreakManager(this);

            if (!initializeViews()) {
                Log.e(TAG, "Failed to initialize views - aborting initialization");
                showMessage("Error loading tasks screen. Some UI elements are missing.");
                return;
            }

            setupCurrentDate();
            setupBottomNavigation(R.id.nav_tasks);
            loadTasks();
            setupAddTaskButton();
            updateStreakCalendar();

            Log.d(TAG, "TasksActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in TasksActivity onCreate: " + e.getMessage(), e);
            showMessage("Error initializing tasks screen: " + e.getMessage());
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_tasks;
    }

    private boolean initializeViews() {
        try {
            recyclerView = findViewById(R.id.tasksRecyclerView);
            addTaskButton = findViewById(R.id.fabAddTask);
            bottomNavigation = findViewById(R.id.customBottomNav);
            calendarView = findViewById(R.id.streakCalendarView);

            boolean allViewsFound = true;

            if (recyclerView == null) {
                Log.e(TAG, "tasksRecyclerView is null");
                allViewsFound = false;
            }
            if (addTaskButton == null) {
                Log.e(TAG, "fabAddTask is null");
                allViewsFound = false;
            }
            if (bottomNavigation == null) {
                Log.e(TAG, "customBottomNav is null");
                allViewsFound = false;
            }
            if (calendarView == null) {
                Log.e(TAG, "streakCalendarView is null");
                allViewsFound = false;
            }

            if (allViewsFound) {
                bottomNavigation.setSelectedIndex(1);
            }

            return allViewsFound;
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            return false;
        }
    }

    private void setupCurrentDate() {
        currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
    }

    private void loadTasks() {
        try {
            // Initialize with sample data - replace with your actual implementation
            tasks.clear();

            // Use the Task class with date field
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Calendar.getInstance().getTime());

            // Get tomorrow's date
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            String tomorrowDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(cal.getTime());

            // Get yesterday's date
            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(cal.getTime());

            // Add tasks with different dates
            tasks.add(new Task("Complete math assignment", false, "high", todayDate));
            tasks.add(new Task("Read chapter 5", true, "medium", todayDate));
            tasks.add(new Task("Prepare for lab", false, "low", tomorrowDate));
            tasks.add(new Task("Submit research paper", false, "high", yesterdayDate));

            // Create and set the adapter with new constructor matching TodoAdapter
            taskAdapter = new TaskAdapter(tasks);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(taskAdapter);

            Log.d(TAG, "Tasks loaded successfully, count: " + tasks.size());
        } catch (Exception e) {
            Log.e(TAG, "Error loading tasks: " + e.getMessage(), e);
        }
    }

    private void updateStreakCalendar() {
        try {
            // Get completed days from StreakManager
            List<Integer> completedDays = streakManager.getCurrentMonthCompletedDays();
            calendarView.setCompletedDays(completedDays);
            calendarView.setStreakCount(streakManager.getCurrentStreak());
            Log.d(TAG, "Streak calendar updated");
        } catch (Exception e) {
            Log.e(TAG, "Error updating streak calendar: " + e.getMessage(), e);
        }
    }

    private void updateTaskCompletion(Task task, boolean isCompleted) {
        task.setCompleted(isCompleted);
        
        // Check if all tasks for today are completed
        if (areAllTasksForTodayCompleted()) {
            // Mark today as complete in streak
            streakManager.markDateComplete(currentDate);
            showMessage("🔥 All tasks completed for today!");
        } else {
            // Mark today as incomplete in streak
            streakManager.markDateIncomplete(currentDate);
        }
        
        // Update the streak calendar
        updateStreakCalendar();
    }

    private boolean areAllTasksForTodayCompleted() {
        if (tasks.isEmpty()) return false;
        
        boolean hasTodayTasks = false;
        for (Task task : tasks) {
            if (task.getDate().equals(currentDate)) {
                hasTodayTasks = true;
                if (!task.isCompleted()) {
                    return false;
                }
            }
        }
        return hasTodayTasks;
    }

    private boolean areAllTasksCompleted() {
        if (tasks.isEmpty()) return false;
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    private void setupAddTaskButton() {
        try {
            addTaskButton.setOnClickListener(v -> showAddTaskDialog());
            Log.d(TAG, "Add task button set up");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up add task button: " + e.getMessage(), e);
        }
    }

    private void showAddTaskDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            EditText taskTitleInput = dialogView.findViewById(R.id.taskTitleInput);
            Spinner prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);
            DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

            // Set up priority spinner
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.priority_levels, R.layout.item_priority_spinner);
            adapter.setDropDownViewResource(R.layout.item_priority_spinner_dropdown);
            prioritySpinner.setAdapter(adapter);

            positiveButton.setOnClickListener(v -> {
                String taskTitle = taskTitleInput.getText().toString().trim();
                if (taskTitle.isEmpty()) {
                    taskTitleInput.setError("Task title is required");
                    return;
                }

                String priority = prioritySpinner.getSelectedItem().toString().toLowerCase();
                
                // Get selected date
                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                String taskDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(calendar.getTime());

                // Create and add the new task
                Task newTask = new Task(taskTitle, false, priority, taskDate);
                tasks.add(newTask);
                taskAdapter.notifyItemInserted(tasks.size() - 1);

                // Update streak if task is for today
                if (taskDate.equals(currentDate)) {
                    updateTaskCompletion(newTask, false);
                }

                showMessage("Task added successfully");
                dialog.dismiss();
            });
        });

        dialog.show();

        // Style the dialog
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
    }

    // Task adapter implementation
    private class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {
        private final List<Task> taskList;

        public TaskAdapter(List<Task> taskList) {
            this.taskList = taskList;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo_task, parent, false);
            return new TaskViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            Task task = taskList.get(position);
            holder.taskTitle.setText(task.title);

            // Set the task date if available
            if (holder.taskTime != null) {
                // Format the date for display (check if the date property exists first)
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
                    if (task.date != null) {
                        Date date = inputFormat.parse(task.date);
                        holder.taskTime.setText(outputFormat.format(date));
                    } else {
                        holder.taskTime.setText("Today"); // Default if no date
                    }
                } catch (Exception e) {
                    holder.taskTime.setText("Today");
                    Log.e(TAG, "Error formatting date: " + e.getMessage());
                }
            }

            // Set priority color if the view exists
            if (holder.priority != null) {
                int color;
                switch (task.priority.toLowerCase()) {
                    case "high":
                        color = android.graphics.Color.parseColor("#D32F2F"); // Red
                        break;
                    case "medium":
                        color = android.graphics.Color.parseColor("#FFA000"); // Amber
                        break;
                    default:
                        color = android.graphics.Color.parseColor("#388E3C"); // Green
                        break;
                }
                holder.priority.setBackgroundColor(color);
            }

            holder.taskCheckbox.setChecked(task.completed);

            // Handle task completion
            holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateTaskCompletion(task, isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }
    }

    // Task ViewHolder implementation
    private static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskTime;
        View priority;
        MaterialCheckBox taskCheckbox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskTime = itemView.findViewById(R.id.taskTime);
            priority = itemView.findViewById(R.id.priorityIndicator);
            taskCheckbox = itemView.findViewById(R.id.taskCheckbox);
        }
    }

    // Task class definition using full Task properties
    private static class Task {
        String title;
        boolean completed;
        String priority;
        String date;  // Date in yyyy-MM-dd format

        public Task(String title, boolean completed, String priority) {
            this.title = title;
            this.completed = completed;
            this.priority = priority;
            // Set default date to today
            this.date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Calendar.getInstance().getTime());
        }

        public Task(String title, boolean completed, String priority, String date) {
            this.title = title;
            this.completed = completed;
            this.priority = priority;
            this.date = date;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public String getDate() {
            return date;
        }
    }
}
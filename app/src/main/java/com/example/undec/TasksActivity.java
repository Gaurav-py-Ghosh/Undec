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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TasksActivity extends AppCompatActivity {
    // Views
    private RecyclerView recyclerView;
    private FloatingActionButton addTaskButton;
    private BottomNavView bottomNavigation;
    private StreakCalendarView calendarView;
    private static final String TAG = "TasksActivity";

    // Data
    private final List<Task> tasks = new ArrayList<>();
    private final StreakManager streakManager = new StreakManager(this);
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

            if (!initializeViews()) {
                Log.e(TAG, "Failed to initialize views - aborting initialization");
                Toast.makeText(this, "Error loading tasks screen. Some UI elements are missing.", Toast.LENGTH_LONG).show();
                return;
            }

            setupCurrentDate();
            setupBottomNavigation();
            loadTasks();
            setupAddTaskButton();
            updateStreakCalendar();

            Log.d(TAG, "TasksActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in TasksActivity onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing tasks screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            // Sample data - replace with your actual implementation
            List<Integer> completedDays = new ArrayList<>();
            completedDays.add(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            calendarView.setCompletedDays(completedDays);
            calendarView.setStreakCount(1); // Sample streak count
            Log.d(TAG, "Streak calendar updated");
        } catch (Exception e) {
            Log.e(TAG, "Error updating streak calendar: " + e.getMessage(), e);
        }
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
            addTaskButton.setOnClickListener(v -> {
                // Replace with your actual task creation activity
                Toast.makeText(this, "Add task functionality", Toast.LENGTH_SHORT).show();
            });
            Log.d(TAG, "Add task button set up");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up add task button: " + e.getMessage(), e);
        }
    }

    private void setupBottomNavigation() {
        try {
            if (bottomNavigation == null) {
                Log.e(TAG, "bottomNavigation is null in setupBottomNavigation");
                Toast.makeText(this, "Navigation not available", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set the correct index first
            bottomNavigation.setSelectedIndex(1); // Tasks is at index 1

            bottomNavigation.setOnNavItemSelected(index -> {
                Log.d(TAG, "Navigation item selected: " + index);

                if (index == bottomNavigation.getSelectedIndex()) {
                    Log.d(TAG, "Already on this tab (index " + index + ")");
                    return;
                }

                Intent intent = null;
                switch (index) {
                    case 0:
                        Log.d(TAG, "Home tab selected");
                        intent = new Intent(this, HomeActivity.class);
                        break;
                    case 1:
                        Log.d(TAG, "Tasks tab selected, already here");
                        return; // Already on tasks screen
                    case 2:
                        Log.d(TAG, "Notes tab selected");
                        intent = new Intent(this, NotesActivity.class);
                        break;
                    case 3:
                        Log.d(TAG, "Profile tab selected");
                        intent = new Intent(this, ProfileActivity.class);
                        break;
                    case 4:
                        Log.d(TAG, "Settings tab selected");
                        intent = new Intent(this, SettingsActivity.class);
                        break;
                }

                if (intent != null) {
                    try {
                        Log.d(TAG, "Starting activity for tab index: " + index);
                        // Clear the back stack and start fresh
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        // Don't finish this activity to allow back navigation
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating to activity: " + e.getMessage(), e);
                        Toast.makeText(this, "Navigation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Log.d(TAG, "Bottom navigation set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation: " + e.getMessage(), e);
            Toast.makeText(this, "Error setting up navigation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStreakCalendar();
        if (bottomNavigation != null) {
            bottomNavigation.setSelectedIndex(1);
        }
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
                task.completed = isChecked;

                // Only update streak calendar if ALL tasks for TODAY are completed
                if (isChecked && areAllTasksForTodayCompleted()) {
                    updateStreakCalendar();
                    Toast.makeText(TasksActivity.this, "All of today's tasks completed!", Toast.LENGTH_SHORT).show();
                }
                // If unchecking a task, don't update the streak calendar
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
        RadioButton taskCheckbox;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskTime = itemView.findViewById(R.id.taskTime);
            priority = itemView.findViewById(R.id.statusIndicator);
            taskCheckbox = itemView.findViewById(R.id.taskRadio);
        }
    }

    // Add helper method to check if all of today's tasks are completed
    private boolean areAllTasksForTodayCompleted() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        boolean hasTodayTasks = false;

        for (Task task : tasks) {
            // Only consider tasks scheduled for today
            if (today.equals(task.date)) {
                hasTodayTasks = true;
                if (!task.completed) {
                    return false;
                }
            }
        }

        // Only return true if there are tasks for today and all are completed
        return hasTodayTasks;
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
    }
}
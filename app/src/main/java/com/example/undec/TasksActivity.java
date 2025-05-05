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
import android.widget.AutoCompleteTextView;

public class TasksActivity extends BaseActivity {
    // Views
    private RecyclerView recyclerView;
    private FloatingActionButton addTaskButton;
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
            if (calendarView == null) {
                Log.e(TAG, "streakCalendarView is null");
                allViewsFound = false;
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

            // Add tasks with different dates - get these from TaskManager instead if you have real data
            tasks.add(new Task("Complete math assignment", false, "high", todayDate));
            tasks.add(new Task("Read chapter 5", true, "medium", todayDate));
            tasks.add(new Task("Prepare for lab", false, "low", tomorrowDate));
            tasks.add(new Task("Submit research paper", false, "high", yesterdayDate));

            // Create and set the adapter
            taskAdapter = new TaskAdapter(tasks, position -> {
                // Toggle task completion
                Task task = tasks.get(position);
                task.setCompleted(!task.isCompleted());
                taskAdapter.notifyItemChanged(position);
                
                // Update streak if task's date is today
                if (task.getDate().equals(currentDate)) {
                    updateTaskCompletion(task, task.isCompleted());
                }
                
                // Show feedback
                if (task.isCompleted()) {
                    showMessage("Task marked as complete");
                } else {
                    showMessage("Task marked as incomplete");
                }
            });
            
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(taskAdapter);

            Log.d(TAG, "Tasks loaded successfully, count: " + tasks.size());
        } catch (Exception e) {
            Log.e(TAG, "Error loading tasks: " + e.getMessage(), e);
            showMessage("Error loading tasks: " + e.getMessage());
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
        try {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
            
            // Find views
            EditText taskTitleInput = dialogView.findViewById(R.id.taskTitleInput);
            AutoCompleteTextView prioritySpinner = dialogView.findViewById(R.id.prioritySpinner);
            DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
            
            // Set up priority dropdown
            String[] priorities = new String[]{"Low", "Medium", "High"};
            ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(
                    this, 
                    R.layout.item_priority_spinner, 
                    R.id.text1,
                    priorities);
            priorityAdapter.setDropDownViewResource(R.layout.item_priority_spinner_dropdown);
            prioritySpinner.setAdapter(priorityAdapter);
            prioritySpinner.setText(priorities[1], false); // Default to Medium
            prioritySpinner.setOnItemClickListener((parent, view, position, id) -> {
                prioritySpinner.setText(priorities[position], false);
            });
            
            AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Add New Task")
                .setView(dialogView)
                .setPositiveButton("Add", null)  // We'll set this in onShowListener
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create();
            
            dialog.setOnShowListener(dialogInterface -> {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(view -> {
                    String taskTitle = taskTitleInput.getText().toString().trim();
                    String priority = prioritySpinner.getText().toString().toLowerCase();
                    
                    // Get selected date
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(calendar.getTime());
                    
                    if (taskTitle.isEmpty()) {
                        taskTitleInput.setError("Task title cannot be empty");
                        return;
                    }
                    
                    // Create new task and add it
                    Task newTask = new Task(taskTitle, false, priority, formattedDate);
                    tasks.add(newTask);
                    taskAdapter.notifyDataSetChanged();
                    
                    // Update UI and show confirmation
                    updateStreakCalendar();
                    dialog.dismiss();
                    Toast.makeText(TasksActivity.this, "Task added: " + taskTitle, Toast.LENGTH_SHORT).show();
                    
                    Log.d(TAG, "New task added: " + taskTitle + ", priority: " + priority + ", date: " + formattedDate);
                });
            });
            
            dialog.show();
            Log.d(TAG, "Add task dialog shown");
        } catch (Exception e) {
            Log.e(TAG, "Error showing add task dialog: " + e.getMessage(), e);
            Toast.makeText(this, "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Interface for task click events
    interface OnTaskClickListener {
        void onTaskClick(int position);
    }
    
    // Task adapter implementation
    private class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
        private final List<Task> taskList;
        private final OnTaskClickListener listener;

        public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
            this.taskList = taskList;
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
            Task task = taskList.get(position);
            
            // Set task data
            holder.taskTitle.setText(task.getName());
            holder.taskCheckbox.setChecked(task.isCompleted());
            holder.taskTime.setText(task.getFormattedDate());
            
            // Set visual indicators for completion
            if (task.isCompleted()) {
                holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                holder.taskTime.setPaintFlags(holder.taskTime.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                holder.taskTime.setPaintFlags(holder.taskTime.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
            }
            
            // Set priority color
            int priorityColor;
            switch (task.getPriority().toLowerCase()) {
                case "high":
                    priorityColor = holder.itemView.getContext().getResources().getColor(R.color.priority_high);
                    break;
                case "medium":
                    priorityColor = holder.itemView.getContext().getResources().getColor(R.color.priority_medium);
                    break;
                default:
                    priorityColor = holder.itemView.getContext().getResources().getColor(R.color.priority_low);
                    break;
            }
            holder.priority.setBackgroundColor(priorityColor);
            
            // Set click listeners
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
            return taskList.size();
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {
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
            this.date = getCurrentDate();
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

        public String getName() {
            return title;
        }

        public String getFormattedDate() {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d", Locale.getDefault());
                if (date != null) {
                    Date date = inputFormat.parse(this.date);
                    return outputFormat.format(date);
                } else {
                    return "Today"; // Default if no date
                }
            } catch (Exception e) {
                return "Today";
            }
        }

        public String getPriority() {
            return priority;
        }
        
        private String getCurrentDate() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
    }

    @Override
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Message shown: " + message);
    }
}
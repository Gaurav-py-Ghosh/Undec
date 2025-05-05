package com.example.undec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private TaskManager taskManager;
    private List<Task> todayTasks;
    private List<Note> recentNotes;
    private TodoAdapter todoAdapter;
    private NoteAdapter noteAdapter;
    private StreakCalendarView streakCalendarView;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "HomeActivity onCreate started");

        try {
            // Initialize TaskManager
            taskManager = new TaskManager(this);
    
            // Initialize views
            streakCalendarView = findViewById(R.id.streakCalendarView);
            RecyclerView todoRecyclerView = findViewById(R.id.todoRecyclerView);
            RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
    
            // Load today's tasks
            todayTasks = taskManager.getTodayTasks();
            
            // Add sample tasks if none exist
            if (todayTasks.isEmpty()) {
                String today = getCurrentDate();
                todayTasks.add(new Task("Complete assignment", false, "low", today));
                todayTasks.add(new Task("Study for exam", false, "high", today));
                todayTasks.add(new Task("Submit homework", false, "medium", today));
                
                // Save sample tasks
                for (Task task : todayTasks) {
                    taskManager.addTask(task);
                }
            }
            
            Log.d(TAG, "Loaded " + todayTasks.size() + " tasks for today");
    
            // Setup tasks RecyclerView
            todoAdapter = new TodoAdapter(todayTasks, position -> {
                Task task = todayTasks.get(position);
                // Toggle completion status
                task.setCompleted(!task.isCompleted());
                
                // Save the updated task to persistent storage
                taskManager.updateTask(position, task);
                
                // Update the UI
                todoAdapter.notifyItemChanged(position);
                
                // Check if all tasks are complete and update streak
                checkAndMarkStreak();
                
                // Show appropriate toast message
                if (task.isCompleted()) {
                    Toast.makeText(this, "Task marked as complete", Toast.LENGTH_SHORT).show();
                    
                    // Check if this was the last task to complete
                    if (areAllTasksDone()) {
                        Toast.makeText(this, "🔥 All tasks completed for today!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Task marked as incomplete", Toast.LENGTH_SHORT).show();
                }
            });
    
            todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            todoRecyclerView.setAdapter(todoAdapter);
            
            // Load notes
            setupNotesSection(notesRecyclerView);
    
            // Setup add task button
            findViewById(R.id.fabAddTask).setOnClickListener(v -> showAddTaskDialog());
            
            // Setup add note button
            findViewById(R.id.fabAddNote).setOnClickListener(v -> {
                Toast.makeText(this, "Adding a new note", Toast.LENGTH_SHORT).show();
                // In a real app, you would open a note editor
            });
            
            // Setup navigation buttons
            setupNavigationButtons();
    
            // Initialize streak display
            checkAndMarkStreak();
            Log.d(TAG, "HomeActivity setup complete");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing HomeActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading app: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupNotesSection(RecyclerView notesRecyclerView) {
        try {
            // Get recent notes from repository
            recentNotes = NotesRepository.getRecentNotes();
            
            if (recentNotes.isEmpty()) {
                Log.w(TAG, "No notes found");
                // If needed, you can add sample notes here
            }
            
            Log.d(TAG, "Loaded " + recentNotes.size() + " notes");
            
            // Set up horizontal layout for notes
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    this, LinearLayoutManager.HORIZONTAL, false);
            notesRecyclerView.setLayoutManager(layoutManager);
            
            // Create and set adapter
            noteAdapter = new NoteAdapter(recentNotes, note -> {
                // Handle note click - navigate to NotesActivity
                Toast.makeText(this, "Opening note: " + note.getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, NotesActivity.class);
                startActivity(intent);
            });
            
            notesRecyclerView.setAdapter(noteAdapter);
            Log.d(TAG, "Notes section setup complete");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up notes section: " + e.getMessage(), e);
        }
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        final String[] priorities = {"Low", "Medium", "High"};
        builder.setSingleChoiceItems(priorities, 1, null);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskName = input.getText().toString();
            if (!taskName.isEmpty()) {
                int selectedPriority = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                String priority = "medium";
                if (selectedPriority == 0) priority = "low";
                else if (selectedPriority == 2) priority = "high";

                Task newTask = new Task(taskName, false, priority);
                taskManager.addTask(newTask);
                todayTasks.add(newTask);
                todoAdapter.notifyItemInserted(todayTasks.size() - 1);
                checkAndMarkStreak();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    
    private boolean areAllTasksDone() {
        if (todayTasks.isEmpty()) {
            return false;
        }

        String todayDate = getCurrentDate();
        boolean hasTodayTasks = false;

        for (Task task : todayTasks) {
            // Only consider tasks with today's date
            if (todayDate.equals(task.getDate())) {
                hasTodayTasks = true;
                if (!task.isCompleted()) {
                    return false; // Found an incomplete task for today
                }
            }
        }

        // Return true only if there are tasks for today and all are completed
        return hasTodayTasks;
    }

    private void checkAndMarkStreak() {
        String currentDate = getCurrentDate();
        boolean allTasksCompleted = areAllTasksDone();

        SharedPreferences sharedPreferences = getSharedPreferences("StreakPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        // Only consider a day complete if there are tasks and all are completed
        if (allTasksCompleted) {
            // Store this date as completed in persistent storage
            editor.putBoolean(currentDate, true);
            Log.d(TAG, "Day marked as complete: " + currentDate);
        } else {
            // If any task is incomplete, mark the day as incomplete
            editor.putBoolean(currentDate, false);
            Log.d(TAG, "Day marked as incomplete: " + currentDate);
        }
        editor.apply();

        // Update the streak calendar display
        updateStreakDisplay(sharedPreferences);
    }

    private void updateStreakDisplay(SharedPreferences sharedPreferences) {
        Map<String, ?> allStreaks = sharedPreferences.getAll();
        List<Integer> completedDays = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        
        Log.d(TAG, "Updating streak display for " + currentMonth + "/" + currentYear);

        // Collect all completed days for the current month
        for (Map.Entry<String, ?> entry : allStreaks.entrySet()) {
            try {
                // Only process boolean values (completion status)
                if (entry.getValue() instanceof Boolean && (Boolean) entry.getValue()) {
                    String dateString = entry.getKey();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf.parse(dateString);
                    
                    calendar.setTime(date);
                    // Only include days from the current month
                    if (calendar.get(Calendar.MONTH) == currentMonth && 
                        calendar.get(Calendar.YEAR) == currentYear) {
                        // Get the day of month (1-31)
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        completedDays.add(day);
                        Log.d(TAG, "Found completed day: " + day + " for date " + dateString);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing date: " + entry.getKey(), e);
            }
        }

        if (streakCalendarView != null) {
            // Update the streak calendar with completed days
            streakCalendarView.setCompletedDays(completedDays);
            
            // Calculate and update the streak count
            int streakCount = calculateCurrentStreak(sharedPreferences);
            streakCalendarView.setStreakCount(streakCount);
            
            Log.d(TAG, "Streak updated: " + completedDays.size() + " completed days, streak=" + streakCount);
            
            // Update today's visual status in streak calendar
            int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            String currentDate = getCurrentDate();
            boolean isTodayComplete = sharedPreferences.getBoolean(currentDate, false);
            
            // Set today's status based on task completion
            if (isTodayComplete) {
                // All tasks for today are completed
                streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.COMPLETED);
                Log.d(TAG, "Today (day " + today + ") is marked as COMPLETED");
            } else if (!todayTasks.isEmpty()) {
                // Has tasks but not all completed
                streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.IN_PROGRESS);
                Log.d(TAG, "Today (day " + today + ") is marked as IN_PROGRESS");
            } else {
                // No tasks for today
                streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.NOT_STARTED);
                Log.d(TAG, "Today (day " + today + ") is marked as NOT_STARTED");
            }
        } else {
            Log.e(TAG, "Streak calendar view is null");
        }
    }

    private int calculateCurrentStreak(SharedPreferences sharedPreferences) {
        int streak = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        
        // Start with today's date and work backwards
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        
        // Check for consecutive completed days starting from today
        boolean streakBroken = false;
        int daysChecked = 0;
        while (!streakBroken && daysChecked < 366) { // Limit to a year to prevent infinite loop
            String dateStr = sdf.format(currentDate);
            boolean isDateComplete = sharedPreferences.getBoolean(dateStr, false);
            
            Log.d(TAG, "Checking streak for date: " + dateStr + ", complete: " + isDateComplete);
            
            if (isDateComplete) {
                // This day has all tasks completed
                streak++;
                // Move to the previous day
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                currentDate = calendar.getTime();
            } else {
                // Streak is broken - this day is not complete
                streakBroken = true;
                Log.d(TAG, "Streak broken at " + dateStr);
            }
            
            daysChecked++;
        }
        
        Log.d(TAG, "Final streak count: " + streak);
        return streak;
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh task data
        if (taskManager != null) {
            todayTasks = taskManager.getTodayTasks();
            if (todoAdapter != null) {
                todoAdapter.notifyDataSetChanged();
            }
        }
        
        // Refresh notes
        recentNotes = NotesRepository.getRecentNotes();
        if (noteAdapter != null) {
            noteAdapter.notifyDataSetChanged();
        }
        
        checkAndMarkStreak();
    }

    private void setupNavigationButtons() {
        // Setup Profile button
        findViewById(R.id.btnViewProfile).setOnClickListener(v -> {
            Log.d(TAG, "Opening Profile page");
            try {
                Intent intent = new Intent(this, ProfileActivity.class);
                // Get streak count to pass to profile
                int streakCount = 0;
                if (streakCalendarView != null) {
                    streakCount = streakCalendarView.getStreakCount();
                }
                intent.putExtra("streakCount", streakCount);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to Profile: " + e.getMessage(), e);
                Toast.makeText(this, "Could not open Profile", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Setup View All Notes button
        findViewById(R.id.btnViewAllNotes).setOnClickListener(v -> {
            Log.d(TAG, "Opening Notes page");
            try {
                Intent intent = new Intent(this, NotesActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to Notes: " + e.getMessage(), e);
                Toast.makeText(this, "Could not open Notes", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Setup Tasks button
        findViewById(R.id.btnViewTasks).setOnClickListener(v -> {
            Log.d(TAG, "Opening Tasks page");
            try {
                Intent intent = new Intent(this, TasksActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error navigating to Tasks: " + e.getMessage(), e);
                Toast.makeText(this, "Could not open Tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Note Adapter for the home screen notes
    static class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
        private final List<Note> notes;
        private final OnNoteClickListener listener;

        interface OnNoteClickListener {
            void onNoteClick(Note note);
        }

        NoteAdapter(List<Note> notes, OnNoteClickListener listener) {
            this.notes = notes;
            this.listener = listener;
        }

        @NonNull
        @Override
        public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home_note, parent, false);
            return new NoteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            Note note = notes.get(position);
            holder.bind(note);
            
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNoteClick(note);
                }
            });
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        static class NoteViewHolder extends RecyclerView.ViewHolder {
            private final TextView titleTextView;
            private final TextView categoryTextView;
            private final ImageView noteImageView;

            NoteViewHolder(@NonNull View itemView) {
                super(itemView);
                titleTextView = itemView.findViewById(R.id.noteTitle);
                categoryTextView = itemView.findViewById(R.id.noteCategory);
                noteImageView = itemView.findViewById(R.id.noteImage);
            }

            void bind(Note note) {
                titleTextView.setText(note.getTitle());
                categoryTextView.setText(note.getCategory());
                noteImageView.setImageResource(note.getImageResId());
            }
        }
    }

    // Task Adapter
    static class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TaskViewHolder> {
        private final List<Task> tasks;
        private final OnTaskClickListener listener;

        interface OnTaskClickListener {
            void onTaskClick(int position);
        }

        TodoAdapter(List<Task> tasks, OnTaskClickListener listener) {
            this.tasks = tasks;
            this.listener = listener;
        }

        @NonNull
        @Override
        public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo_task, parent, false);
            return new TaskViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
            holder.bind(tasks.get(position));
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        static class TaskViewHolder extends RecyclerView.ViewHolder {
            private final TextView taskTitle;
            private final TextView taskTime;
            private final View priority;
            private final RadioButton taskCheckbox;
            private final OnTaskClickListener listener;

            public TaskViewHolder(View itemView, OnTaskClickListener listener) {
                super(itemView);
                this.listener = listener;
                taskTitle = itemView.findViewById(R.id.taskTitle);
                taskTime = itemView.findViewById(R.id.taskTime);
                priority = itemView.findViewById(R.id.statusIndicator);
                taskCheckbox = itemView.findViewById(R.id.taskRadio);

                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        this.listener.onTaskClick(position);
                    }
                });
            }

            void bind(Task task) {
                taskTitle.setText(task.getName());
                taskCheckbox.setChecked(task.isCompleted());
                taskTime.setText(task.getFormattedDate());

                switch (task.getPriority().toLowerCase()) {
                    case "high":
                        priority.setBackgroundColor(Color.RED);
                        break;
                    case "medium":
                        priority.setBackgroundColor(Color.YELLOW);
                        break;
                    default:
                        priority.setBackgroundColor(Color.GREEN);
                }
            }
        }
    }
}
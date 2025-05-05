package com.example.undec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends BaseActivity {

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

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        taskManager = new TaskManager(this);
        streakCalendarView = findViewById(R.id.streakCalendarView);
        RecyclerView todoRecyclerView = findViewById(R.id.todoRecyclerView);
        RecyclerView notesRecyclerView = findViewById(R.id.notesRecyclerView);
        
        // Setup bottom navigation
        setupBottomNavigation(R.id.nav_home);
        
        // Setup Events FAB
        findViewById(R.id.fabEvents).setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, EventsActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error launching EventsActivity", e);
                Toast.makeText(this, "Events feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        // Load today's tasks
        todayTasks = taskManager.getTodayTasks();
        if (todayTasks.isEmpty()) {
            String today = getCurrentDate();
            todayTasks.add(new Task("Complete assignment", false, "low", today));
            todayTasks.add(new Task("Study for exam", false, "high", today));
            todayTasks.add(new Task("Submit homework", false, "medium", today));
            for (Task task : todayTasks) {
                taskManager.addTask(task);
            }
        }

        // Setup tasks RecyclerView
        todoAdapter = new TodoAdapter(todayTasks, position -> {
            Task task = todayTasks.get(position);
            task.setCompleted(!task.isCompleted());
            taskManager.updateTask(position, task);
            todoAdapter.notifyItemChanged(position);
            checkAndMarkStreak();

            Toast.makeText(this, task.isCompleted() ?
                    "Task completed" : "Task incomplete", Toast.LENGTH_SHORT).show();
        });

        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoRecyclerView.setAdapter(todoAdapter);

        // Setup notes section
        setupNotesSection(notesRecyclerView);

        // Setup navigation
        setupSeeMoreButtons();
        setupTabLayout();

        // Initialize streak
        checkAndMarkStreak();
    }
    
    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_home;
    }

    @Override
    protected void onResume() {
        super.onResume();
        todayTasks = taskManager.getTodayTasks();
        if (todoAdapter != null) {
            todoAdapter.notifyDataSetChanged();
        }

        recentNotes = NotesRepository.getRecentNotes();
        if (noteAdapter != null) {
            noteAdapter.notifyDataSetChanged();
        }

        checkAndMarkStreak();
    }

    private void setupNotesSection(RecyclerView notesRecyclerView) {
        recentNotes = NotesRepository.getRecentNotes();
        if (recentNotes.isEmpty()) {
            recentNotes.add(new Note("Welcome Note", "General", getCurrentDate(),
                    android.R.drawable.ic_menu_edit, "Welcome to your notes app!"));
            recentNotes.add(new Note("Quick Tip", "Tips", getCurrentDate(),
                    android.R.drawable.ic_dialog_info, "Organize notes by categories."));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false);
        notesRecyclerView.setLayoutManager(layoutManager);

        noteAdapter = new NoteAdapter(recentNotes, note -> {
            Intent intent = new Intent(this, NotesActivity.class);
            intent.putExtra("note_title", note.getTitle());
            intent.putExtra("note_content", note.getContent());
            startActivity(intent);
        });

        notesRecyclerView.setAdapter(noteAdapter);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter task name");
        builder.setView(input);

        final String[] priorities = {"Low", "Medium", "High"};
        builder.setSingleChoiceItems(priorities, 1, null);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskName = input.getText().toString().trim();
            if (!taskName.isEmpty()) {
                int selectedPriority = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                String priority = "medium";
                if (selectedPriority == 0) priority = "low";
                else if (selectedPriority == 2) priority = "high";

                Task newTask = new Task(taskName, false, priority, getCurrentDate());
                taskManager.addTask(newTask);
                todayTasks.add(newTask);
                todoAdapter.notifyItemInserted(todayTasks.size() - 1);
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setupSeeMoreButtons() {
        // Tasks "See more"
        findViewById(R.id.seeMoreTasks).setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        // Notes "See more"
        findViewById(R.id.seeMoreNotes).setOnClickListener(v ->
                startActivity(new Intent(this, NotesActivity.class)));

        // Streak "See more"
        findViewById(R.id.seeMoreStreak).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("streakCount", streakCalendarView.getStreakCount());
            startActivity(intent);
        });
        
        // Events "See more"
        findViewById(R.id.seeMoreEvents).setOnClickListener(v -> {
            navigateToEventsActivity();
        });

        // Upcoming Task Card
        findViewById(R.id.upcomingTaskCard).setOnClickListener(v ->
                startActivity(new Intent(this, TasksActivity.class)));

        // Upcoming Event Card
        findViewById(R.id.upcomingEventCard).setOnClickListener(v -> {
            navigateToEventsActivity();
        });
        
        // Event Card Navigation
        findViewById(R.id.eventCardNavigation).setOnClickListener(v -> {
            navigateToEventsActivity();
        });
    }
    
    // Helper method to navigate to EventsActivity
    private void navigateToEventsActivity() {
        try {
            Intent intent = new Intent(this, EventsActivity.class);
            startActivity(intent);
            // Don't call finish() here to allow back navigation
        } catch (Exception e) {
            Log.e(TAG, "Error launching EventsActivity", e);
            Toast.makeText(this, "Events feature coming soon", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 3) { // Events tab
                    navigateToEventsActivity();
                    // Reset tab selection to avoid issues when returning
                    tabLayout.getTabAt(0).select();
                } else {
                    updateVisibility(position);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                navigateToTabActivity(tab.getPosition());
            }
        });
    }

    private void updateVisibility(int position) {
        boolean showTasks = position == 0 || position == 1;
        boolean showNotes = position == 0 || position == 2;
        boolean showEvents = position == 0 || position == 3;

        findViewById(R.id.todoRecyclerView).setVisibility(showTasks ? View.VISIBLE : View.GONE);
        findViewById(R.id.notesRecyclerView).setVisibility(showNotes ? View.VISIBLE : View.GONE);
        findViewById(R.id.upcomingEventCard).setVisibility(showEvents ? View.VISIBLE : View.GONE);
        findViewById(R.id.upcomingTaskCard).setVisibility(showTasks ? View.VISIBLE : View.GONE);

        // Show/hide section headers
        findViewById(R.id.taskSectionHeader).setVisibility(showTasks ? View.VISIBLE : View.GONE);
        findViewById(R.id.notesSectionHeader).setVisibility(showNotes ? View.VISIBLE : View.GONE);
        findViewById(R.id.streakSectionHeader).setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        findViewById(R.id.streakCalendarView).setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        
        // If Events tab is selected, navigate to EventsActivity
        if (position == 3) {
            navigateToEventsActivity();
        }
    }

    private void navigateToTabActivity(int position) {
        Class<?> targetActivity = null;
        switch (position) {
            case 1: targetActivity = TasksActivity.class; break;
            case 2: targetActivity = NotesActivity.class; break;
            case 3: targetActivity = EventsActivity.class; break;
        }
        if (targetActivity != null) {
            Intent intent = new Intent(this, targetActivity);
            startActivity(intent);
            // Don't call finish() to allow back navigation
        }
    }

    private void checkAndMarkStreak() {
        String currentDate = getCurrentDate();
        boolean allTasksCompleted = areAllTasksDone();

        SharedPreferences sharedPreferences = getSharedPreferences("StreakPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (allTasksCompleted) {
            editor.putBoolean(currentDate, true);
        } else {
            editor.putBoolean(currentDate, false);
        }
        editor.apply();

        updateStreakDisplay(sharedPreferences);
    }

    private boolean areAllTasksDone() {
        if (todayTasks.isEmpty()) return false;

        String todayDate = getCurrentDate();
        boolean hasTodayTasks = false;

        for (Task task : todayTasks) {
            if (todayDate.equals(task.getDate())) {
                hasTodayTasks = true;
                if (!task.isCompleted()) {
                    return false;
                }
            }
        }
        return hasTodayTasks;
    }

    private void updateStreakDisplay(SharedPreferences sharedPreferences) {
        Map<String, ?> allStreaks = sharedPreferences.getAll();
        List<Integer> completedDays = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        for (Map.Entry<String, ?> entry : allStreaks.entrySet()) {
            try {
                if (entry.getValue() instanceof Boolean && (Boolean) entry.getValue()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf.parse(entry.getKey());

                    calendar.setTime(date);
                    if (calendar.get(Calendar.MONTH) == currentMonth &&
                            calendar.get(Calendar.YEAR) == currentYear) {
                        completedDays.add(calendar.get(Calendar.DAY_OF_MONTH));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing date", e);
            }
        }

        if (streakCalendarView != null) {
            streakCalendarView.setCompletedDays(completedDays);
            streakCalendarView.setStreakCount(calculateCurrentStreak(sharedPreferences));

            int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            String currentDate = getCurrentDate();
            boolean isTodayComplete = sharedPreferences.getBoolean(currentDate, false);

            if (isTodayComplete) {
                streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.COMPLETED);
            } else if (!todayTasks.isEmpty()) {
                streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.IN_PROGRESS);
            } else {
                streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.NOT_STARTED);
            }
        }
    }

    private int calculateCurrentStreak(SharedPreferences sharedPreferences) {
        int streak = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        while (streak < 366) { // Limit to a year
            String dateStr = sdf.format(calendar.getTime());
            if (sharedPreferences.getBoolean(dateStr, false)) {
                streak++;
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                break;
            }
        }
        return streak;
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
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
            private final MaterialCheckBox taskCheckbox;

            public TaskViewHolder(View itemView, OnTaskClickListener listener) {
                super(itemView);
                taskTitle = itemView.findViewById(R.id.taskTitle);
                taskTime = itemView.findViewById(R.id.taskTime);
                priority = itemView.findViewById(R.id.priorityIndicator);
                taskCheckbox = itemView.findViewById(R.id.taskCheckbox);

                itemView.setOnClickListener(v -> listener.onTaskClick(getAdapterPosition()));
                taskCheckbox.setOnClickListener(v -> listener.onTaskClick(getAdapterPosition()));
            }

            void bind(Task task) {
                taskTitle.setText(task.getName());
                taskCheckbox.setChecked(task.isCompleted());
                taskTime.setText(task.getFormattedDate());

                int priorityColor;
                switch (task.getPriority().toLowerCase()) {
                    case "high": priorityColor = Color.RED; break;
                    case "medium": priorityColor = Color.YELLOW; break;
                    default: priorityColor = Color.GREEN;
                }
                priority.setBackgroundColor(priorityColor);
            }
        }
    }

    // Note Adapter
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
            return new NoteViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
            holder.bind(notes.get(position));
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        static class NoteViewHolder extends RecyclerView.ViewHolder {
            private final TextView titleTextView;
            private final TextView contentTextView;
            private final OnNoteClickListener listener;

            NoteViewHolder(@NonNull View itemView, OnNoteClickListener listener) {
                super(itemView);
                this.listener = listener;
                titleTextView = itemView.findViewById(R.id.noteTitle);
                contentTextView = itemView.findViewById(R.id.noteContent);
            }

            void bind(Note note) {
                titleTextView.setText(note.getTitle());
                contentTextView.setText(note.getContent());
                itemView.setOnClickListener(v -> listener.onNoteClick(note));
            }
        }
    }
}
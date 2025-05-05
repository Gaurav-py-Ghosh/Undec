package com.example.undec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private StreakCalendarView streakCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_profile);
            Log.d(TAG, "ProfileActivity onCreate started");

            // Initialize views
            streakCalendarView = findViewById(R.id.streakCalendarView);
            RecyclerView taskRecyclerView = findViewById(R.id.taskRecyclerView);

            setupBottomNavigation(R.id.nav_profile);

            int streakCount = getIntent().getIntExtra("streakCount", 0);

            if (streakCalendarView != null) {
                initializeStreakCalendar(streakCount);
            } else {
                Log.e(TAG, "Streak calendar view not found");
            }

            if (taskRecyclerView != null) {
                taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                List<TaskItem> taskList = new ArrayList<>();
                taskList.add(new TaskItem("Create Design System", 70));
                taskList.add(new TaskItem("Medical Website Design", 55));
                taskList.add(new TaskItem("Agency App Design", 87));


                TaskAdapter adapter = new TaskAdapter(this, taskList);
                taskRecyclerView.setAdapter(adapter);
            } else {
                Log.e(TAG, "Task RecyclerView not found");
            }

            Log.d(TAG, "ProfileActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in ProfileActivity onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_profile;
    }

    private void initializeStreakCalendar(int streakCount) {
        streakCalendarView.setStreakCount(streakCount);
        SharedPreferences prefs = getSharedPreferences("StreakPreferences", MODE_PRIVATE);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (prefs.getBoolean(getCurrentDate(), false)) {
            streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.COMPLETED);
        } else {
            streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.IN_PROGRESS);
        }

        Log.d(TAG, "Streak calendar initialized with count: " + streakCount);
    }

    private String getCurrentDate() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }
}

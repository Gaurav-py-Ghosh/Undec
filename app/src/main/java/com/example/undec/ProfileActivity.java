package com.example.undec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileActivity extends BaseActivity {
    private static final String TAG = "ProfileActivity";
    private StreakCalendarView streakCalendarView;
    private TextView tvName, tvEmail, tvPhone, tvBio, tvCharCount;
    private Button btnUpdate;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.d(TAG, "ProfileActivity onCreate started");

            // Initialize bottom navigation first to avoid crashes
            setupBottomNavigation(R.id.nav_profile);

            // Initialize profile views
            initializeProfileViews();

            // Initialize streak calendar
            initializeStreakCalendarViews();

            // Initialize tasks RecyclerView
            initializeTaskRecyclerView();

            Log.d(TAG, "ProfileActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in ProfileActivity onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initializeProfileViews() {
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvBio = findViewById(R.id.tvBio);
        tvCharCount = findViewById(R.id.tvCharCount);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Set sample data (replace with your actual data)
        tvName.setText("gaurav ghosh");
        tvEmail.setText("gaurav@baims.com");
        tvPhone.setText("+966 9861 22 12");
        tvBio.setText("This is a bio of gaurav, the best Physics professor of all time.");
        tvCharCount.setText(tvBio.getText().length() + " / 150 characters");

        // Set click listener for update button
        btnUpdate.setOnClickListener(v -> {
            // Handle update button click
            Toast.makeText(this, "Update profile clicked", Toast.LENGTH_SHORT).show();
            // You can start an EditProfileActivity here or show a dialog
        });
    }

    private void initializeStreakCalendarViews() {
        streakCalendarView = findViewById(R.id.streakCalendarView);
        int streakCount = getIntent().getIntExtra("streakCount", 0);

        if (streakCalendarView != null) {
            streakCalendarView.setStreakCount(streakCount);
            SharedPreferences prefs = getSharedPreferences("StreakPreferences", MODE_PRIVATE);
            int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            if (prefs.getBoolean(getCurrentDate(), false)) {
                streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.COMPLETED);
            } else {
                streakCalendarView.setDayStatus(today, StreakCalendarView.DayStatus.IN_PROGRESS);
            }

            Log.d(TAG, "Streak calendar initialized with count: " + streakCount);
        } else {
            Log.e(TAG, "Streak calendar view not found");
        }
    }

    private void initializeTaskRecyclerView() {
        RecyclerView taskRecyclerView = findViewById(R.id.taskRecyclerView);
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
    }

    private String getCurrentDate() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_profile;
    }
}
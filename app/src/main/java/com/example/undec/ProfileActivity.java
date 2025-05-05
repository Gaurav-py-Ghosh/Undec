package com.example.undec;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private BottomNavView bottomNavView;
    private StreakCalendarView streakCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_profile);
            Log.d(TAG, "ProfileActivity onCreate started");

            // Initialize views
            bottomNavView = findViewById(R.id.customBottomNav);
            streakCalendarView = findViewById(R.id.streakCalendarView);

            // Make sure bottom nav is found
            if (bottomNavView == null) {
                Log.e(TAG, "Bottom navigation view not found");
                Toast.makeText(this, "Navigation not available", Toast.LENGTH_SHORT).show();
            } else {
                setupBottomNavigation();
            }

            // Get streak count from intent if passed
            int streakCount = getIntent().getIntExtra("streakCount", 0);
            
            // Set up streak calendar if it exists
            if (streakCalendarView != null) {
                initializeStreakCalendar(streakCount);
            } else {
                Log.e(TAG, "Streak calendar view not found");
            }
            
            Log.d(TAG, "ProfileActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in ProfileActivity onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error initializing profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupBottomNavigation() {
        // Set the profile tab as selected (index 3)
        bottomNavView.setSelectedIndex(3);
        
        // Set navigation listener
        bottomNavView.setOnNavItemSelected(index -> {
            if (index == bottomNavView.getSelectedIndex()) {
                return; // Already on this tab
            }

            Intent intent = null;
            switch (index) {
                case 0: // Home
                    intent = new Intent(this, HomeActivity.class);
                    break;
                case 1: // Tasks
                    intent = new Intent(this, TasksActivity.class);
                    break;
                case 2: // Notes
                    intent = new Intent(this, NotesActivity.class);
                    break;
                case 3: // Profile
                    // Already here
                    return;
                case 4: // Settings
                    intent = new Intent(this, SettingsActivity.class);
                    break;
            }

            if (intent != null) {
                try {
                    // Clear the back stack and start fresh
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception e) {
                    Log.e(TAG, "Error navigating: " + e.getMessage(), e);
                    Toast.makeText(this, "Navigation error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeStreakCalendar(int streakCount) {
        // Set streak count
        streakCalendarView.setStreakCount(streakCount);
        
        // Get the shared preferences with completed days
        SharedPreferences prefs = getSharedPreferences("StreakPreferences", MODE_PRIVATE);
        
        // Mark the current day appropriately
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
package com.example.undec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private BottomNavView bottomNavView;
    private static final String TAG = "SettingsActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Initialize bottom navigation
        bottomNavView = findViewById(R.id.customBottomNav);
        
        if (bottomNavView == null) {
            Log.e(TAG, "BottomNavView not found in layout");
            Toast.makeText(this, "Navigation not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        setupBottomNavigation();
    }
    
    private void setupBottomNavigation() {
        try {
            // Set the settings tab as selected (index 4)
            bottomNavView.setSelectedIndex(4);
            
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
                        intent = new Intent(this, ProfileActivity.class);
                        break;
                    case 4: // Settings
                        // Already here, do nothing
                        return;
                }

                if (intent != null) {
                    try {
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
            Toast.makeText(this, "Error setting up navigation", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onBackPressed() {
        // Go back to home when back is pressed
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}

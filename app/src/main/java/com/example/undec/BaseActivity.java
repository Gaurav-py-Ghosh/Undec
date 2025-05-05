package com.example.undec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    
    protected BottomNavigationView bottomNavigation;
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Automatically set content view if a valid resource is provided
        int layoutResourceId = getLayoutResourceId();
        if (layoutResourceId != 0) {
            setContentView(layoutResourceId);
        }
    }
    
    /**
     * Get the layout resource ID for this activity.
     * Override this method in child activities to automatically set content view.
     * @return layout resource ID
     */
    protected int getLayoutResourceId() {
        // Child activities should override this
        return 0;
    }

    protected void setupBottomNavigation(int currentItemId) {
        try {
            bottomNavigation = findViewById(R.id.bottomNavigation);
            if (bottomNavigation != null) {
                bottomNavigation.setOnNavigationItemSelectedListener(this);
                
                // Don't select the item if it's not a valid ID
                if (currentItemId == R.id.nav_home || 
                    currentItemId == R.id.nav_tasks || 
                    currentItemId == R.id.nav_notes || 
                    currentItemId == R.id.nav_profile) {
                    bottomNavigation.setSelectedItemId(currentItemId);
                }
                
                Log.d(TAG, "Bottom navigation setup successful");
            } else {
                Log.e(TAG, "Bottom navigation view not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            if (bottomNavigation == null) {
                Log.e(TAG, "Bottom navigation is null in onNavigationItemSelected");
                return false;
            }
            
            if (item.getItemId() == bottomNavigation.getSelectedItemId()) {
                // Already on this screen
                Log.d(TAG, "Already on this screen, ignoring navigation tap");
                return true;
            }

            Intent intent = null;
            int itemId = item.getItemId();
            String destination = "unknown";

            if (itemId == R.id.nav_home) {
                intent = new Intent(this, HomeActivity.class);
                destination = "HomeActivity";
            } else if (itemId == R.id.nav_tasks) {
                intent = new Intent(this, TasksActivity.class);
                destination = "TasksActivity";
            } else if (itemId == R.id.nav_notes) {
                intent = new Intent(this, NotesActivity.class);
                destination = "NotesActivity";
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(this, ProfileActivity.class);
                destination = "ProfileActivity";
            }

            if (intent != null) {
                Log.d(TAG, "Navigating to " + destination);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else {
                Log.e(TAG, "Unknown navigation item selected: " + itemId);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Navigation error: " + e.getMessage(), e);
            Toast.makeText(this, "Navigation failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (bottomNavigation != null) {
                updateNavigationSelection();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
        }
    }

    protected abstract int getNavigationMenuItemId();

    private void updateNavigationSelection() {
        try {
            int itemId = getNavigationMenuItemId();
            if (bottomNavigation != null && bottomNavigation.getSelectedItemId() != itemId) {
                bottomNavigation.setSelectedItemId(itemId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating navigation selection: " + e.getMessage(), e);
        }
    }

    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
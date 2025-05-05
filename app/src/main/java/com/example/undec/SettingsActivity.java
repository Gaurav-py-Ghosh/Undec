package com.example.undec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {
    private static final String TAG = "SettingsActivity";
    
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "SettingsActivity onCreate started");
            
            // Initialize bottom navigation first to avoid crashes
            setupBottomNavigation(R.id.nav_home); // Settings doesn't have a dedicated nav item
            
            Log.d(TAG, "SettingsActivity setup complete");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SettingsActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading settings screen", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected int getNavigationMenuItemId() {
        // Settings doesn't have a dedicated navigation menu item, so return home
        return R.id.nav_home;
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

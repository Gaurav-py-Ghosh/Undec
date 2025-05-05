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
        // Child activities will set their own content view
    }

    protected void setupBottomNavigation(int currentItemId) {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        if (bottomNavigation != null) {
            bottomNavigation.setOnNavigationItemSelectedListener(this);
            bottomNavigation.setSelectedItemId(currentItemId);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == bottomNavigation.getSelectedItemId()) {
            return false;
        }

        Intent intent = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            intent = new Intent(this, HomeActivity.class);
        } else if (itemId == R.id.nav_tasks) {
            intent = new Intent(this, TasksActivity.class);
        } else if (itemId == R.id.nav_notes) {
            intent = new Intent(this, NotesActivity.class);
        } else if (itemId == R.id.nav_profile) {
            intent = new Intent(this, ProfileActivity.class);
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigation != null) {
            updateNavigationSelection();
        }
    }

    protected abstract int getNavigationMenuItemId();

    private void updateNavigationSelection() {
        int itemId = getNavigationMenuItemId();
        if (bottomNavigation.getSelectedItemId() != itemId) {
            bottomNavigation.setSelectedItemId(itemId);
        }
    }

    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
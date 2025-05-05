package com.example.undec;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private BottomNavView bottomNavView;
    private static final String TAG = "NotesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Initialize bottom navigation
        bottomNavView = findViewById(R.id.customBottomNav);

        if (bottomNavView == null) {
            Log.e(TAG, "BottomNavView not found in layout");
            Toast.makeText(this, "Navigation not available", Toast.LENGTH_SHORT).show();
            return;
        }

        setupBottomNavigation();
        setupNotesRecycler();
    }

    private void setupNotesRecycler() {
        RecyclerView recyclerView = findViewById(R.id.notesRecyclerView);

        List<Note> allNotes = NotesRepository.getAllNotes();

        NoteAdapter adapter = new NoteAdapter(allNotes, note -> {
            // Handle note click
            Toast.makeText(this, "Selected: " + note.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAddNote).setOnClickListener(v ->
                Toast.makeText(this, "Add Note Clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupBottomNavigation() {
        try {
            bottomNavView.setSelectedIndex(2); // Notes tab

            bottomNavView.setOnNavItemSelected(index -> {
                if (index == bottomNavView.getSelectedIndex()) return;

                Intent intent = null;
                switch (index) {
                    case 0:
                        intent = new Intent(this, HomeActivity.class);
                        break;
                    case 1:
                        intent = new Intent(this, TasksActivity.class);
                        break;
                    case 2:
                        return;
                    case 3:
                        intent = new Intent(this, ProfileActivity.class);
                        break;
                    case 4:
                        intent = new Intent(this, SettingsActivity.class);
                        break;
                }

                if (intent != null) {
                    try {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } catch (Exception e) {
                        Log.e(TAG, "Error navigating: " + e.getMessage(), e);
                        Toast.makeText(this, "Navigation error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            Log.d(TAG, "Bottom navigation set up successfully");
        } catch (Exception e) {
            Log.e(TAG, "Navigation setup error: " + e.getMessage(), e);
            Toast.makeText(this, "Navigation error", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onBackPressed() {
//
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//    }
}

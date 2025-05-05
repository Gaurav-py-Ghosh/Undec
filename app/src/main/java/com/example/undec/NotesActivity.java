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

public class NotesActivity extends BaseActivity {

    private static final String TAG = "NotesActivity";
    
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_notes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Initialize bottom navigation first to avoid crashes
            setupBottomNavigation(R.id.nav_notes);
            
            // Setup notes list
            setupNotesRecycler();
            
            Log.d(TAG, "NotesActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in NotesActivity: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading notes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_notes;
    }

    private void setupNotesRecycler() {
        try {
            RecyclerView recyclerView = findViewById(R.id.notesRecyclerView);
            if (recyclerView == null) {
                Log.e(TAG, "Notes RecyclerView not found");
                return;
            }

            List<Note> allNotes = NotesRepository.getAllNotes();

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            NoteAdapter adapter = new NoteAdapter(allNotes, note -> {
                // Handle note click
                Toast.makeText(this, "Selected: " + note.getTitle(), Toast.LENGTH_SHORT).show();
            });
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up notes recycler: " + e.getMessage(), e);
        }
    }
}

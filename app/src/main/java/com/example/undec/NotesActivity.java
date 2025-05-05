package com.example.undec;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesActivity extends BaseActivity {

    private static final String TAG = "NotesActivity";
    private FloatingActionButton fabAddNote;
    
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
            
            // Setup floating action button
            fabAddNote = findViewById(R.id.fabAddNote);
            if (fabAddNote != null) {
                fabAddNote.setOnClickListener(v -> showAddNoteDialog());
            } else {
                Log.e(TAG, "Add note FAB not found");
            }
            
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
    
    private void showAddNoteDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Create New Note");
            
            // Set up the input fields
            EditText titleInput = new EditText(this);
            titleInput.setInputType(InputType.TYPE_CLASS_TEXT);
            titleInput.setHint("Note Title");
            
            EditText contentInput = new EditText(this);
            contentInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            contentInput.setHint("Note Content");
            contentInput.setMinLines(3);
            
            // Create a container layout
            android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);
            layout.addView(titleInput);
            
            // Add some spacing
            android.widget.Space space = new android.widget.Space(this);
            space.setMinimumHeight(20);
            layout.addView(space);
            
            layout.addView(contentInput);
            builder.setView(layout);
            
            // Add buttons
            builder.setPositiveButton("Save", (dialog, which) -> {
                String title = titleInput.getText().toString().trim();
                String content = contentInput.getText().toString().trim();
                
                if (title.isEmpty()) {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Create and save a new note
                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date());
                
                Note newNote = new Note(
                        title, 
                        "General", // Default category
                        currentDate,
                        android.R.drawable.ic_menu_edit, // Default icon 
                        content);
                
                NotesRepository.addNote(newNote);
                setupNotesRecycler(); // Refresh the list
                
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            });
            
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            
            builder.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing add note dialog: " + e.getMessage(), e);
            Toast.makeText(this, "Error creating note", Toast.LENGTH_SHORT).show();
        }
    }
}

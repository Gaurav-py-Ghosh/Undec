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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        setupBottomNavigation(R.id.nav_notes);
        setupNotesRecycler();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.nav_notes;
    }

    private void setupNotesRecycler() {
        RecyclerView recyclerView = findViewById(R.id.notesRecyclerView);

        List<Note> allNotes = NotesRepository.getAllNotes();

        NoteAdapter adapter = new NoteAdapter(allNotes, note -> {
            // Handle note click
            Toast.makeText(this, "Selected: " + note.getTitle(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}

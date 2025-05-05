package com.example.undec;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class NotesRepository {
    private static List<Note> notes = null;

    public static List<Note> getAllNotes() {
        if (notes == null) {
            // Use ArrayList instead of Arrays.asList to allow modifications
            notes = new ArrayList<>(Arrays.asList(
                    new Note("How To Draw A Professional Wireframe?", "Design • Wireframe", "2020/05/09", R.drawable.img_4, "For Wireframe Design, You Need To Have A Pen And Paper With You..."),
                    new Note("Ways To Succeed Early", "Success • Goals", "2020/05/09", R.drawable.img3, "Some tips to help you succeed in life and career..."),
                    new Note("Scientific Facts Of Space", "Science • Space", "2020/06/08", R.drawable.img2, "Interesting facts about our solar system and beyond..."),
                    new Note("Algebra Basics", "Mathematics • Basics", new SimpleDateFormat("yyyy/MM/dd").format(new Date()), R.drawable.maths, "Key algebraic concepts explained simply..."),
                    new Note("Quantum Physics", "Physics • Quantum", new SimpleDateFormat("yyyy/MM/dd").format(new Date()), R.drawable.physics, "Introduction to quantum mechanics principles..."),
                    new Note("Organic Chemistry", "Chemistry • Organic", new SimpleDateFormat("yyyy/MM/dd").format(new Date()), R.drawable.chem, "Basics of carbon-based molecules and compounds..."),
                    new Note("Cell Biology", "Biology • Cells", new SimpleDateFormat("yyyy/MM/dd").format(new Date()), R.drawable.biology, "Understanding the building blocks of life...")
            ));
        }
        return notes;
    }

    public static List<Note> getRecentNotes() {
        List<Note> allNotes = getAllNotes();
        // Return first 4 notes for carousel
        return allNotes.subList(0, Math.min(4, allNotes.size()));
    }
    
    public static void addNote(Note note) {
        if (notes == null) {
            getAllNotes(); // Initialize if needed
        }
        
        // Add the new note at the beginning of the list to make it appear as the most recent
        notes.add(0, note);
    }
    
    public static void deleteNote(Note note) {
        if (notes != null) {
            notes.remove(note);
        }
    }
    
    public static void updateNote(Note oldNote, Note newNote) {
        if (notes != null) {
            int index = notes.indexOf(oldNote);
            if (index >= 0) {
                notes.set(index, newNote);
            }
        }
    }
}
package com.example.undec;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Date;

public class NotesRepository {
    private static List<Note> notes = null;

    public static List<Note> getAllNotes() {
        if (notes == null) {
            String currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());

            notes = Arrays.asList(
                    new Note("Algebra Basics", "Mathematics", currentDate, R.drawable.maths),
                    new Note("Quantum Physics", "Physics", currentDate, R.drawable.physics),
                    new Note("Organic Chemistry", "Chemistry", currentDate, R.drawable.chem),
                    new Note("Cell Biology", "Biology", currentDate, R.drawable.biology),
                    new Note("How to Draw a Wireframe", "Design", currentDate, R.drawable.img_4),
                    new Note("Scientific Facts of Space", "Science", currentDate, R.drawable.img2),
                    new Note("Ways To Succeed Early", "Success", currentDate, R.drawable.img3)
            );
        }
        return notes;
    }

    public static List<Note> getRecentNotes() {
        List<Note> allNotes = getAllNotes();
        // Return first 4 notes for carousel
        return allNotes.subList(0, Math.min(4, allNotes.size()));
    }
}
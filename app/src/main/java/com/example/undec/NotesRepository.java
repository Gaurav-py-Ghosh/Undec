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
                    new Note("Algebra Basics", "Mathematics", currentDate, R.drawable.maths, "Basic concepts of algebra including equations, functions, and graphs."),
                    new Note("Quantum Physics", "Physics", currentDate, R.drawable.physics, "Introduction to quantum mechanics and its fundamental principles."),
                    new Note("Organic Chemistry", "Chemistry", currentDate, R.drawable.chem, "Study of carbon compounds and their reactions."),
                    new Note("Cell Biology", "Biology", currentDate, R.drawable.biology, "Understanding cell structure, function, and processes."),
                    new Note("How to Draw a Wireframe", "Design", currentDate, R.drawable.img_4, "Step-by-step guide to creating effective wireframes."),
                    new Note("Scientific Facts of Space", "Science", currentDate, R.drawable.img2, "Fascinating facts about space, planets, and the universe."),
                    new Note("Ways To Succeed Early", "Success", currentDate, R.drawable.img3, "Tips and strategies for achieving success in your career.")
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
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
            notes = Arrays.asList(
                    new Note("How To Draw A Professional Wireframe?", "Design • Wireframe", "2020/05/09", R.drawable.img_4, "For Wireframe Design, You Need To Have A Pen And Paper With You..."),
                    new Note("Ways To Succeed Early", "Success • Goals", "2020/05/09", R.drawable.img3, ""),
                    new Note("Scientific Facts Of Space", "Science • Space", "2020/06/08", R.drawable.img2, ""),
                    new Note("Algebra Basics", "Mathematics • Basics", new SimpleDateFormat("yyyy/MM/dd").format(new Date()), R.drawable.maths, ""),
                    new Note("Quantum Physics", "Physics • Quantum", new SimpleDateFormat("yyyy/MM/dd").format(new Date()), R.drawable.physics, ""),
                    new Note("Organic Chemistry", "Chemistry • Organic", new SimpleDateFormat("yyyy/MM/dd").format(new Date()), R.drawable.chem, ""),
                    new Note("Cell Biology", "Biology • Cells", new SimpleDateFormat("yyyy/MM/dd").format(new Date()), R.drawable.biology, "")
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
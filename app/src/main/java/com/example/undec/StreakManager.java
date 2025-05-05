package com.example.undec;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class StreakManager {
    private static final String STREAK_PREFS = "StreakPrefs";
    private static final String COMPLETED_DATES_KEY = "completed_dates";
    private final SharedPreferences sharedPreferences;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public StreakManager(Context context) {
        sharedPreferences = context.getSharedPreferences(STREAK_PREFS, Context.MODE_PRIVATE);
    }

    // First define the save method
    private void saveCompletedDates(Set<String> dates) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(COMPLETED_DATES_KEY, new HashSet<>(dates));
        editor.apply();
    }

    // Then define methods that use it
    public void markDateComplete(String date) {
        Set<String> completedDates = getCompletedDates();
        completedDates.add(date);
        saveCompletedDates(completedDates);
    }

    public void markDateIncomplete(String date) {
        Set<String> completedDates = getCompletedDates();
        completedDates.remove(date);
        saveCompletedDates(completedDates);
    }

    public Set<String> getCompletedDates() {
        // Create a new HashSet to avoid issues with SharedPreferences StringSet
        return new HashSet<>(sharedPreferences.getStringSet(COMPLETED_DATES_KEY, new HashSet<>()));
    }

    public List<Integer> getCurrentMonthCompletedDays() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        List<Integer> completedDays = new ArrayList<>();
        Set<String> allCompletedDates = getCompletedDates();

        for (String dateStr : allCompletedDates) {
            try {
                calendar.setTime(dateFormat.parse(dateStr));
                if (calendar.get(Calendar.MONTH) == currentMonth &&
                        calendar.get(Calendar.YEAR) == currentYear) {
                    completedDays.add(calendar.get(Calendar.DAY_OF_MONTH));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return completedDays;
    }

    public int getCurrentStreak() {
        Calendar calendar = Calendar.getInstance();
        Set<String> completedDates = getCompletedDates();
        int streak = 0;

        // Check consecutive days starting from today
        while (true) {
            String dateStr = dateFormat.format(calendar.getTime());
            if (!completedDates.contains(dateStr)) {
                break;
            }
            streak++;
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        return streak;
    }
}
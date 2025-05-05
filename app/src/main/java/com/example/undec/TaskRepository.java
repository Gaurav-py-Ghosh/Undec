package com.example.undec;

import android.content.Context;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskRepository {

    // Get today's date in yyyy-MM-dd format
    private static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
    
    // Get a specific date by adding/subtracting days from today
    private static String getDateOffset(int dayOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, dayOffset);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    // Example method to fetch today's tasks
    public static List<Task> getTodayTasks(Context context) {
        List<Task> tasks = new ArrayList<>();
        String today = getTodayDate();
        String yesterday = getDateOffset(-1);
        String tomorrow = getDateOffset(1);

        // Today's tasks with today's date
        tasks.add(new Task("Complete assignment", false, "low", today));
        tasks.add(new Task("Prepare for lab", true, "medium", today));
        tasks.add(new Task("Revise notes", false, "high", today));
        
        // Add a few tasks for other dates to demonstrate filtering
        tasks.add(new Task("Submit research paper", false, "high", yesterday));
        tasks.add(new Task("Attend project meeting", false, "medium", tomorrow));

        return tasks;
    }
    
    // Example method to fetch all tasks (including upcoming ones)
    public static List<Task> getAllTasks(Context context) {
        List<Task> tasks = new ArrayList<>();
        String today = getTodayDate();
        
        // Today's tasks
        tasks.add(new Task("Complete assignment", false, "low", today));
        tasks.add(new Task("Prepare for lab", true, "medium", today));
        tasks.add(new Task("Revise notes", false, "high", today));
        
        // Tomorrow's tasks
        String tomorrow = getDateOffset(1);
        tasks.add(new Task("Submit project", false, "high", tomorrow));
        tasks.add(new Task("Study for quiz", false, "medium", tomorrow));
        
        // Day after tomorrow
        String dayAfter = getDateOffset(2);
        tasks.add(new Task("Group meeting", false, "medium", dayAfter));
        
        return tasks;
    }
    
    // Get tasks for a specific date
    public static List<Task> getTasksForDate(Context context, String date) {
        List<Task> allTasks = getAllTasks(context);
        List<Task> tasksForDate = new ArrayList<>();
        
        for (Task task : allTasks) {
            if (task.getDate().equals(date)) {
                tasksForDate.add(task);
            }
        }
        
        return tasksForDate;
    }
}

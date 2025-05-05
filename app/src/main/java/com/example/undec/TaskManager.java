package com.example.undec;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskManager {
    private static final String PREFS_NAME = "TaskPreferences";
    private static final String TASKS_KEY = "tasks";
    private SharedPreferences sharedPreferences;

    public TaskManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        String jsonString = sharedPreferences.getString(TASKS_KEY, null);

        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Task task = Task.fromJson(jsonArray.getString(i));
                    if (task != null) {
                        tasks.add(task);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }

    public List<Task> loadTasks() {
        return getAllTasks();
    }

    public void saveTasks(List<Task> tasks) {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            jsonArray.put(task.toJson());
        }
        sharedPreferences.edit().putString(TASKS_KEY, jsonArray.toString()).apply();
    }

    public void addTask(Task newTask) {
        List<Task> tasks = getAllTasks();
        tasks.add(newTask);
        saveTasks(tasks);
    }

    public void updateTask(int position, Task updatedTask) {
        List<Task> tasks = loadTasks();
        
        // Find the task by name and date rather than position
        // This ensures we update the correct task even if the list order changes
        boolean found = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getName().equals(updatedTask.getName()) && 
                task.getDate().equals(updatedTask.getDate())) {
                tasks.set(i, updatedTask);
                found = true;
                break;
            }
        }
        
        // If we didn't find it, add it to the list or update by position as fallback
        if (!found) {
            if (position >= 0 && position < tasks.size()) {
                tasks.set(position, updatedTask);
            } else {
                // Add as new task if we can't update
                tasks.add(updatedTask);
            }
        }
        
        saveTasks(tasks);
    }

    public void deleteTask(int position) {
        List<Task> tasks = getAllTasks();
        if (position >= 0 && position < tasks.size()) {
            tasks.remove(position);
            saveTasks(tasks);
        }
    }

    public List<Task> getTasksForDate(String date) {
        List<Task> allTasks = getAllTasks();
        List<Task> dateTasks = new ArrayList<>();

        for (Task task : allTasks) {
            if (date.equals(task.getDate())) {
                dateTasks.add(task);
            }
        }
        return dateTasks;
    }

    public boolean areAllTasksCompleteForDate(String date) {
        List<Task> dateTasks = getTasksForDate(date);
        if (dateTasks.isEmpty()) {
            return false;
        }

        for (Task task : dateTasks) {
            if (!task.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    public List<Task> getTodayTasks() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return getTasksForDate(today);
    }
}
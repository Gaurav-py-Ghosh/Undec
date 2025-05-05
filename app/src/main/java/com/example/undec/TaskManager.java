package com.example.undec;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private static final String PREFS_NAME = "TaskPreferences";
    private static final String TASKS_KEY = "tasks";
    private SharedPreferences sharedPreferences;

    public TaskManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveTasks(List<Task> tasks) {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            jsonArray.put(task.toJson());
        }
        sharedPreferences.edit().putString(TASKS_KEY, jsonArray.toString()).apply();
    }

    public List<Task> loadTasks() {
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

    public void addTask(Task newTask) {
        List<Task> tasks = loadTasks();
        tasks.add(newTask);
        saveTasks(tasks);
    }

    public void updateTask(int position, Task updatedTask) {
        List<Task> tasks = loadTasks();
        if (position >= 0 && position < tasks.size()) {
            tasks.set(position, updatedTask);
            saveTasks(tasks);
        }
    }

    public void deleteTask(int position) {
        List<Task> tasks = loadTasks();
        if (position >= 0 && position < tasks.size()) {
            tasks.remove(position);
            saveTasks(tasks);
        }
    }

    public List<Task> getTodayTasks() {
        String today = new Task("", false, "").getCurrentDate();
        List<Task> allTasks = loadTasks();
        List<Task> todayTasks = new ArrayList<>();

        for (Task task : allTasks) {
            if (today.equals(task.getDate())) {
                todayTasks.add(task);
            }
        }
        return todayTasks;
    }
}
package com.example.undec;

import org.json.JSONException;
import org.json.JSONObject;

public class Task {
    private String name;
    private boolean completed;
    private String priority;
    private String date; // Date in format "yyyy-MM-dd"

    public Task(String name, boolean completed, String priority) {
        this.name = name;
        this.completed = completed;
        this.priority = priority;
        this.date = getCurrentDate();
    }

    public Task(String name, boolean completed, String priority, String date) {
        this.name = name;
        this.completed = completed;
        this.priority = priority;
        this.date = date;
    }

    // JSON Serialization
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("completed", completed);
            jsonObject.put("priority", priority);
            jsonObject.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static Task fromJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            return new Task(
                    jsonObject.getString("name"),
                    jsonObject.getBoolean("completed"),
                    jsonObject.getString("priority"),
                    jsonObject.getString("date")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Existing methods remain the same
    public String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getPriority() {
        return priority;
    }

    public String getDate() {
        return date;
    }

    public String getFormattedDate() {
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(this.date);
            return outputFormat.format(date);
        } catch (Exception e) {
            return this.date;
        }
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
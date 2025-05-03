package com.example.undec;

public class Task {
    String title;
    boolean isCompleted;
    public String priority;

    public Task(String title, boolean isCompleted, String priority) {
        this.title = title;
        this.isCompleted = isCompleted;
        this.priority = priority;
    }
}

package com.example.undec;

public class Note {
    private String title;
    private String category;
    private String date;
    private int imageResId;
    private String content;

    public Note(String title, String category, String date, int imageResId, String content) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.imageResId = imageResId;
        this.content = content;
    }

    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public int getImageResId() { return imageResId; }
    public String getContent() { return content; }
}
package com.example.undec;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    RecyclerView carouselRecyclerView, todoRecyclerView;
    CarouselAdapter carouselAdapter;
    TodoAdapter todoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        carouselRecyclerView = findViewById(R.id.carouselRecyclerView);
        todoRecyclerView = findViewById(R.id.todoRecyclerView);

        // Carousel setup
        List<NoteCard> notes = Arrays.asList(
                new NoteCard("Mathematics", R.drawable.maths),
                new NoteCard("Physics", R.drawable.biology),
                new NoteCard("Chemistry", R.drawable.physics)
        );
        carouselAdapter = new CarouselAdapter(notes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        carouselRecyclerView.setLayoutManager(layoutManager);
        carouselRecyclerView.setAdapter(carouselAdapter);

        // To-do list setup
        List<Task> tasks = Arrays.asList(
                new Task("Complete assignment", false,"low"),
                new Task("Prepare for lab", true,"medium"),
                new Task("Revise notes", false,"high")
        );
        todoAdapter = new TodoAdapter(tasks);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoRecyclerView.setAdapter(todoAdapter);
    }
}

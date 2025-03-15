package com.hypertron.hypernotes;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabTheme = findViewById(R.id.fabTheme);

        // Set up RecyclerView
        noteList = new ArrayList<>();
        adapter = new NoteAdapter(this, noteList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // Set up add button click listener
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add a new note
                addNewNote();
            }
        });

        // Set up theme toggle button click listener
        fabTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTheme();
            }
        });
    }

    private void addNewNote() {
        // Create a new note with default values
        Note newNote = new Note("New Note " + (noteList.size() + 1), 
                                "Tap to edit", 
                                System.currentTimeMillis());
        noteList.add(0, newNote);
        adapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);
        
        Toast.makeText(this, "New note created", Toast.LENGTH_SHORT).show();
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
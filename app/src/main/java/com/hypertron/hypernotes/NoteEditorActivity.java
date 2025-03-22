package com.hypertron.hypernotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteEditorActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editContent;
    private FloatingActionButton fabSave;
    
    private long noteId = -1;
    private boolean isNewNote = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.edit_note);
        }
        
        // Initialize views
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        fabSave = findViewById(R.id.fabSave);
        
        // Apply accent color
        applyAccentColors();
        
        // Get data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("note_id")) {
            noteId = intent.getLongExtra("note_id", -1);
            isNewNote = false;
            loadNote(noteId);
        }
        
        // Set up save button
        fabSave.setOnClickListener(view -> saveNote());
    }
    
    private void applyAccentColors() {
        // Get accent color from shared preferences
        int accentColor = AccentColorActivity.getAccentColor(this);
        
        // Apply to FAB
        if (fabSave != null) {
            fabSave.setBackgroundTintList(ColorStateList.valueOf(accentColor));
        }
    }
    
    private void loadNote(long id) {
        // In a real app, you would load note data from database
        // For demonstration, we'll just set some placeholder text
        
        // This should be replaced with actual database query in a real app
        editTitle.setText("Note Title");
        editContent.setText("Note content goes here...");
    }
    
    private void saveNote() {
        // Get title and content
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();
        
        // Validate inputs
        if (title.isEmpty()) {
            editTitle.setError(getString(R.string.error_empty_title));
            return;
        }
        
        // In a real app, you would save to database here
        // For demonstration, we'll just show a success message
        
        Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
        
        // Return to main activity
        finish();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            // Handle back button
            onBackPressed();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
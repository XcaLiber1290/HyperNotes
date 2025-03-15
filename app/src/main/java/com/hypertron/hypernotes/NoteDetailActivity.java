package com.hypertron.hypernotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Date;

public class NoteDetailActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etContent;
    private String noteTitle;
    private String noteContent;
    private long noteTimestamp;
    private int notePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);

        // Get note data from intent
        Intent intent = getIntent();
        noteTitle = intent.getStringExtra("note_title");
        noteContent = intent.getStringExtra("note_content");
        noteTimestamp = intent.getLongExtra("note_timestamp", System.currentTimeMillis());
        notePosition = intent.getIntExtra("note_position", -1);

        // Set note data to views
        etTitle.setText(noteTitle);
        etContent.setText(noteContent);

        // Set up save button
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            title = getString(R.string.untitled_note);
        }

        // Create intent to return data
        Intent resultIntent = new Intent();
        resultIntent.putExtra("note_title", title);
        resultIntent.putExtra("note_content", content);
        resultIntent.putExtra("note_timestamp", System.currentTimeMillis());
        resultIntent.putExtra("note_position", notePosition);

        setResult(RESULT_OK, resultIntent);
        finish();
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
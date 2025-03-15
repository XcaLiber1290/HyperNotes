package com.hypertron.hypernotes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

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
        adapter = new NoteAdapter(this, noteList, this);
        
        // Calculate number of columns based on screen width
        int spanCount = calculateSpanCount();
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Set up add button click listener
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNoteDialog();
            }
        });

        // Set up theme toggle button click listener
        fabTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTheme();
            }
        });
        
        // Add sample notes
        addSampleNotes();
    }
    
    private int calculateSpanCount() {
        // Get the screen width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        
        // Calculate how many notes can fit on screen (assuming each note is about 180dp wide)
        int noteWidth = getResources().getDimensionPixelSize(R.dimen.note_width);
        
        // Return at least 2 columns, more on wider screens
        return Math.max(2, screenWidth / noteWidth);
    }

    private void showAddNoteDialog() {
        // Create dialog for adding new note
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        builder.setView(dialogView);
        
        final EditText etTitle = dialogView.findViewById(R.id.etTitle);
        
        builder.setTitle(R.string.add_new_note);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = etTitle.getText().toString().trim();
                if (title.isEmpty()) {
                    title = getString(R.string.untitled_note);
                }
                addNewNote(title);
            }
        });
        
        builder.setNegativeButton(R.string.cancel, null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addNewNote(String title) {
        // Create a new note
        Note newNote = new Note(title, 
                               getString(R.string.tap_to_edit), 
                               System.currentTimeMillis());
        noteList.add(0, newNote);
        adapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);
        
        Toast.makeText(this, R.string.note_created, Toast.LENGTH_SHORT).show();
    }
    
    private void addSampleNotes() {
        // Add 6 sample notes as shown in your reference image
        String[] titles = {"notebook 6", "notebook 5", "notebook 4", 
                          "notebook 3", "notebook 2", "notebook 1"};
        
        for (String title : titles) {
            Note note = new Note(title, getString(R.string.tap_to_edit), System.currentTimeMillis());
            noteList.add(note);
        }
        
        adapter.notifyDataSetChanged();
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        Toast.makeText(this, isDarkMode ? R.string.dark_mode : R.string.light_mode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoteClick(int position) {
        // Open note for editing
        Note note = noteList.get(position);
        // Start NoteDetailActivity here with the selected note
        Toast.makeText(this, getString(R.string.opening) + " " + note.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoteLongClick(int position) {
        // Show options menu for the note (delete, share, etc.)
        showNoteOptionsDialog(position);
    }
    
    private void showNoteOptionsDialog(final int position) {
        String[] options = {getString(R.string.edit), getString(R.string.delete)};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.note_options);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Edit option
                    onNoteClick(position);
                } else if (which == 1) {
                    // Delete option
                    deleteNote(position);
                }
            }
        });
        
        builder.show();
    }
    
    private void deleteNote(int position) {
        // Confirm before deleting
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_delete);
        builder.setMessage(R.string.delete_message);
        
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noteList.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(MainActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }
}
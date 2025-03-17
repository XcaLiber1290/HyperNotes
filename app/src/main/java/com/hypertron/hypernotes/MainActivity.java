package com.hypertron.hypernotes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import java.util.Random;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.drawable.ColorDrawable;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private boolean isDarkMode = false;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabTheme = findViewById(R.id.fabTheme);
        ImageButton menuButton = findViewById(R.id.menu_button);

        // Set up menu button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

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
        
    }

    
    private int calculateSpanCount() {
        // Get the screen width
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        
        // Calculate how many notes can fit on screen (assuming each note is about 180dp wide)
        int noteWidth = getResources().getDimensionPixelSize(R.dimen.note_width);
        
        // Return at least 2 columns, more on wider screens
        return Math.max(2, screenWidth / noteWidth);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.main_menu);
        
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_settings) {
                    // Handle settings click
                    Toast.makeText(MainActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        
        popupMenu.show();
    }

    private void showAddNoteDialog() {
        // Create dialog for adding new note with custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        builder.setView(dialogView);
        
        final EditText etTitle = dialogView.findViewById(R.id.etTitle);
        final EditText etContent = dialogView.findViewById(R.id.etContent);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        
        AlertDialog dialog = builder.create();
        
        // Set window properties for rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        
        dialog.show();
        
        // Set button click listeners
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString().trim();
                String content = etContent.getText().toString().trim();
                
                if (title.isEmpty()) {
                    title = getString(R.string.untitled_note);
                }
                
                // Create new note with random color assignment
                addNewNote(title, content);
                dialog.dismiss();
            }
        });
    }

    private void addNewNote(String title, String content) {
        // Create a new note
        Note newNote = new Note(title, 
                               content.isEmpty() ? getString(R.string.tap_to_edit) : content, 
                               System.currentTimeMillis());
        
        noteList.add(0, newNote);
        adapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);
        
        Toast.makeText(this, R.string.note_created, Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Open settings
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
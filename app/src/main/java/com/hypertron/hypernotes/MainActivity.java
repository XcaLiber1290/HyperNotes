package com.hypertron.hypernotes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private static final String KEY_ACCENT_COLOR = "accent_color";
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private boolean isDarkMode = false;

    private int getThemeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("HyperNotesPrefs", MODE_PRIVATE);
        if (!prefs.contains("accent_color")) {
            startActivity(new Intent(this, AccentColorActivity.class));
            finish();
            return;
        }

        // Apply the saved accent color
        int accentColor = prefs.getInt(KEY_ACCENT_COLOR, ContextCompat.getColor(this, R.color.colorAccent));
        applyAccentColor(accentColor);
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
                Dialogs.showAddNoteDialog(MainActivity.this, new Dialogs.AddNoteListener() {
                    @Override
                    public void onNoteAdded(String title, String content, int colorIndex, int symbolIndex, String customEmoji) {
                        addNewNote(title, content, colorIndex, symbolIndex, customEmoji);
                    }
                });
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

    private void applyAccentColor(int color) {
        // Set system bar colors dynamically
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color); // Change status bar color
        window.setNavigationBarColor(color); // Change navigation bar color
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

    private void addNewNote(String title, String content, int colorIndex, int symbolIndex, String customEmoji) {
        try {
            // Create a new note with the provided parameters
            Note newNote = new Note(title, 
                            content.isEmpty() ? getString(R.string.tap_to_edit) : content, 
                            System.currentTimeMillis());
            
            // Store the selection in the note-specific maps
            if (colorIndex >= 0) {
                adapter.setNoteColor(newNote.getTimestamp(), colorIndex);
            }
        
            if (symbolIndex >= 0) {
                adapter.setNoteSymbol(newNote.getTimestamp(), symbolIndex);
            } else if (customEmoji != null) {
                adapter.setNoteCustomEmoji(newNote.getTimestamp(), customEmoji);
            }
            
            noteList.add(0, newNote);
            adapter.notifyItemInserted(0);
            recyclerView.smoothScrollToPosition(0);
            
            Toast.makeText(this, R.string.note_created, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // This will show exactly what's causing the crash
            Toast.makeText(this, "Error creating note: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
        Dialogs.showNoteOptionsDialog(this, position, new Dialogs.NoteOptionsListener() {
            @Override
            public void onNoteEdit(int position) {
                onNoteClick(position);
            }

            @Override
            public void onNoteDelete(int position) {
                deleteNote(position);
            }
        });
    }
    
    private void deleteNote(int position) {
        noteList.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(MainActivity.this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
    }
}
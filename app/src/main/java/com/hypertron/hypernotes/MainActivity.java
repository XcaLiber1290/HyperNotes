package com.hypertron.hypernotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "HyperNotesPrefs";
    private static final String KEY_FIRST_RUN = "first_run";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final int REQUEST_CREATE_NOTE = 1001;
    
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> notesList;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabTheme;
    
    // Activity result launcher for CreateNoteActivity
    private ActivityResultLauncher<Intent> createNoteLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check if this is the first run
        if (isFirstRun()) {
            // Redirect to AccentColorActivity for first-time setup
            startActivity(new Intent(this, AccentColorActivity.class));
            finish();
            return;
        }
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        fabTheme = findViewById(R.id.fabTheme);
        
        // Register activity result launcher
        registerActivityResultLaunchers();
        
        // Apply accent colors to UI elements
        applyAccentColors();
        
        // Set up recycler view
        setupRecyclerView();
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void registerActivityResultLaunchers() {
        createNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Process the returned note data
                    Intent data = result.getData();
                    String title = data.getStringExtra("title");
                    String content = data.getStringExtra("content");
                    int colorIndex = data.getIntExtra("colorIndex", -1);
                    int symbolIndex = data.getIntExtra("symbolIndex", -1);
                    String customEmoji = data.getStringExtra("customEmoji");
                    
                    // Get color resource based on colorIndex
                    int colorResId;
                    if (colorIndex == -1) {
                        // Random color
                        int[] colorResIds = getColorResourceIds();
                        colorResId = colorResIds[new Random().nextInt(colorResIds.length)];
                    } else {
                        int[] colorResIds = getColorResourceIds();
                        colorResId = colorResIds[colorIndex];
                    }
                    
                    // Create new note with generated ID
                    long newId = System.currentTimeMillis(); // Simple ID generation
                    Note newNote = new Note(newId, title, content, System.currentTimeMillis(), colorResId);
                    
                    // Additional properties can be stored in a real implementation
                    // Store symbolIndex or customEmoji if needed
                    
                    // Add to list and update adapter
                    notesList.add(0, newNote); // Add to beginning of the list
                    noteAdapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                    
                    // Show confirmation
                    Toast.makeText(MainActivity.this, "Note created", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private boolean isFirstRun() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true);
        
        if (isFirstRun) {
            // Set first run flag to false
            prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply();
        }
        
        return isFirstRun;
    }
    
    private void applyAccentColors() {
        // Get accent colors
        int accentColor = AccentColorActivity.getAccentColor(this);
        
        // Apply to Floating Action Buttons
        if (fabAdd != null) {
            fabAdd.setBackgroundTintList(ColorStateList.valueOf(accentColor));
        }
        
        if (fabTheme != null) {
            fabTheme.setBackgroundTintList(ColorStateList.valueOf(accentColor));
        }
    }
    
    private void setupRecyclerView() {
        // Initialize notes list
        notesList = new ArrayList<>();
        
        // Set up adapter
        noteAdapter = new NoteAdapter(this, notesList);
        
        // Determine number of columns based on orientation
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        
        // Set layout manager
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteAdapter);
    }
    
    private void setupClickListeners() {
        // FAB for adding new notes
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch CreateNoteActivity to create a new note
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                createNoteLauncher.launch(intent);
            }
        });
        
        // FAB for toggling theme
        fabTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleThemeMode();
            }
        });
    }
    
    private int[] getColorResourceIds() {
        return new int[] {
            R.color.note_blue,
            R.color.note_green,
            R.color.note_yellow,
            R.color.note_orange,
            R.color.note_red,
            R.color.note_purple,
            R.color.note_pink,
            R.color.note_teal
        };
    }
    
    private void toggleThemeMode() {
        // Get current night mode
        int currentNightMode = getResources().getConfiguration().uiMode & 
                                Configuration.UI_MODE_NIGHT_MASK;
        
        // Toggle mode
        int newMode;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            newMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else {
            newMode = AppCompatDelegate.MODE_NIGHT_YES;
        }
        
        // Save preference
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME_MODE, newMode).apply();
        
        // Apply new theme
        AppCompatDelegate.setDefaultNightMode(newMode);
        recreate();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            // Open settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_change_accent) {
            // Open accent color chooser
            Intent intent = new Intent(this, AccentColorActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search) {
            // Handle search
            // Implement search functionality
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh notes list when returning to activity
        // In a real app, you would reload from database
        if (noteAdapter != null) {
            noteAdapter.notifyDataSetChanged();
        }
        
        // Reapply accent colors in case they were changed
        applyAccentColors();
    }
    
    // Note class for demonstration (normally would be in a separate file)
    public static class Note {
        private long id;
        private String title;
        private String content;
        private long timestamp;
        private int colorResId;
        
        public Note(long id, String title, String content, long timestamp, int colorResId) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.timestamp = timestamp;
            this.colorResId = colorResId;
        }
        
        public long getId() { return id; }
        public String getTitle() { return title; }
        public String getContent() { return content; }
        public long getTimestamp() { return timestamp; }
        public int getColorResId() { return colorResId; }
    }
    
    // NoteAdapter class for demonstration (normally would be in a separate file)
    public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
        private android.content.Context context;
        private List<Note> notes;
        
        public NoteAdapter(android.content.Context context, List<Note> notes) {
            this.context = context;
            this.notes = notes;
        }
        
        @Override
        public NoteViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(context)
                .inflate(R.layout.item_note, parent, false);
            return new NoteViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(NoteViewHolder holder, int position) {
            Note note = notes.get(position);
            // Bind note data to ViewHolder
            // Implementation depends on your item_note.xml layout
        }
        
        @Override
        public int getItemCount() {
            return notes.size();
        }
        
        public class NoteViewHolder extends RecyclerView.ViewHolder {
            // Define views from item_note.xml
            
            public NoteViewHolder(android.view.View itemView) {
                super(itemView);
                // Initialize views
                // Set click listener for opening note
            }
        }
    }
}
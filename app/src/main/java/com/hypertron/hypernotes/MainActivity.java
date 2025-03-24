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
    
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> notesList;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabTheme;
    
    private ActivityResultLauncher<Intent> createNoteLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isFirstRun()) {
            startActivity(new Intent(this, AccentColorActivity.class));
            finish();
            return;
        }
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        
        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        fabTheme = findViewById(R.id.fabTheme);
        
        registerActivityResultLaunchers();
        applyAccentColors();
        setupRecyclerView();
        setupClickListeners();
    }
    
    private void registerActivityResultLaunchers() {
        createNoteLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    String title = data.getStringExtra("title");
                    String content = data.getStringExtra("content");
                    int colorIndex = data.getIntExtra("colorIndex", -1);
                    int symbolIndex = data.getIntExtra("symbolIndex", -1);
                    String customEmoji = data.getStringExtra("customEmoji");
                    
                    int colorResId;
                    if (colorIndex == -1) {
                        int[] colorResIds = getColorResourceIds();
                        colorResId = colorResIds[new Random().nextInt(colorResIds.length)];
                    } else {
                        int[] colorResIds = getColorResourceIds();
                        colorResId = colorResIds[colorIndex];
                    }
                    
                    long newId = System.currentTimeMillis();
                    Note newNote = new Note(newId, title, content, System.currentTimeMillis(), colorResId);
                    
                    // Set the symbol index and custom emoji
                    newNote.setSymbolIndex(symbolIndex);
                    if (customEmoji != null) {
                        newNote.setCustomEmoji(customEmoji);
                    }
                    
                    notesList.add(0, newNote);
                    noteAdapter.notifyItemInserted(0);
                    recyclerView.scrollToPosition(0);
                    
                    Toast.makeText(MainActivity.this, "Note created", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private boolean isFirstRun() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean(KEY_FIRST_RUN, true);
        if (isFirstRun) {
            prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply();
        }
        return isFirstRun;
    }
    
    private void applyAccentColors() {
        int accentColor = AccentColorActivity.getAccentColor(this);
        
        if (fabAdd != null) {
            fabAdd.setBackgroundTintList(ColorStateList.valueOf(accentColor));
        }
        
        if (fabTheme != null) {
            fabTheme.setBackgroundTintList(ColorStateList.valueOf(accentColor));
        }
    }
    
    private void setupRecyclerView() {
        notesList = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, notesList);
        
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
        
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noteAdapter);
    }
    
    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
            createNoteLauncher.launch(intent);
        });

        fabTheme.setOnClickListener(v -> toggleThemeMode());
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
        int currentNightMode = getResources().getConfiguration().uiMode & 
                                Configuration.UI_MODE_NIGHT_MASK;
        
        int newMode = (currentNightMode == Configuration.UI_MODE_NIGHT_YES) 
                      ? AppCompatDelegate.MODE_NIGHT_NO 
                      : AppCompatDelegate.MODE_NIGHT_YES;
        
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME_MODE, newMode).apply();
        
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_change_accent) {
            startActivity(new Intent(this, AccentColorActivity.class));
            return true;
        } else if (id == R.id.action_search) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (noteAdapter != null) {
            noteAdapter.notifyDataSetChanged();
        }
        applyAccentColors();
    }
}
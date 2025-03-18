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
import android.widget.TextView;
import android.text.InputFilter;
import android.text.InputType;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;
    private boolean isDarkMode = false;
    private Random random = new Random();
    private int selectedColorIndex = -1;
    private int selectedSymbolIndex = -1;
    private String customEmoji = null;
    private Button btnChooseColor;
    private Button btnChooseSymbol;

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
    Button btnChooseColor = dialogView.findViewById(R.id.btnChooseColor);
    Button btnChooseSymbol = dialogView.findViewById(R.id.btnChooseSymbol);
    TextView tvSelectedColor = dialogView.findViewById(R.id.tvSelectedColor);
    TextView tvSelectedSymbol = dialogView.findViewById(R.id.tvSelectedSymbol);
    
    // Reset selection
    selectedColorIndex = -1;
    selectedSymbolIndex = -1;
    customEmoji = null;
    
    AlertDialog dialog = builder.create();
    
    // Set window properties for rounded corners
    if (dialog.getWindow() != null) {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }
    
    // Set up color chooser
    btnChooseColor.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showColorChooserDialog(dialog, tvSelectedColor);
        }
    });
    
    // Set up symbol chooser
    btnChooseSymbol.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showSymbolChooserDialog(dialog, tvSelectedSymbol);
        }
    });
    
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
                
                // Create new note with selected or random color and symbol
                addNewNote(title, content, selectedColorIndex, selectedSymbolIndex, customEmoji);
                dialog.dismiss();
            }
        });
    }

    private void showColorChooserDialog(AlertDialog parentDialog, TextView tvSelectedColor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_color);
        
        // Get color names from resources
        String[] colorNames = getResources().getStringArray(R.array.color_names);
        
        builder.setSingleChoiceItems(colorNames, selectedColorIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedColorIndex = which;
                tvSelectedColor.setText(colorNames[which]);
                dialog.dismiss();
            }
        });
        
        // Add "Random" option
        builder.setNeutralButton(R.string.random_color, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedColorIndex = -1;
                tvSelectedColor.setText(R.string.random_color);
            }
        });
        
        builder.show();
    }

    private void showSymbolChooserDialog(AlertDialog parentDialog, TextView tvSelectedSymbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_symbol);
        
        // Get symbols from resources
        String[] symbols = getResources().getStringArray(R.array.note_symbols);
        
        // Create a grid layout for symbols
        View symbolsView = LayoutInflater.from(this).inflate(R.layout.dialog_symbols, null);
        RecyclerView recyclerView = symbolsView.findViewById(R.id.recyclerViewSymbols);
        
        // Set up the RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        
        // Create adapter for symbols
        SymbolAdapter symbolAdapter = new SymbolAdapter(symbols, new SymbolAdapter.OnSymbolClickListener() {
            @Override
            public void onSymbolClick(int position, String symbol) {
                selectedSymbolIndex = position;
                customEmoji = null;
                tvSelectedSymbol.setText(symbol);
                builder.create().dismiss();
            }
        });
        
        recyclerView.setAdapter(symbolAdapter);
        builder.setView(symbolsView);
        
        // Add "Custom Emoji" option
        builder.setPositiveButton(R.string.custom_emoji, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showCustomEmojiDialog(tvSelectedSymbol);
            }
        });
        
        // Add "Random" option
        builder.setNeutralButton(R.string.random_symbol, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedSymbolIndex = -1;
                customEmoji = null;
                tvSelectedSymbol.setText(R.string.random_symbol);
            }
        });
        
        builder.show();
    }

    private void showCustomEmojiDialog(TextView tvSelectedSymbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.custom_emoji);
        
        // Create EditText for custom emoji
        final EditText etEmoji = new EditText(this);
        etEmoji.setHint("Enter emoji 😊");
        etEmoji.setInputType(InputType.TYPE_CLASS_TEXT);
        etEmoji.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        builder.setView(etEmoji);
        
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customEmoji = etEmoji.getText().toString();
                if (!customEmoji.isEmpty()) {
                    selectedSymbolIndex = -2; // Custom emoji indicator
                    tvSelectedSymbol.setText(customEmoji);
                } else {
                    selectedSymbolIndex = -1;
                    customEmoji = null;
                    tvSelectedSymbol.setText(R.string.random_symbol);
                }
            }
        });
        
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        
        builder.show();
    }

    private void addNewNote(String title, String content, int colorIndex, int symbolIndex, String customEmoji) {
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
package com.hypertron.hypernotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editContent;
    private FloatingActionButton fabSave;
    private CardView previewCardView;
    private TextView tvPreviewIcon;
    private TextView tvPreviewTitle;
    
    private Button btnChooseColor;
    private Button btnChooseSymbol;
    private RecyclerView recyclerViewColors;
    private RecyclerView recyclerViewSymbols;
    
    private int selectedColorIndex = -1; // -1 means random
    private int selectedSymbolIndex = -1; // -1 means random
    private String customEmoji = null;
    
    private boolean showingColors = false;
    private boolean showingSymbols = false;
    
    private ColorAdapter colorAdapter;
    private SymbolAdapter symbolAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.create_note);
        }
        
        // Initialize views
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        fabSave = findViewById(R.id.fabSave);
        previewCardView = findViewById(R.id.previewCardView);
        tvPreviewIcon = findViewById(R.id.tvPreviewIcon);
        tvPreviewTitle = findViewById(R.id.tvPreviewTitle);
        btnChooseColor = findViewById(R.id.btnChooseColor);
        btnChooseSymbol = findViewById(R.id.btnChooseSymbol);
        recyclerViewColors = findViewById(R.id.recyclerViewColors);
        recyclerViewSymbols = findViewById(R.id.recyclerViewSymbols);
        
        // Apply accent color
        applyAccentColors();
        
        // Set up RecyclerViews
        setupColorRecyclerView();
        setupSymbolRecyclerView();
        
        // Set up click listeners
        setupClickListeners();
        
        // Initialize preview with random settings
        updatePreview();
    }
    
    private void applyAccentColors() {
        // Get accent color from shared preferences
        int accentColor = AccentColorActivity.getAccentColor(this);
        
        // Apply to FAB
        if (fabSave != null) {
            fabSave.setBackgroundTintList(ColorStateList.valueOf(accentColor));
        }
        
        // Apply to buttons
        if (btnChooseColor != null) {
            btnChooseColor.setTextColor(accentColor);
        }
        
        if (btnChooseSymbol != null) {
            btnChooseSymbol.setTextColor(accentColor);
        }
    }
    
    private void setupColorRecyclerView() {
        // Set up with a grid layout for colors
        int spanCount = 4; // Number of columns
        recyclerViewColors.setLayoutManager(new GridLayoutManager(this, spanCount));
        
        // Initially hide the RecyclerView
        recyclerViewColors.setVisibility(View.GONE);
        
        // Create color adapter
        colorAdapter = new ColorAdapter(this, color -> {
            selectedColorIndex = color;
            updatePreview();
            toggleColorSelector(); // Hide after selection
            
            // Update button text
            if (selectedColorIndex == -1) {
                btnChooseColor.setText(R.string.random_color);
            } else {
                String[] colorNames = getResources().getStringArray(R.array.color_names);
                btnChooseColor.setText(colorNames[selectedColorIndex]);
            }
        });
        
        recyclerViewColors.setAdapter(colorAdapter);
    }
    
    private void setupSymbolRecyclerView() {
        // Set up with a grid layout for symbols
        int spanCount = 4; // Number of columns
        recyclerViewSymbols.setLayoutManager(new GridLayoutManager(this, spanCount));
        
        // Initially hide the RecyclerView
        recyclerViewSymbols.setVisibility(View.GONE);
        
        // Create symbol adapter
        symbolAdapter = new SymbolAdapter(this, true, (position, symbol, isRandom) -> {
            selectedSymbolIndex = position;
            if (position == -2) {
                // Custom emoji
                showCustomEmojiInput();
            } else {
                customEmoji = isRandom ? null : symbol;
                updatePreview();
                toggleSymbolSelector(); // Hide after selection
                
                // Update button text
                if (selectedSymbolIndex == -1) {
                    btnChooseSymbol.setText(R.string.random_symbol);
                } else if (selectedSymbolIndex == -2 && customEmoji != null) {
                    btnChooseSymbol.setText(customEmoji);
                } else {
                    String[] symbols = getResources().getStringArray(R.array.note_symbols);
                    btnChooseSymbol.setText(symbols[selectedSymbolIndex]);
                }
            }
        });
        
        recyclerViewSymbols.setAdapter(symbolAdapter);
    }
    
    private void showCustomEmojiInput() {
        // Show a dialog for entering custom emoji
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.custom_emoji);
        
        // Create EditText for custom emoji
        final EditText etEmoji = new EditText(this);
        etEmoji.setHint(getString(R.string.enter_emoji_hint));
        etEmoji.setFilters(new android.text.InputFilter[] { 
            new android.text.InputFilter.LengthFilter(2) 
        });
        builder.setView(etEmoji);
        
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String emoji = etEmoji.getText().toString();
            if (!emoji.isEmpty()) {
                customEmoji = emoji;
                selectedSymbolIndex = -2; // Custom emoji
                
                // Update button text and preview
                btnChooseSymbol.setText(customEmoji);
                updatePreview();
            } else {
                // Fall back to random
                selectedSymbolIndex = -1;
                customEmoji = null;
                btnChooseSymbol.setText(R.string.random_symbol);
                updatePreview();
            }
        });
        
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.cancel();
        });
        
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    private void setupClickListeners() {
        // Color selection button
        btnChooseColor.setOnClickListener(v -> toggleColorSelector());
        
        // Symbol selection button
        btnChooseSymbol.setOnClickListener(v -> toggleSymbolSelector());
        
        // Save button
        fabSave.setOnClickListener(v -> saveNote());
    }
    
    private void toggleColorSelector() {
        showingColors = !showingColors;
        recyclerViewColors.setVisibility(showingColors ? View.VISIBLE : View.GONE);
        // Hide symbols when showing colors
        if (showingColors) {
            recyclerViewSymbols.setVisibility(View.GONE);
            showingSymbols = false;
        }
    }
    
    private void toggleSymbolSelector() {
        showingSymbols = !showingSymbols;
        recyclerViewSymbols.setVisibility(showingSymbols ? View.VISIBLE : View.GONE);
        // Hide colors when showing symbols
        if (showingSymbols) {
            recyclerViewColors.setVisibility(View.GONE);
            showingColors = false;
        }
    }
    
    private void updatePreview() {
        // Get title text
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            title = getString(R.string.untitled_note);
        }
        
        // Update title text
        tvPreviewTitle.setText(title);
        
        // Get color (random or selected)
        int colorResId;
        if (selectedColorIndex == -1) {
            // Random color
            int[] colorResIds = getColorResourceIds();
            colorResId = colorResIds[new Random().nextInt(colorResIds.length)];
        } else {
            int[] colorResIds = getColorResourceIds();
            colorResId = colorResIds[selectedColorIndex];
        }
        
        // Set card background color
        int color = getResources().getColor(colorResId);
        previewCardView.setCardBackgroundColor(color);
        
        // Get symbol (random or selected)
        String symbol;
        if (selectedSymbolIndex == -1) {
            // Random symbol
            String[] symbols = getResources().getStringArray(R.array.note_symbols);
            symbol = symbols[new Random().nextInt(symbols.length)];
        } else if (selectedSymbolIndex == -2 && customEmoji != null) {
            // Custom emoji
            symbol = customEmoji;
        } else {
            String[] symbols = getResources().getStringArray(R.array.note_symbols);
            symbol = symbols[selectedSymbolIndex];
        }
        
        // Set symbol text
        tvPreviewIcon.setText(symbol);
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
    
    private void saveNote() {
        // Get title and content
        String title = editTitle.getText().toString().trim();
        String content = editContent.getText().toString().trim();
        
        // Use default title if empty
        if (title.isEmpty()) {
            title = getString(R.string.untitled_note);
        }
        
        // Prepare data to return to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("content", content);
        resultIntent.putExtra("colorIndex", selectedColorIndex);
        resultIntent.putExtra("symbolIndex", selectedSymbolIndex);
        if (customEmoji != null) {
            resultIntent.putExtra("customEmoji", customEmoji);
        }
        
        // Set result and finish
        setResult(RESULT_OK, resultIntent);
        finish();
    }
    
    // Inner class for color selection
    private class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
        private final Context context;
        private final List<Integer> colorResIds;
        private final OnColorClickListener listener;
        
        public interface OnColorClickListener {
            void onColorClick(int colorIndex);
        }
        
        public ColorAdapter(Context context, OnColorClickListener listener) {
            this.context = context;
            this.listener = listener;
            
            // Add colors + random option
            this.colorResIds = new ArrayList<>();
            this.colorResIds.add(-1); // Random color option
            
            // Add all color resource IDs
            int[] colors = getColorResourceIds();
            for (int i = 0; i < colors.length; i++) {
                this.colorResIds.add(i);
            }
        }
        
        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(context)
                .inflate(R.layout.item_color, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int colorIndex = colorResIds.get(position);
            
            if (colorIndex == -1) {
                // Random color - show special indicator
                holder.colorView.setText("?");
                holder.colorView.setBackgroundResource(R.drawable.random_color_background);
            } else {
                // Regular color
                holder.colorView.setText("");
                int colorResId = getColorResourceIds()[colorIndex];
                holder.colorView.setBackgroundColor(context.getResources().getColor(colorResId));
            }
            
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onColorClick(colorIndex);
                }
            });
        }
        
        @Override
        public int getItemCount() {
            return colorResIds.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView colorView;
            
            ViewHolder(View itemView) {
                super(itemView);
                colorView = itemView.findViewById(R.id.colorView);
            }
        }
    }
}
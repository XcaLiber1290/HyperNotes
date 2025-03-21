package com.hypertron.hypernotes;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hypertron.hypernotes.SymbolAdapter;


import android.widget.ListView;
import android.widget.ArrayAdapter;

import android.graphics.drawable.ColorDrawable;

public class Dialogs {

    public interface ColorSelectionListener {
        void onColorSelected(int colorIndex);
    }
    
    public interface SymbolSelectionListener {
        void onSymbolSelected(int symbolIndex, String customEmoji);
    }
    
    public interface AddNoteListener {
        void onNoteAdded(String title, String content, int colorIndex, int symbolIndex, String customEmoji);
    }
    
    public interface NoteOptionsListener {
        void onNoteEdit(int position);
        void onNoteDelete(int position);
    }

    /**
     * Show dialog for selecting a color
     */
    public static void showColorChooserDialog(Context context, int currentColorIndex, ColorSelectionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.choose_color);
        
        // Get color names from resources
        String[] colorNames = context.getResources().getStringArray(R.array.color_names);
        
        // Create a new array with Random as the first option
        String[] optionsWithRandom = new String[colorNames.length + 1];
        optionsWithRandom[0] = context.getString(R.string.random_color);
        System.arraycopy(colorNames, 0, optionsWithRandom, 1, colorNames.length);
        
        // Calculate the currently selected index for the dialog
        int selectedIndex = (currentColorIndex == -1) ? 0 : currentColorIndex + 1;
        
        builder.setSingleChoiceItems(optionsWithRandom, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Adjust index to account for Random option (-1 means random)
                int colorIndex = (which == 0) ? -1 : which - 1;
                
                if (listener != null) {
                    listener.onColorSelected(colorIndex);
                }
                dialog.dismiss();
            }
        });
        
        AlertDialog dialog = builder.create();
        // Add this line to set rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        dialog.show();
    }

    /**
     * Show dialog for selecting a symbol
     */
    public static void showSymbolChooserDialog(Context context, int currentSymbolIndex, SymbolSelectionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        
        // Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_symbols, null);
        if (dialogView == null) {
            return; // Prevent crash if layout is missing
        }
        builder.setView(dialogView);

        // Get RecyclerView and configure it
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewSymbols);
        if (recyclerView == null) {
            return; // Prevent crash if RecyclerView is not found
        }
        
        // Set up with a grid layout
        int spanCount = 4; // Number of columns in the grid
        GridLayoutManager layoutManager = new GridLayoutManager(context, spanCount);
        recyclerView.setLayoutManager(layoutManager);

        // Get cancel button
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);

        if (cancelButton == null) {
            return; // Prevent crash if button is missing
        }

        // Create and set the adapter with special options
        AlertDialog dialog = builder.create();
        SymbolAdapter adapter = new SymbolAdapter(context, true, (position, symbol, isRandom) -> {
            if (position == -2) {
                // Custom emoji option selected
                showCustomEmojiDialog(context, listener);
                if (dialog != null) {
                    dialog.dismiss();
                }
            } else {
                if (listener != null) {
                    listener.onSymbolSelected(position, isRandom ? null : symbol);
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        recyclerView.setAdapter(adapter);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Show dialog for entering a custom emoji
     */
    public static void showCustomEmojiDialog(Context context, SymbolSelectionListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.custom_emoji);
        
        // Create EditText for custom emoji
        final EditText etEmoji = new EditText(context);
        etEmoji.setHint(context.getString(R.string.enter_emoji_hint));
        etEmoji.setInputType(InputType.TYPE_CLASS_TEXT);
        etEmoji.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        builder.setView(etEmoji);
        
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String customEmoji = etEmoji.getText().toString();
                if (!customEmoji.isEmpty()) {
                    if (listener != null) {
                        listener.onSymbolSelected(-2, customEmoji); // -2 indicates custom emoji
                    }
                } else {
                    if (listener != null) {
                        listener.onSymbolSelected(-1, null); // Fall back to random
                    }
                }
            }
        });
        
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        
        dialog.show();
    }
    
    /**
     * Show dialog for adding a new note
     */
    public static void showAddNoteDialog(Context context, AddNoteListener listener) {
        // Create dialog for adding new note with custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomDialogTheme);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_note, null);
        builder.setView(dialogView);
        
        final EditText etTitle = dialogView.findViewById(R.id.etTitle);
        final EditText etContent = dialogView.findViewById(R.id.etContent);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnChooseColor = dialogView.findViewById(R.id.btnChooseColor);
        Button btnChooseSymbol = dialogView.findViewById(R.id.btnChooseSymbol);
        
        // Initial values
        int selectedColorIndex = -1;
        int selectedSymbolIndex = -1;
        String[] customEmojiHolder = new String[1]; // Use array to hold reference
        customEmojiHolder[0] = null;
        
        AlertDialog dialog = builder.create();
        
        // Set window properties for rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        
        // Set up color chooser
        int[] finalSelectedColorIndex = {-1}; // Using array to enable modification inside listener

        btnChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorChooserDialog(context, finalSelectedColorIndex[0], new ColorSelectionListener() {
                    @Override
                    public void onColorSelected(int colorIndex) {
                        finalSelectedColorIndex[0] = colorIndex;
                        if (colorIndex == -1) {
                            btnChooseColor.setText(R.string.random_color);
                        } else {
                            String[] colorNames = context.getResources().getStringArray(R.array.color_names);
                            btnChooseColor.setText(colorNames[colorIndex]);
                        }
                    }
                });
            }
        });
        
        // Set up symbol chooser
        int[] finalSelectedSymbolIndex = {-1}; // Using array to enable modification inside listener

        btnChooseSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSymbolChooserDialog(context, finalSelectedSymbolIndex[0], new SymbolSelectionListener() {
                    @Override
                    public void onSymbolSelected(int symbolIndex, String customEmoji) {
                        finalSelectedSymbolIndex[0] = symbolIndex;
                        customEmojiHolder[0] = customEmoji;
                        
                        if (symbolIndex == -1) {
                            btnChooseSymbol.setText(R.string.random_symbol);
                        } else if (symbolIndex == -2 && customEmoji != null) {
                            btnChooseSymbol.setText(customEmoji);
                        } else {
                            String[] symbols = context.getResources().getStringArray(R.array.note_symbols);
                            btnChooseSymbol.setText(symbols[symbolIndex]);
                        }
                    }
                });
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
                    title = context.getString(R.string.untitled_note);
                }
                
                if (listener != null) {
                    listener.onNoteAdded(title, content, finalSelectedColorIndex[0], 
                            finalSelectedSymbolIndex[0], customEmojiHolder[0]);
                }
                dialog.dismiss();
            }
        });
    }
    
    /**
     * Show options dialog for a note
     */
    public static void showNoteOptionsDialog(Context context, int position, NoteOptionsListener listener) {
        String[] options = {context.getString(R.string.edit), context.getString(R.string.delete)};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.note_options);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Edit option
                    if (listener != null) {
                        listener.onNoteEdit(position);
                    }
                } else if (which == 1) {
                    // Delete option
                    showDeleteConfirmationDialog(context, position, listener);
                }
            }
        });
        
        AlertDialog dialog = builder.create();
        // Add this to ensure rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        dialog.show();
    }
    
    /**
     * Show confirmation dialog for deleting a note
     */
    public static void showDeleteConfirmationDialog(Context context, int position, NoteOptionsListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.confirm_delete);
        builder.setMessage(R.string.delete_message);
        
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onNoteDelete(position);
                }
            }
        });
        
        builder.setNegativeButton(R.string.cancel, null);
        
        AlertDialog dialog = builder.create();
        // Add this to ensure rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        }
        dialog.show();
    }
}
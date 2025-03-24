package com.hypertron.hypernotes;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.util.Random;
import java.text.SimpleDateFormat;
import android.content.Intent;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> notes;

    public NoteAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        
        // Set title
        holder.tvTitle.setText(note.getTitle());
        
        // Format and set time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(note.getTimestamp())));
        
        // Set symbol/emoji
        String symbol;
        if (note.getCustomEmoji() != null) {
            // Use custom emoji if available
            symbol = note.getCustomEmoji();
        } else {
            try {
                String[] symbols = context.getResources().getStringArray(R.array.note_symbols);
                int symbolIndex = note.getSymbolIndex();
                
                // If symbolIndex is -1 (random) or out of bounds, use a random symbol
                if (symbolIndex == -1 || symbolIndex >= symbols.length) {
                    symbolIndex = new Random().nextInt(symbols.length);
                }
                
                symbol = symbols[symbolIndex];
            } catch (Resources.NotFoundException e) {
                symbol = "📝"; // Fallback symbol
            }
        }
        
        holder.tvIcon.setText(symbol);
        
        // IMPORTANT: Remove any typeface setting that might prevent emoji display
        // holder.tvIcon.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        
        // Set card color
        int colorResId = note.getColorResId();
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getResources().getColor(colorResId, context.getTheme());
        } else {
            color = context.getResources().getColor(colorResId);
        }
        holder.cardView.setCardBackgroundColor(color);
    }
    
    // Helper method to determine if a color is dark
    private boolean isDarkColor(int color) {
        // Calculate the perceptive luminance (weighted RGB)
        double luminance = (0.299 * ((color >> 16) & 0xFF) + 
                            0.587 * ((color >> 8) & 0xFF) + 
                            0.114 * (color & 0xFF)) / 255;
        return luminance < 0.5;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        TextView tvTime;
        TextView tvIcon;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvIcon = itemView.findViewById(R.id.tvIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);

            // Click listener for opening the note
            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Note note = notes.get(position);
                    Intent intent = new Intent(context, NoteEditorActivity.class);
                    intent.putExtra("noteId", note.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
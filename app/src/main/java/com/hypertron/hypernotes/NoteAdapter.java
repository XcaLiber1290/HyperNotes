package com.hypertron.hypernotes;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
        if (note.getSymbolIndex() == -2 && note.getCustomEmoji() != null) {
            symbol = note.getCustomEmoji();
        } else {
            String[] symbols = context.getResources().getStringArray(R.array.note_symbols);
            int symbolIndex = note.getSymbolIndex() == -1 ? 
                    new Random().nextInt(symbols.length) : note.getSymbolIndex();
            symbol = symbols[Math.min(symbolIndex, symbols.length - 1)];
        }
        holder.tvIcon.setText(symbol);
        
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

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        TextView tvTime;
        TextView tvIcon;
        
        public NoteViewHolder(android.view.View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvIcon = itemView.findViewById(R.id.tvIcon);
        }
    }
}
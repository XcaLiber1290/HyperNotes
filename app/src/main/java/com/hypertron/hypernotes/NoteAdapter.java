package com.hypertron.hypernotes;

import androidx.core.content.ContextCompat;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // ✅ Use TextView instead of ImageView
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> noteList;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private OnNoteClickListener listener;
    private Random random = new Random();
    private Map<Long, Integer> noteColorMap = new HashMap<>();
    private Map<Long, Integer> noteIconMap = new HashMap<>();
    
    // Load symbols from resources
    private String[] icons;

    // Color scheme based on the provided design
    private int[] colors = {
        R.color.note_pink,
        R.color.note_purple,
        R.color.note_blue,
        R.color.note_peach,
        R.color.note_yellow,
        R.color.note_green,
        R.color.note_lavender,
        R.color.note_teal
    };

    public interface OnNoteClickListener {
        void onNoteClick(int position);
        void onNoteLongClick(int position);
    }

    public NoteAdapter(Context context, List<Note> noteList, OnNoteClickListener listener) {
        this.context = context;
        this.noteList = noteList;
        this.listener = listener;

        // Load symbols from values/note_symbols.xml
        this.icons = context.getResources().getStringArray(R.array.note_symbols);
    }

    // Store note colors
    public void setNoteColor(long noteId, int colorIndex) {
        noteColorMap.put(noteId, colorIndex);
    }

    public void setNoteSymbol(long noteId, int symbolIndex) {
        noteIconMap.put(noteId, symbolIndex);
    }

    // Map to store custom emojis
    private Map<Long, String> noteCustomEmojiMap = new HashMap<>();

    public void setNoteCustomEmoji(long noteId, String emoji) {
        noteCustomEmojiMap.put(noteId, emoji);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        // Set note data
        holder.tvTitle.setText(note.getTitle());
        holder.tvTime.setText(timeFormat.format(new Date(note.getTimestamp())));

        // Get or assign random color for this note
        int colorIndex = getOrAssignColor(note.getTimestamp());
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, colors[colorIndex]));

        // ✅ Display text symbol or custom emoji
        if (noteCustomEmojiMap.containsKey(note.getTimestamp())) {
            holder.tvIcon.setText(noteCustomEmojiMap.get(note.getTimestamp()));
        } else {
            int iconIndex = getOrAssignIcon(note.getTimestamp());
            holder.tvIcon.setText(icons[iconIndex]); // ✅ Set text instead of image
        }

        // Adjust text colors based on background color brightness
        int color = ContextCompat.getColor(context, colors[colorIndex]);
        boolean isDarkColor = isDarkColor(color);

        holder.tvTitle.setTextColor(isDarkColor ? 
                ContextCompat.getColor(context, R.color.white) : 
                ContextCompat.getColor(context, R.color.text_dark));

        holder.tvTime.setTextColor(isDarkColor ? 
                ContextCompat.getColor(context, R.color.text_light_gray) : 
                ContextCompat.getColor(context, R.color.text_gray));

        holder.tvIcon.setTextColor(isDarkColor ? 
                ContextCompat.getColor(context, R.color.white) : 
                ContextCompat.getColor(context, R.color.text_dark));
        holder.tvIcon.setAlpha(0.5f);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    private int getOrAssignColor(long noteId) {
        if (!noteColorMap.containsKey(noteId) || noteColorMap.get(noteId) == -1) {
            noteColorMap.put(noteId, random.nextInt(colors.length));
        }
        return noteColorMap.get(noteId);
    }

    private int getOrAssignIcon(long noteId) {
        if (!noteIconMap.containsKey(noteId)) {
            noteIconMap.put(noteId, random.nextInt(icons.length));
        }
        return noteIconMap.get(noteId);
    }

    private boolean isDarkColor(int color) {
        double darkness = 1 - (0.299 * android.graphics.Color.red(color) + 
                         0.587 * android.graphics.Color.green(color) + 
                         0.114 * android.graphics.Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        TextView tvTime;
        TextView tvIcon; // ✅ Use TextView instead of ImageView
        View vLine;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvIcon = itemView.findViewById(R.id.tvIcon); // ✅ Make sure this is a TextView in XML
            vLine = itemView.findViewById(R.id.vLine);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onNoteClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onNoteLongClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
}

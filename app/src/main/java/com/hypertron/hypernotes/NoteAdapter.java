package com.hypertron.hypernotes;

import androidx.core.content.ContextCompat;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
    
    // Icons for notes (using Unicode symbols)
    private String[] icons = {
        "✿",  // flower
        "☽",  // moon
        "☼",  // compass/sun symbol
        "❀",  // flower alternative
        "☀",  // sun
        "➔",  // arrow/plane
        "❋",  // flower shape
        "✧",  // star
        "◉",  // circle
        "♫",  // music note
        "❖",  // diamond
        "✤"   // flower
    };
    
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
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);
        
        // Set note data
        holder.tvTitle.setText(note.getTitle());
        holder.tvTime.setText(timeFormat.format(new Date(note.getTimestamp())));
        
        // Get or assign random color and icon for this note
        int colorIndex = getOrAssignColor(note.getTimestamp());
        int iconIndex = getOrAssignIcon(note.getTimestamp());
        
        // Set color and icon
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, colors[colorIndex]));
        holder.ivIcon.setText(icons[iconIndex]);
        
        // Adjust text colors based on background color brightness
        int color = ContextCompat.getColor(context, colors[colorIndex]);
        boolean isDarkColor = isDarkColor(color);
        
        holder.tvTitle.setTextColor(isDarkColor ? 
                ContextCompat.getColor(context, R.color.white) : 
                ContextCompat.getColor(context, R.color.text_dark));
        
        holder.tvTime.setTextColor(isDarkColor ? 
                ContextCompat.getColor(context, R.color.text_light_gray) : 
                ContextCompat.getColor(context, R.color.text_gray));
                
        holder.ivIcon.setTextColor(isDarkColor ? 
                ContextCompat.getColor(context, R.color.white) : 
                ContextCompat.getColor(context, R.color.text_dark));
        holder.ivIcon.setAlpha(0.5f);
    }
    
    private int getOrAssignColor(long noteId) {
        if (!noteColorMap.containsKey(noteId)) {
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
        TextView ivIcon;
        View vLine;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivIcon = itemView.findViewById(R.id.ivIcon);
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
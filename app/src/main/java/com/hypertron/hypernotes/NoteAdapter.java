package com.hypertron.hypernotes;

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
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<Note> noteList;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private OnNoteClickListener listener;
    
    // Icons for notes (using Unicode symbols instead of drawables)
    private String[] icons = {
        "✿",  // flower
        "☽",  // moon
        "☼",  // compass/sun symbol
        "❀",  // flower alternative
        "☀",  // sun
        "➔"   // arrow/plane
    };
    
    // Color scheme based on the provided design
    private int[] colors = {
        R.color.note_pink,
        R.color.note_purple,
        R.color.note_blue,
        R.color.note_peach,
        R.color.note_yellow,
        R.color.note_green
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
        
        // Assign color to note (consistent per position)
        int colorPos = position % colors.length;
        holder.cardView.setCardBackgroundColor(context.getResources().getColor(colors[colorPos]));
        
        // Set the icon using Unicode characters instead of drawables
        holder.ivIcon.setText(icons[colorPos]);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        TextView tvTime;
        TextView ivIcon;  // Changed to TextView to display Unicode symbols
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
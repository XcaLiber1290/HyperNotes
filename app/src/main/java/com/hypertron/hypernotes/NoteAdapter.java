public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context context;
    private List<Note> notes;
    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(int position);
        void onNoteLongClick(int position);
    }

    public NoteAdapter(Context context, List<Note> notes, OnNoteClickListener listener) {
        this.context = context;
        this.notes = notes;
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
        Note note = notes.get(position);
        
        // Set title
        holder.tvTitle.setText(note.getTitle());
        
        // Format and set time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(note.getTimestamp())));
        
        // Set symbol/emoji
        if (note.getSymbolIndex() == -2 && note.getCustomEmoji() != null) {
            // Custom emoji
            holder.tvIcon.setText(note.getCustomEmoji());
        } else if (note.getSymbolIndex() == -1 || note.getSymbolIndex() >= 0) {
            // Random or specific symbol
            String[] symbols = context.getResources().getStringArray(R.array.note_symbols);
            int index = note.getSymbolIndex() == -1 ? 
                    new Random().nextInt(symbols.length) : note.getSymbolIndex();
            holder.tvIcon.setText(symbols[index]);
        }
        
        // Set card color
        int[] colors = context.getResources().getIntArray(R.array.note_colors);
        int colorIndex = note.getColorIndex() == -1 ? 
                new Random().nextInt(colors.length) : note.getColorIndex();
        holder.cardView.setCardBackgroundColor(colors[colorIndex]);
        
        // Set on click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNoteClick(position);
            }
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onNoteLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvTitle;
        TextView tvTime;
        TextView tvIcon;

        NoteViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvIcon = itemView.findViewById(R.id.tvIcon);
        }
    }
}
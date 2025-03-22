package com.hypertron.hypernotes;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.ViewHolder> {
    private final Context context;
    private final List<String> symbols;
    private final OnSymbolClickListener listener;
    private static final int VIEW_TYPE_SYMBOL = 0;
    private static final int VIEW_TYPE_RANDOM = 1;
    private static final int VIEW_TYPE_ADD = 2;

    public interface OnSymbolClickListener {
        void onSymbolClick(int position, String symbol, boolean isRandom);
    }

    public SymbolAdapter(Context context, boolean includeSpecial, OnSymbolClickListener listener) {
        this.context = context;
        this.listener = listener;
        List<String> baseSymbols = loadSymbolsFromResources(context);
        
        // Add special items at the beginning if requested
        this.symbols = new ArrayList<>();
        if (includeSpecial) {
            // Shuffle option
            this.symbols.add("⟳");
            // Add custom option
            this.symbols.add("+");
        }
        
        // Add all regular symbols
        this.symbols.addAll(baseSymbols);
    }

    private List<String> loadSymbolsFromResources(Context context) {
        Resources res = context.getResources();
        String[] symbolArray = res.getStringArray(R.array.note_symbols);
        return new ArrayList<>(Arrays.asList(symbolArray));
    }

    @Override
    public int getItemViewType(int position) {
        String symbol = symbols.get(position);
        if (symbol.equals("🔀")) {
            return VIEW_TYPE_RANDOM;
        } else if (symbol.equals("+")) {
            return VIEW_TYPE_ADD;
        } else {
            return VIEW_TYPE_SYMBOL;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_symbol, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String symbol = symbols.get(position);
        holder.symbolText.setText(symbol);
        
        // Apply a font that doesn't render emojis
        Typeface nonEmojiFont = Typeface.create("serif", Typeface.NORMAL);
        holder.symbolText.setTypeface(nonEmojiFont);
        
        int viewType = getItemViewType(position);
        
        if (viewType == VIEW_TYPE_RANDOM) {
            holder.itemView.setContentDescription(context.getString(R.string.random_symbol));
        } else if (viewType == VIEW_TYPE_ADD) {
            holder.itemView.setContentDescription(context.getString(R.string.add_custom_symbol));
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                if (viewType == VIEW_TYPE_RANDOM) {
                    listener.onSymbolClick(-1, symbol, true);
                } else if (viewType == VIEW_TYPE_ADD) {
                    // The dialog will handle custom emoji input
                    listener.onSymbolClick(-2, symbol, false);
                } else {
                    listener.onSymbolClick(position - (symbols.contains("🔀") ? 1 : 0) - (symbols.contains("+") ? 1 : 0), 
                                        symbol, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return symbols.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView symbolText;

        ViewHolder(View itemView) {
            super(itemView);
            symbolText = itemView.findViewById(R.id.tvSymbolAsImage);
        }
    }
}
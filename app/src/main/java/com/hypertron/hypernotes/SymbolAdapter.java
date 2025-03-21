package com.hypertron.hypernotes;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.ViewHolder> {
    private final Context context;
    private final List<String> symbols;
    private final OnSymbolClickListener listener;

    public interface OnSymbolClickListener {
        void onSymbolClick(int position, String symbol, boolean isRandom);
    }

    public SymbolAdapter(Context context, boolean isRandom, OnSymbolClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.symbols = loadSymbolsFromResources(context);
    }

    private List<String> loadSymbolsFromResources(Context context) {
        Resources res = context.getResources();
        String[] symbolArray = res.getStringArray(R.array.note_symbols);
        return new ArrayList<>(Arrays.asList(symbolArray));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String symbol = symbols.get(position);
        holder.textView.setText(symbol);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSymbolClick(position, symbol, false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return symbols.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

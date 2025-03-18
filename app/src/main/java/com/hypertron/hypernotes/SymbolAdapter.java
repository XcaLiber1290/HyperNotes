// SymbolAdapter.java
package com.hypertron.hypernotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.SymbolViewHolder> {

    private String[] symbols;
    private OnSymbolClickListener listener;

    public interface OnSymbolClickListener {
        void onSymbolClick(int position, String symbol);
    }

    public SymbolAdapter(String[] symbols, OnSymbolClickListener listener) {
        this.symbols = symbols;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SymbolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symbol, parent, false);
        return new SymbolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SymbolViewHolder holder, int position) {
        holder.tvSymbol.setText(symbols[position]);
    }

    @Override
    public int getItemCount() {
        return symbols.length;
    }

    class SymbolViewHolder extends RecyclerView.ViewHolder {
        TextView tvSymbol;

        public SymbolViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSymbol = itemView.findViewById(R.id.tvSymbol);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onSymbolClick(position, symbols[position]);
                    }
                }
            });
        }
    }
}
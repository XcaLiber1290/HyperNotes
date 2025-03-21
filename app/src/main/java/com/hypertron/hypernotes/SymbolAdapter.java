// SymbolAdapter.java
package com.hypertron.hypernotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SymbolAdapter extends RecyclerView.Adapter<SymbolAdapter.SymbolViewHolder> {

    private String[] symbols;
    private OnSymbolClickListener listener;
    private boolean includeRandomOption;

    public interface OnSymbolClickListener {
        void onSymbolClick(int position, String symbol, boolean isRandom);
    }

    public SymbolAdapter(String[] symbols, boolean includeRandomOption, OnSymbolClickListener listener) {
        this.symbols = symbols;
        this.listener = listener;
        this.includeRandomOption = includeRandomOption;
    }

    @NonNull
    @Override
    public SymbolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_symbol, parent, false);
        return new SymbolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SymbolViewHolder holder, int position) {
        // Determine if this item is the random option (first item when includeRandomOption is true)
        boolean isRandomOption = includeRandomOption && position == 0;
        
        if (isRandomOption) {
            // This is the Random option - use shuffle icon
            holder.tvSymbol.setText(holder.itemView.getContext().getString(R.string.random_symbol));
            holder.tvSymbolAsImage.setVisibility(View.GONE);
            holder.ivSymbol.setVisibility(View.VISIBLE);
            holder.ivSymbol.setImageResource(R.drawable.shuffle_24);
            holder.ivSymbol.setContentDescription(holder.itemView.getContext().getString(R.string.random_symbol));
        } else {
            // This is a regular symbol
            int symbolIndex = includeRandomOption ? position - 1 : position;
            String symbol = symbols[symbolIndex];
            
            // Set text on the TextView (it will be hidden but we keep it for accessibility)
            holder.tvSymbol.setText(symbol);
            
            // Use the TextView styled as image for display
            holder.ivSymbol.setVisibility(View.GONE);
            holder.tvSymbolAsImage.setVisibility(View.VISIBLE);
            holder.tvSymbolAsImage.setText(symbol);
        }
    }

    @Override
    public int getItemCount() {
        return includeRandomOption ? symbols.length + 1 : symbols.length;
    }

    class SymbolViewHolder extends RecyclerView.ViewHolder {
        TextView tvSymbol;
        ImageView ivSymbol;
        TextView tvSymbolAsImage;

        public SymbolViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSymbol = itemView.findViewById(R.id.tvSymbol);
            ivSymbol = itemView.findViewById(R.id.ivSymbol);
            tvSymbolAsImage = itemView.findViewById(R.id.tvSymbolAsImage);
            
            // Hide the regular TextView
            tvSymbol.setVisibility(View.GONE);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        boolean isRandomOption = includeRandomOption && position == 0;
                        
                        if (isRandomOption) {
                            // Clicking the random option
                            listener.onSymbolClick(-1, null, true);
                        } else {
                            // Regular symbol clicked
                            int symbolIndex = includeRandomOption ? position - 1 : position;
                            listener.onSymbolClick(symbolIndex, symbols[symbolIndex], false);
                        }
                    }
                }
            });
        }
    }
}
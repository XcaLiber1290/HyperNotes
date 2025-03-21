package com.hypertron.hypernotes;

import android.content.Context;
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

    public SymbolAdapter(Context context, boolean includeRandomOption, OnSymbolClickListener listener) {
        try {
            this.symbols = context.getResources().getStringArray(R.array.note_symbols);
        } catch (Exception e) {
            e.printStackTrace();
            this.symbols = new String[]{"❌"}; // Fallback
        }
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
        if (symbols == null || symbols.length == 0) {
            holder.tvSymbolAsImage.setText("❌");
            holder.tvSymbol.setText("Error");
            holder.ivSymbol.setVisibility(View.GONE);
            holder.tvSymbolAsImage.setVisibility(View.VISIBLE);
            return;
        }

        try {
            if (includeRandomOption && position == 0) {
                // Random symbol
                holder.tvSymbol.setText("🔀");
                holder.ivSymbol.setVisibility(View.GONE);
                holder.tvSymbolAsImage.setVisibility(View.VISIBLE);
                holder.tvSymbolAsImage.setText("🔀");
                holder.tvSymbolAsImage.setContentDescription("Random Symbol");
            } else {
                int symbolIndex = includeRandomOption ? position - 1 : position;
                if (symbolIndex >= 0 && symbolIndex < symbols.length) {
                    String symbol = symbols[symbolIndex];
                    holder.tvSymbol.setText(symbol);
                    holder.ivSymbol.setVisibility(View.GONE);
                    holder.tvSymbolAsImage.setVisibility(View.VISIBLE);
                    holder.tvSymbolAsImage.setText(symbol);
                    holder.tvSymbolAsImage.setContentDescription(symbol);
                } else {
                    // Out of bounds - show error symbol
                    holder.tvSymbolAsImage.setText("❌");
                    holder.tvSymbolAsImage.setContentDescription("Error");
                }
            }
        } catch (Exception e) {
            // Catch any unexpected exceptions
            e.printStackTrace();
            holder.tvSymbolAsImage.setText("❌");
            holder.tvSymbolAsImage.setContentDescription("Error");
        }
    }

    @Override
    public int getItemCount() {
        return includeRandomOption ? (symbols.length + 1) : symbols.length;
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
            tvSymbol.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int position = getAbsoluteAdapterPosition();
                        if (position == RecyclerView.NO_POSITION || listener == null) return;

                        if (includeRandomOption && position == 0) {
                            listener.onSymbolClick(-1, null, true);
                        } else {
                            int symbolIndex = includeRandomOption ? position - 1 : position;
                            if (symbolIndex >= 0 && symbolIndex < symbols.length) {
                                listener.onSymbolClick(symbolIndex, symbols[symbolIndex], false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

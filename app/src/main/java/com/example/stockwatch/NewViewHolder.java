package com.example.stockwatch;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NewViewHolder extends RecyclerView.ViewHolder {
    public TextView symbol;
    public TextView name;
    public TextView price;
    public TextView change;


    public NewViewHolder(View v){
        super(v);
        symbol = itemView.findViewById(R.id.stockSymbol);
        name = itemView.findViewById(R.id.stockName);
        price = itemView.findViewById(R.id.stockPrice);
        change = itemView.findViewById(R.id.stockChange);
    }
}

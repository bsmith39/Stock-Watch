package com.example.stockwatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<NewViewHolder> {
    private List<Stock> stockList;
    private MainActivity mainActivity;

    public StockAdapter(List<Stock> sList, MainActivity ma){
        this.stockList = sList;
        mainActivity = ma;
    }

    @NonNull
    @Override
    public NewViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_setup, parent, false);
        itemView.setOnLongClickListener(mainActivity);
        itemView.setOnClickListener(mainActivity);
        return new NewViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull NewViewHolder holder, int pos){
        Stock stock = stockList.get(pos);

        holder.change.setText(stock.getUd() + " " + stock.getChange());
        holder.price.setText(stock.getPrice());
        holder.name.setText(stock.getName());
        holder.symbol.setText(stock.getSymbol());
        /*if(stock.getUd().equals("v")){
            holder.change.setTextColor(16711680);
            holder.price.setTextColor(16711680);
            holder.name.setTextColor(16711680);
            holder.symbol.setTextColor(16711680);
        }else{
            holder.change.setTextColor(65280);
            holder.price.setTextColor(65280);
            holder.name.setTextColor(65280);
            holder.symbol.setTextColor(65280);
        }*/

    }

    @Override
    public int getItemCount(){return stockList.size();}

}

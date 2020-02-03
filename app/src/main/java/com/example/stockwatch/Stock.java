package com.example.stockwatch;

import android.net.Network;
import android.net.NetworkInfo;

public class Stock {
    private String symbol;
    private String name;
    private String price;
    private String change;
    private String ud;
    private String col;

    public Stock() {
        this.change = "";
        this.name = "";
        this.price = "";
        this.symbol = "";
        this.ud = "";
        this.col = "g";
    }

    public Stock(String s, String n, String p, String c, double u){
        this.change = c;
        this.name = n;
        this.price = p;
        this.symbol = s;
        if (u > 0) {
            this.ud = "^";
            this.col = "g";
        }else{
            this.ud = "v";
            this.col = "r";
        }
    }

    public Stock(String s, String n){
        this.symbol = s;
        this.name = n;
    }

    public String getSymbol() {return symbol;}

    public String getName() {return name;}

    public String getPrice() {return price;}

    public String getChange() {return change;}

    public String getUd() {return ud;}

    public void setChange(String change) {
        this.change = change;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setUd(double ud) {
        if (ud > 0) {
            this.ud = "^";
            this.col = "g";
        } else {
            this.ud = "v";
            this.col = "r";
        }
    }


}


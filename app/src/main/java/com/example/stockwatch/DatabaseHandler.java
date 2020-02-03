package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SymbolTable;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final  String TAG = "DatabaseHandler";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "StocksDB";

    private static final String TABLE_NAME = "StockTable";

    private static final String NAME = "Name";
    private static final String SYMBOL = "Symbol";
    //private static final String PERCENT_CHANGE = "PercentChange";
    //private static final String CHANGE_DIRECTION = "ChangeDirection";
    //private static final String PRICE = "Price";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique, "+
                    NAME + " TEXT not null)";

    private SQLiteDatabase database;
    private  MainActivity ma;

    DatabaseHandler(MainActivity context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ma = context;
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d(TAG, "onCreate: Making new DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }


        ArrayList<Stock> loadStocks(String s) {
        Log.d(TAG, "loadStocks: Start");
        ArrayList<Stock> stocks = new ArrayList<>();

        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{SYMBOL, NAME},
                SYMBOL + " LIKE '"+ s +"%'",
                null,
                null,
                null,
                SYMBOL);
        if (cursor != null) {
            cursor.moveToFirst();

            for(int i = 0; i < cursor.getCount(); i++){
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);
                //String price = cursor.getString(2);
                //String change_direction = cursor.getString(3);
                //String percent_change = cursor.getString(4);
                Stock stock = new Stock(symbol, name);
                stocks.add(stock);
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: Done");
        return stocks;
    }

    void addStock(Stock s) {
        try {
            ContentValues values = new ContentValues();

            values.put(SYMBOL, s.getSymbol());
            values.put(NAME, s.getName());
            //values.put(PRICE, s.getPrice());
            //values.put(CHANGE_DIRECTION, s.getUd());
            //values.put(PERCENT_CHANGE, s.getChange());

            long key = database.insert(TABLE_NAME, null, values);
            Log.d(TAG, "addStock: " + key);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void updateStock(Stock s){
        ContentValues values = new ContentValues();

        values.put(SYMBOL, s.getSymbol());
        values.put(NAME, s.getName());
        //values.put(PRICE, s.getPrice());
        //values.put(CHANGE_DIRECTION, s.getUd());
        //values.put(PERCENT_CHANGE, s.getChange());

        long numRows = database.update(TABLE_NAME,  values, SYMBOL + " = ?", new String[]{s.getSymbol()} );
        Log.d(TAG, "updateStock: " + numRows);
    }

    void deleteStock(String name){
        Log.d(TAG, "deleteStock: " + name);
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{name});
        Log.d(TAG, "deleteStock: " + cnt);
    }

    void dumpDbToLog(){
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if(cursor != null){
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for(int i = 0;i<cursor.getCount(); i++){
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);
                //String price = cursor.getString(2);
                //String change_direction = cursor.getString(3);
                //String percent_change = cursor.getString(4);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", NAME + ":", name));
                        //String.format("%s %-18s", PRICE + ":", price) +
                        //String.format("%s %-18s", CHANGE_DIRECTION + ":", change_direction) +
                        //String.format("%s %-18s", PERCENT_CHANGE + ":", percent_change));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

    }
    void shutdown(){database.close();}
}

package com.example.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {


    private List<Stock> sl = new ArrayList<>();
    private RecyclerView rv;
    private StockAdapter sa;
    private SwipeRefreshLayout swiper;
    private DatabaseHandler databaseHandler;
    private final  MainActivity ma = this;
    private static final String TAG = "MainActivity";


    //TODO####################################################################
    ////Set up the second async when a stock is selected to be put in. only add stocks with this async
    ////(Should be Easy)
    ////Refresh should do second async for every stock in list (Should be easy)
    ////Network check  (Super easy)
    ////Coloring        (Should be super easy)
    ////fix up and down symbols     (Should be super Easy)
    //TODO#####################################################################


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.recycler);
        sa = new StockAdapter(sl, this);
        rv.setAdapter(sa);
        rv.setLayoutManager(new LinearLayoutManager(this));
        sa.notifyDataSetChanged();
        if(!netCheck()){

        }
        databaseHandler = new DatabaseHandler(this);
        databaseHandler.dumpDbToLog();
        new SymbolNameAsync(this).execute();

        final View view = findViewById(R.id.swiper);
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Toast.makeText(view.getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                if(netCheck()){
                    for(int i = 0; i< sl.size(); i++){
                        sl = orgList(sl);
                        Stock tmp = sl.remove(i);
                        new DataAsync(ma).execute(tmp.getSymbol());
                        swiper.setRefreshing(false);

                    }
                }else{
                    swiper.setRefreshing(false);

                }

                //Refresh all data and recycler
            }
        });


    }

    public List<Stock> orgList(List<Stock> list){
        for (int i = 0; i < list.size(); i++){
            for (int j = i+1; j<list.size();j++){
                if(list.get(i).getSymbol().compareTo(list.get(j).getSymbol())<0){
                    Stock tmp = list.get(i);
                    list.set(i,list.get(j));
                    list.set(j,tmp);
                }
            }
        }
        return list;
    }

    protected void callDatabase(ArrayList<Stock> list) {
        for (int i = 0; i < list.size(); i++) {
            databaseHandler.addStock(list.get(i));
        }
        //Toast.makeText(this, "DONE WITH ADD", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        //databaseHandler.dumpDbToLog();
        //ArrayList<Stock> list = databaseHandler.loadStocks();
        //sl.clear();
        //sl.addAll(list);
        //Log.d(TAG, "onResume: " + list);
        //sa.notifyDataSetChanged();

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stock_menu, menu);
        return true;
    }

    int check = 0;
    String in = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        check = 0;
        in = "";
        if(!netCheck()){
            return false;
        }
        if (item.getItemId() == R.id.addStock) {
            //prompt in all caps asking for
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            builder.setIcon(R.mipmap.ic_launcher_round);
            builder.setTitle("Search for stock");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    check = 1;
                    in = input.getText().toString();
                    Search();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    check = 0;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return true;
    }

    public void Search(){
        final ArrayList<Stock> arr = databaseHandler.loadStocks(in);
        if(check ==1) {
            AlertDialog.Builder search = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
            if (arr.size() == 0) {
                search.setTitle("No Match Found");
                AlertDialog dialog = search.create();
                dialog.show();
                return;
            } else if (arr.size() == 1) {
                String sym = arr.get(0).getSymbol();
                new DataAsync(ma).execute(sym);
                return;
            } else {
                CharSequence[] s = new CharSequence[arr.size()];
                for (int i = 0; i < arr.size(); i++) {
                    CharSequence temp = arr.get(i).getSymbol() + " - " + arr.get(i).getName();
                    s[i] = temp;
                }
                search.setTitle("Make Selection");
                search.setItems(s, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sym = arr.get(i).getSymbol();
                        new DataAsync(ma).execute(sym);


                    }
                });
                search.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = search.create();
                dialog.show();
                return;


            }

        }
            return;
    }

    public void addS(Stock stock){
        for(int i = 0;i < sl.size();i++){
            if(stock.getSymbol().equals(sl.get(i).getSymbol())){
                AlertDialog.Builder dup = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
                dup.setTitle("Duplicate Stock");
                dup.setMessage("Stock Symbol" + stock.getSymbol() +" is already displayed");
                dup.show();
                return;

            }
        }
        sl.add(stock);
        sl = orgList(sl);
        sa.notifyDataSetChanged();
        //Toast.makeText(this.getApplicationContext(), "DONE Adding", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v){
        //fill in
        //Implement internet search for the website for each stock
        View vi = v;
        int pos = rv.getChildLayoutPosition(v);
        Stock s = sl.get(pos);
        String ss = s.getSymbol();
        goToUrl("https://www.marketwatch.com/investing/stock/" + ss);
        //Toast.makeText(vi.getContext(), "https://www.marketwatch.com/investing/stock/" + ss, Toast.LENGTH_SHORT).show();


    }

    private void goToUrl(String url){
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    @Override
    public boolean onLongClick(View v) {
        //fill in
        //asks to delete then does if yes is pressed
        final View vi = v;
        final int pos = rv.getChildLayoutPosition(v);
        Stock s = sl.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //databaseHandler.deleteStock(sl.get(pos).getName());
                sl.remove(pos);
                sa.notifyDataSetChanged();
                //Toast.makeText(vi.getContext(), "Stock Deleted", Toast.LENGTH_SHORT).show();
            }

        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setTitle("Delete " + s.getName() +"?");
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }


    public boolean netCheck(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null){
            return false;
        }

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        AlertDialog.Builder con = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else{
            con.setTitle("No Network Connection");
            con.setMessage("Connect to network to refresh or add new stocks");
            con.show();
            return false;
        }
    }



    /*protected void onPause(){
        //fill in
        //save
        return;
    }*/


    //private void save(){}

}

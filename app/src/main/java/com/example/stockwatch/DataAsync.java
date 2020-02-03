package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DataAsync extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;
    private static final String Key = "sk_d5745208e17e4e7bb04e25211912e5a2";
    private static final String dataURL = "https://cloud.iexapis.com/stable/stock/";
    private Stock retStock = new Stock();
    private static final String TAG = "DataAsync";

    public DataAsync(MainActivity ma){mainActivity = ma;}

    @Override
    protected String doInBackground(String... params) {
        Log.d(TAG, "doInBackground: Start Back");
        String sym = params[0];
        String url = dataURL + sym + "/quote?token=" + Key;
        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        String urlToUse = buildURL.build().toString();

        StringBuilder sb = new StringBuilder();

        try {
            URL Url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = read.readLine()) != null) {
                sb.append(line).append("\n");
            }
            is.close();
            read.close();
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
        }


        return null;
    }

        /*try{
            InputStream is = mainActivity.getApplicationContext().openFileInput("file_name.json");
            JsonReader read = new JsonReader(new InputStreamReader(is, "UTF-8"));
            read.beginArray();
            while(read.hasNext()){
                read.beginObject();
                while(read.hasNext()){
                    String name = read.nextName();
                    if(name.equals("symbol")){
                        dum.add((read.nextString()));
                    }else if (name.equals("companyName")){
                        dum.add((read.nextString()));
                    } else if(name.equals("latestPrice")){
                        dum.add((read.nextString()));
                    }else if(name.equals("Change")){
                        dum.add((read.nextString()));
                    }else if(name.equals("changePercent")){
                        dum.add(read.nextString());
                    } else{
                        read.skipValue();
                    }
                }
                read.endObject();
            }
            read.endArray();
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //return dum;


    @Override
    protected void onPostExecute(String s){
        Stock stock = parseJSON(s);
        mainActivity.addS(stock);
        return;
    }

    public Stock parseJSON(String s){
        try {
            //JSONArray jObjMain = new JSONArray("{" + s + "}");
            //JSONArray jObjMain = new JSONArray(s) ;
            //JSONObject jStock = (JSONObject) jObjMain.get(0);
            JSONObject jStock = new JSONObject(s);
            Stock stock = new Stock(jStock.getString("symbol"),
                    jStock.getString("companyName"),
                    jStock.getString("latestPrice"),
                    jStock.getString("changePercent"),
                    jStock.getDouble("change"));
            return stock;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

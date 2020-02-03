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

public class SymbolNameAsync extends AsyncTask<String, Integer, List<String>> {
    private MainActivity mainActivity;
    private ArrayList<Stock> retList = new ArrayList<>();
    private static final String dataURL = "https://api.iextrading.com/1.0/ref-data/symbols";
    private static final String TAG = "SymbolNameAsync";


    public SymbolNameAsync(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected List<String> doInBackground(String... params) {
        Uri.Builder buildURL = Uri.parse(dataURL).buildUpon();
        String urlToUse = buildURL.build().toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = read.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
        }

        parseJSON(sb.toString());
        return null;
    }

    //JsonReader read = new JsonReader(new InputStreamReader(is, "UTF-8"));
                /*read.beginArray();
                while(read.hasNext()){
                    read.beginObject();
                    while(read.hasNext()){
                        String name = read.nextName();
                        if(name.equals("symbol")){
                            dum.add((read.nextString()));
                        }else if(name.equals("name")){
                            dum.add((read.nextString()));
                        }else{
                            read.skipValue();
                        }
                    }
                    read.endObject();
                }
                read.endArray();
            }catch (Exception e){
                e.printStackTrace();
            }
            return dum;
        }*/
    /*@Override
    protected void onPostExecute(List<String> l) {
        List<Stock> s = new ArrayList<>();
        for (int i = 0; i < l.size(); i = i + 2) {
            Stock stock = new Stock(l.get(i), l.get(i + 1), "", "", "");
            s.add(stock);
        }
    }*/

    @Override
    protected void onPostExecute(List<String> l){
        mainActivity.callDatabase(retList);
    }


    private void parseJSON(String s) {
        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                Stock stock = new Stock(jStock.getString("symbol"), jStock.getString("name"));
                retList.add(stock);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

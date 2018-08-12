package com.example.ankit.mausum;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpCalls {
    Context context;
RecyclerView rvFuture;
    private static final String TAG = "meg";
    public OkhttpCalls(Context context, RecyclerView rvFuture) {
        this.context = context;
        this.rvFuture = rvFuture;
    }

    public void Call(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                final ArrayList<WeatherList> arrayList = parse(res);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FutureAdapter futureAdapter=new FutureAdapter(arrayList);
                        rvFuture.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
                        rvFuture.setAdapter(futureAdapter);
                    }
                });
            }
        });
    }

        public ArrayList<WeatherList> parse (String url)
        {
            ArrayList<WeatherList> arrayList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(url);
                JSONArray jsonArray = jsonObject.getJSONArray("list");
                for (int i = 0; i < 7; ++i) {
                    JSONObject listJson = jsonArray.getJSONObject(i);
                    JSONObject mainJson = listJson.getJSONObject("main");
                    String Degree = mainJson.getString("temp");
                    int temp = (int) (Double.valueOf(Degree) - 273.15);
                    JSONArray weatherJson = listJson.getJSONArray("weather");
                    JSONObject weatherObject = weatherJson.getJSONObject(0);
                    String main = weatherObject.getString("description");
                    String icon = weatherObject.getString("icon");
                    String dt_txt = listJson.getString("dt_txt");
                    WeatherList weatherList = new WeatherList(temp, main, icon);
                    weatherList.setTime(dt_txt);
                    weatherList.setDegree(temp);
                    arrayList.add(weatherList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return arrayList;
        }

}

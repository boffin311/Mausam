package com.example.ankit.mausum;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ankit.mausum.database.CityList;
import com.example.ankit.mausum.database.MyDatabaseHelper;
import com.example.ankit.mausum.database.cityTable;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
public static final String TAG="CHK";


    TextView tvmain,tvDate,tvLocation,tvHours,tvMin,tvTemp,tvDegree,tvMonth,tvDay;
    ImageView imageWeather;
    Button retry;
    RecyclerView rvFutureWeather,rvFutureWeather2,rvFuture;
    LinearLayout linear,lastLinear;
    SearchView search;
    SQLiteDatabase db;
    LinearLayout initialLinear;
    ImageView image;
    ProgressBar progress;
    AppBarLayout appbar;
    OkhttpCalls okhttpCalls;
    String city,tempCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(getBaseContext());
        db = myDatabaseHelper.getWritableDatabase();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       appbar=findViewById(R.id.appbar);
        tvmain=findViewById(R.id.tvmain);
        tvLocation=findViewById(R.id.tvLocation);
        initialLinear=findViewById(R.id.initialLinear);
        linear=findViewById(R.id.linear);
        progress=findViewById(R.id.progress);
        lastLinear=findViewById(R.id.lastLinear);
        rvFutureWeather=findViewById(R.id.rvfutureWeather);
        rvFutureWeather2=findViewById(R.id.rvfutureWeather2);
        rvFuture=findViewById(R.id.rvFuture);
        tvDegree=findViewById(R.id.tvDegree);
        tvDate=findViewById(R.id.tvDate);
        tvDay=findViewById(R.id.tvDay);
        tvHours=findViewById(R.id.tvHours);
        tvMin=findViewById(R.id.tvMin);
         retry=findViewById(R.id.btnRetry);
        tvTemp=findViewById(R.id.tvTemp);
        imageWeather=findViewById(R.id.imageWeather);
        tvMonth=findViewById(R.id.tvMonth);



        ArrayList<CityList> arrayList= cityTable.readCity(db);
    if (arrayList.size()==0)
    { cityTable.insertCity(db);
    city=cityTable.readCity(db).get(0).getCity();}
    else
    city = arrayList.get(0).getCity();
    makeAcall("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=2bfddb3d0f0062143837f34fdf64c723");
    okhttpCalls = new OkhttpCalls(getBaseContext(), rvFuture);
    okhttpCalls.Call("https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=2bfddb3d0f0062143837f34fdf64c723");
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAcall("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=2bfddb3d0f0062143837f34fdf64c723");
                okhttpCalls = new OkhttpCalls(getBaseContext(), rvFuture);
                okhttpCalls.Call("https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=2bfddb3d0f0062143837f34fdf64c723");
            }
        });
        search =findViewById(R.id.search);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               progress.setVisibility(View.VISIBLE);
                tempCity=query;
                Log.d(TAG, "onQueryTextSubmit: "+query);
                makeAcall("https://api.openweathermap.org/data/2.5/weather?q="+query+"&appid=2bfddb3d0f0062143837f34fdf64c723");

                okhttpCalls = new OkhttpCalls(getBaseContext(),rvFuture);
                okhttpCalls.Call("https://api.openweathermap.org/data/2.5/forecast?q="+query+"&appid=2bfddb3d0f0062143837f34fdf64c723");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void makeAcall(String url){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                initialLinear.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                  progress.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string=response.body().string();
                final ArrayList<WeatherList> arrayList=parse(string);
                final ArrayList<tvSettingList> tvDetaiArray=parseIt(string);
                final ArrayList<tvSettingList> tvArrayList=getInfo(string);
                if (response.isSuccessful()){
                   MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        initialLinear.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                            progress.setVisibility(View.INVISIBLE);
                            DetailAdapter detailAdapter = new DetailAdapter(tvDetaiArray);
                            DetailAdapter adapter = new DetailAdapter(tvArrayList);
                            rvFutureWeather.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                            rvFutureWeather.setAdapter(detailAdapter);
                            rvFutureWeather2.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
                            rvFutureWeather2.setAdapter(adapter);

                            int hours = Calendar.getInstance().get(Calendar.HOUR);
                            int minutes = Calendar.getInstance().get(Calendar.MINUTE);
                            int date = Calendar.getInstance().get(Calendar.DATE);
                            int month = Calendar.getInstance().get(Calendar.MONTH);
                            switch (month) {
                                case 0: tvMonth.setText("Jan");break;
                                case 1: tvMonth.setText("Feb");break;
                                case 2: tvMonth.setText("Mar");break;
                                case 3: tvMonth.setText("Apr");break;
                                case 4: tvMonth.setText("May");break;
                                case 5: tvMonth.setText("Jun");break;
                                case 6: tvMonth.setText("Jul");break;
                                case 7: tvMonth.setText("Aug");break;
                                case 8: tvMonth.setText("Sept");break;
                                case 9: tvMonth.setText("Oct");break;
                                case 10: tvMonth.setText("Nov");break;
                                case 11: tvMonth.setText("Dec");break;
                            }

                            int Day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                            switch (Day) {
                                case 1: tvDay.setText("Sunday");break;
                                case 2: tvDay.setText("Monday");break;
                                case 3: tvDay.setText("Tuesday");break;
                                case 4: tvDay.setText("Wednesday");break;
                                case 5: tvDay.setText("Thursday");break;
                                case 6: tvDay.setText("Friday");break;
                                case 7: tvDay.setText("Saturday");break;
                            }
                            if (arrayList.size() != 0) {
                               if (tempCity!=null)
                               {city=tempCity;
                                   Log.d(TAG, "run: "+city);}
                                switch (arrayList.get(0).getIcon()) {
                                    case "01d":
                                        imageWeather.setImageResource(R.drawable.one_d);
                                        imagesetter("https://www.flowerpicturegallery.com/d/602-1/sunflower+on+sunny+day.jpg");
                                        appbar.setBackgroundColor(Color.rgb(13, 123, 178));
                                        lastLinear.setBackgroundColor(Color.argb(255, 238, 151, 26));
                                        break;
                                    case "01n":
                                        imageWeather.setImageResource(R.drawable.one_n);
                                        imagesetter("https://ae01.alicdn.com/kf/HTB15shWlCYH8KJjSspdq6ARgVXag/Moon-lake-mountains-cold-night-nature-scenery-poster-silk-fabric-cloth-print-wall-sticker-Wall-Decor.jpg_640x640.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 9, 77, 126));
                                        appbar.setBackgroundColor(Color.rgb(23, 104, 165));
                                        break;
                                    case "02d":
                                        imageWeather.setImageResource(R.drawable.two_d);
                                        imagesetter("https://wallpaper-house.com/data/out/12/wallpaper2you_541439.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 80, 35, 5));
                                        appbar.setBackgroundColor(Color.rgb(69, 148, 202));
                                        break;
                                    case "02n":
                                        imageWeather.setImageResource(R.drawable.two_n);
                                        imagesetter("https://www.goabase.net/pic/20170805_luna-rave-experience_20170315055734.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 66, 67, 172));
                                        appbar.setBackgroundColor(Color.rgb(40, 26, 39));
                                        break;
                                    case "03d":
                                        imageWeather.setImageResource(R.drawable.three_d);
                                        imagesetter("https://d2v9y0dukr6mq2.cloudfront.net/video/thumbnail/LAp2L-s/scattered-cloud-sky-lapse_nyvsayafx__F0000.png");
                                        lastLinear.setBackgroundColor(Color.argb(255, 79, 118, 175));
                                        appbar.setBackgroundColor(Color.rgb(10, 29, 98));
                                        break;
                                    case "03n":
                                        imageWeather.setImageResource(R.drawable.three_n);
                                        imagesetter("http://www.megacidade.com/images/noticias/5115/1644550362.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 75, 88, 122));
                                        appbar.setBackgroundColor(Color.rgb(104, 122, 165));
                                        break;
                                    case "04d":
                                        imageWeather.setImageResource(R.drawable.three_d);
                                        imagesetter("https://img00.deviantart.net/6364/i/2017/030/4/1/broken_clouds_by_kevintheman-dax9bd4.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 85, 144, 207));
                                        appbar.setBackgroundColor(Color.rgb(16, 47, 94));
                                        break;
                                    case "04n":
                                        imageWeather.setImageResource(R.drawable.three_n);
                                        imagesetter("http://s3.thingpic.com/images/4U/JKaaHQg8ot7bjGc26EeiESfs.jpeg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 9, 77, 126));
                                        //  appbar.setBackgroundColor(Color.rgb());
                                        break;
                                    case "09d":
                                        imageWeather.setImageResource(R.drawable.nine_d);
                                        imagesetter("https://cdn1.bbend.net/media/com_news/story/2014/11/13/521218/main/Raineck.jpg");
                                        appbar.setBackgroundColor(Color.rgb(147, 152, 38));

                                        lastLinear.setBackgroundColor(Color.argb(255, 120, 121, 124));
                                        break;
                                    case "09n":
                                        imageWeather.setImageResource(R.drawable.nine_d);
                                        imagesetter("https://photo-cdn.urdupoint.com/media/2017/04/_3/730x425/pic_1491298911.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 80, 96, 121));
                                        appbar.setBackgroundColor(Color.rgb(1, 11, 35));
                                        break;
                                    case "10d":
                                        imageWeather.setImageResource(R.drawable.ten_d);
                                        imagesetter("https://sciencetrends-techmakaillc.netdna-ssl.com/wp-content/uploads/2017/10/rainbow.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 119, 82, 40));
                                        appbar.setBackgroundColor(Color.rgb(147, 176, 214));
                                        break;
                                    case "10n":
                                        imageWeather.setImageResource(R.drawable.ten_n);
                                        imagesetter("https://www.wallpaperup.com/uploads/wallpapers/2013/10/20/163403/90bd6de711bd0d52ce0939064a388963-700.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 56, 95, 137));
                                        appbar.setBackgroundColor(Color.rgb(1, 11, 39));
                                        break;
                                    case "11d":
                                        imageWeather.setImageResource(R.drawable.eleven_d);
                                        imagesetter("https://okcthunderbasketball.files.wordpress.com/2008/08/thunder.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 47, 53, 84));
                                        appbar.setBackgroundColor(Color.rgb(58, 64, 110));
                                        break;
                                    case "11n":
                                        imageWeather.setImageResource(R.drawable.eleven_n);
                                        imagesetter("https://www.thoughtco.com/thmb/35HKmrimexItlqC0a1-vnimJe94=/768x0/filters:no_upscale():max_bytes(150000):strip_icc()/92173281-56a2acf75f9b58b7d0cd4cee.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 54, 52, 92));
                                        appbar.setBackgroundColor(Color.rgb(110, 74, 117));
                                        break;
                                    case "13d":
                                        imageWeather.setImageResource(R.drawable.thirteen_d);
                                        imagesetter("http://www.trbimg.com/img-5aa059f5/turbine/bs-md-weather-20180305");
                                        lastLinear.setBackgroundColor(Color.argb(255, 139, 145, 155));
                                        appbar.setBackgroundColor(Color.rgb(180, 185, 193));
                                        break;
                                    case "13n":
                                        imageWeather.setImageResource(R.drawable.thirteen_n);
                                        imagesetter("https://fournews-assets-prod-s3b-ew1-aws-c4-pml.s3.amazonaws.com/media/2017/12/snow_london_g_hd.jpg");
                                        lastLinear.setBackgroundColor(Color.argb(255, 109, 97, 193));
                                        appbar.setBackgroundColor(Color.rgb(112, 108, 213));
                                        break;
                                    case "50d":
                                        imageWeather.setImageResource(R.drawable.fifty_d);
                                        imagesetter("https://bloximages.chicago2.vip.townnews.com/host.madison.com/content/tncms/assets/v3/editorial/d/47/d4757e35-03ee-504c-a42a-fa738f0e0f72/582269020f39e.image.jpg?resize=1200%2C803");
                                        lastLinear.setBackgroundColor(Color.argb(255, 207, 137, 69));
                                        appbar.setBackgroundColor(Color.rgb(161, 119, 72));
                                        break;
                                    case "50n":
                                        imageWeather.setImageResource(R.drawable.fifty_n);
                                        imagesetter("https://bloximages.chicago2.vip.townnews.com/host.madison.com/content/tncms/assets/v3/editorial/d/47/d4757e35-03ee-504c-a42a-fa738f0e0f72/582269020f39e.image.jpg?resize=1200%2C803");
                                        lastLinear.setBackgroundColor(Color.argb(255, 207, 137, 69));
                                        appbar.setBackgroundColor(Color.rgb(161, 119, 72));
                                        break;
                                }
                                tvTemp.setText(String.valueOf(arrayList.get(0).getTemp()));
                                tvDate.setText(String.valueOf(date));
                                if (search.getQuery().length() != 0) {
                                    tvLocation.setText("in " + search.getQuery());
                                   }
                                    else
                                {tvLocation.setText("in " + city);
                                    }
                                if (minutes < 10)
                                    tvMin.setText(String.valueOf("0" + minutes));
                                else
                                    tvMin.setText(String.valueOf(minutes));
                                tvHours.setText(String.valueOf(hours));
                                tvmain.setText(arrayList.get(0).getMain());
                            }

                        }
                    });
                }
            }
        });
    }
    public ArrayList<WeatherList> parse(String url){
        ArrayList<WeatherList> arrayList=new ArrayList<>();
        try {

            JSONObject jsonObject=new JSONObject(url);
            String code=jsonObject.getString("cod");
            if (Integer.valueOf(code)==200)
            {JSONArray jsonArray=jsonObject.getJSONArray("weather");
            JSONObject jsonObject1=jsonArray.getJSONObject(0);
            String main=jsonObject1.getString("main");
            String icon=jsonObject1.getString("icon");
            JSONObject mainjson=jsonObject.getJSONObject("main");
            String Degree=mainjson.getString("temp");
            int temp=(int)(Double.valueOf(Degree)-273.15);
            WeatherList weatherList=new WeatherList(temp,main,icon);
            arrayList.add(weatherList);
            }
            else
            {
                final String message=jsonObject.getString("message");
              MainActivity.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                  }
              });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
    public void imagesetter(String imageUrl){

        image = new ImageView(getBaseContext());
        Picasso.get().load(imageUrl).centerCrop().resize(1024,768).into(image, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                linear.setBackground(image.getDrawable());
            }

            @Override
            public void onError(Exception e) {
                 //left here
                linear.setBackgroundResource(R.drawable.ic_not_interested_blue_grey);
                linear.setBackgroundColor(Color.rgb(61,59,59));
            }
        });
    }
    public ArrayList<tvSettingList> parseIt(String url)
    {
        ArrayList<tvSettingList> tvArray=new ArrayList<>();
        try {
            JSONObject jsonObject=new JSONObject(url);
            JSONObject coordJson=jsonObject.getJSONObject("coord");
            String lon=coordJson.getString("lon");

            String lat=coordJson.getString("lat");
            JSONArray weatherArray=jsonObject.getJSONArray("weather");
            JSONObject desjson=weatherArray.getJSONObject(0);

            String description=desjson.getString("description");
            JSONObject mainJson=jsonObject.getJSONObject("main");
            String pressure=mainJson.getString("pressure");
            String humidity=mainJson.getString("humidity");

            tvSettingList Add_Pressure=new tvSettingList("Pressure",Double.valueOf(pressure));
            tvSettingList Add_Humidity=new tvSettingList("Humidity",Double.valueOf(humidity));
            tvSettingList Add_Lat=new tvSettingList("Lattitude",Double.valueOf(lat));
            tvSettingList Add_lon=new tvSettingList("Longitude",Double.valueOf(lon));
       tvArray.add(Add_Humidity);
            tvArray.add(Add_Lat);
            tvArray.add(Add_lon);
            tvArray.add(Add_Pressure);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  tvArray;
    }
    public ArrayList<tvSettingList> getInfo(String url) {
        ArrayList<tvSettingList> tvArrayList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(url);
            JSONObject mainJson = jsonObject.getJSONObject("main");
            String temp_min = mainJson.getString("temp_min");
            String temp_max = mainJson.getString("temp_max");
           if(mainJson.length()==5)
           {  String visibility = jsonObject.getString("visibility");
               tvSettingList Add_visibility = new tvSettingList("Visibility", Double.valueOf(visibility));
               tvArrayList.add(Add_visibility);
           }
           else
           {
               String sea_level=mainJson.getString("sea_level");
               String grnd_level=mainJson.getString("grnd_level");
               tvSettingList Add_sea_level = new tvSettingList("Sea Level", Double.valueOf(sea_level));
               tvSettingList add_grnd_level = new tvSettingList("Grnd Level", Double.valueOf(grnd_level));
               tvArrayList.add(Add_sea_level);
               tvArrayList.add(add_grnd_level);
           }
            JSONObject windJson = jsonObject.getJSONObject("wind");
            String windSpeed = windJson.getString("speed");
            tvSettingList Add_temp_min = new tvSettingList("Min. Temp.", Double.valueOf(temp_min));
            tvSettingList Add_temp_max = new tvSettingList("Max. Temp.", Double.valueOf(temp_max));
            tvSettingList Add_windSpeed = new tvSettingList("Wind Speed", Double.valueOf(windSpeed));
            tvArrayList.add(Add_temp_max);
            tvArrayList.add(Add_temp_min);
            tvArrayList.add(Add_windSpeed);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tvArrayList;
    }

    @Override
    protected void onStop() {
        CityList cityList=new CityList();
        cityList.setCity(city);
        cityTable.Update(db,cityList);
        super.onStop();

    }
}

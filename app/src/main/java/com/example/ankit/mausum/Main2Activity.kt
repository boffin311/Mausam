package com.example.ankit.mausum

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.ankit.mausum.database.CityList
import com.example.ankit.mausum.database.MyDatabaseHelper
import com.example.ankit.mausum.database.cityTable
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class Main2Activity : AppCompatActivity() {
    private var db:SQLiteDatabase?=null
      var okhttpCalls:OkhttpCalls?=null
    var city:String?="India"
    val TAG="CHK"
    var tempCity:String? ="Delhi"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var myDatabaseHelper = MyDatabaseHelper(this)
        db = myDatabaseHelper.writableDatabase
        setSupportActionBar(toolbar)
        var arrayList = cityTable.readCity(db)
        if (arrayList.size == 0) {
            cityTable.insertCity(db)
            city = cityTable.readCity(db).get(0).city
        } else
            city = arrayList.get(0).city
        makeAcall("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=2bfddb3d0f0062143837f34fdf64c723");
        okhttpCalls=OkhttpCalls(baseContext, rvFuture)
        okhttpCalls!!.Call("https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=2bfddb3d0f0062143837f34fdf64c723");
        btnRetry.setOnClickListener {
            makeAcall("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=2bfddb3d0f0062143837f34fdf64c723");
            okhttpCalls=OkhttpCalls(baseContext, rvFuture)

            okhttpCalls!!.Call("https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=2bfddb3d0f0062143837f34fdf64c723");
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                progress.visibility = View.VISIBLE
                tempCity = query
                Log.d(TAG, "onQueryTextSubmit: $query")
                makeAcall("https://api.openweathermap.org/data/2.5/weather?q=" + query + "&appid=2bfddb3d0f0062143837f34fdf64c723")
                okhttpCalls=OkhttpCalls(baseContext, rvFuture)
                okhttpCalls!!.Call("https://api.openweathermap.org/data/2.5/forecast?q=" + query + "&appid=2bfddb3d0f0062143837f34fdf64c723")
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
        fun  makeAcall(url:String):Unit {
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                    .url(url)
                    .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call?, e: IOException?) {
                    this@Main2Activity.runOnUiThread {
                        initialLinear.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                        progress.visibility = View.INVISIBLE
                    }

                }

                override fun onResponse(call: Call?, response: Response?) {
                    var string = response!!.body()!!.string()
                    var arrayList = parse(string)
                    var tvDetaiArray = parseIt(string)
                    val tvArrayList = getInfo(string)
                    if (response.isSuccessful()) {
                        this@Main2Activity.runOnUiThread {
                            initialLinear.layoutParams = LinearLayout.LayoutParams(0, 0)
                            progress.visibility = View.INVISIBLE
                            var detailAdapter = DetailAdapter(tvDetaiArray)
                            var adapter = DetailAdapter(tvArrayList)
//                            with(rvfutureWeather) {
                                rvfutureWeather.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
                                rvfutureWeather.adapter = detailAdapter
//                            }

                            rvfutureWeather2.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
                            rvfutureWeather2.adapter = adapter

                            var hours = Calendar.getInstance()[Calendar.HOUR]
                            var minutes = Calendar.getInstance().get(Calendar.MINUTE)
                            var date = Calendar.getInstance().get(Calendar.DATE)
                            var month = Calendar.getInstance().get(Calendar.MONTH)
                            when (month) {
                                0 -> tvMonth.text = "Jan"
                                1 -> tvMonth.text = "Feb"
                                2 -> tvMonth.text = "Mar"
                                3 -> tvMonth.text = "Apr"
                                4 -> tvMonth.text = "May"
                                5 -> tvMonth.text = "Jun"
                                6 -> tvMonth.text = "Jul"
                                7 -> tvMonth.text = "Aug"
                                8 -> tvMonth.text = "Sept"
                                9 -> tvMonth.text = "Oct"
                                10 -> tvMonth.text = "Nov"
                                11 -> tvMonth.text = "Dec"
                            }
                            val Day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                            when (Day) {
                                1 -> tvDay.text = "Sunday"
                                2 -> tvDay.text = "Monday"
                                3 -> tvDay.text = "Tuesday"
                                4 -> tvDay.text = "Wednesday"
                                5 -> tvDay.text = "Thursday"
                                6 -> tvDay.text = "Friday"
                                7 -> tvDay.text = "Saturday"
                            }
                            if (arrayList.size != 0) {
                                if (tempCity != null) {
                                    city = tempCity
                                    Log.d(TAG, "run: $city")
                                }
                                when (arrayList.get(0).getIcon()) {
                                    "01d" -> {
                                        imageWeather.setImageResource(R.drawable.one_d)
                                        imagesetter("https://www.flowerpicturegallery.com/d/602-1/sunflower+on+sunny+day.jpg")
                                        appbar.setBackgroundColor(Color.rgb(13, 123, 178))
                                        lastLinear.setBackgroundColor(Color.argb(255, 238, 151, 26))
                                    }
                                    "01n" -> {
                                        imageWeather.setImageResource(R.drawable.one_n)
                                        imagesetter("https://ae01.alicdn.com/kf/HTB15shWlCYH8KJjSspdq6ARgVXag/Moon-lake-mountains-cold-night-nature-scenery-poster-silk-fabric-cloth-print-wall-sticker-Wall-Decor.jpg_640x640.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 9, 77, 126))
                                        appbar.setBackgroundColor(Color.rgb(23, 104, 165))
                                    }
                                    "02d" -> {
                                        imageWeather.setImageResource(R.drawable.two_d)
                                        imagesetter("https://wallpaper-house.com/data/out/12/wallpaper2you_541439.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 80, 35, 5))
                                        appbar.setBackgroundColor(Color.rgb(69, 148, 202))
                                    }
                                    "02n" -> {
                                        imageWeather.setImageResource(R.drawable.two_n)
                                        imagesetter("https://www.goabase.net/pic/20170805_luna-rave-experience_20170315055734.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 66, 67, 172))
                                        appbar.setBackgroundColor(Color.rgb(40, 26, 39))
                                    }
                                    "03d" -> {
                                        imageWeather.setImageResource(R.drawable.three_d)
                                        imagesetter("https://d2v9y0dukr6mq2.cloudfront.net/video/thumbnail/LAp2L-s/scattered-cloud-sky-lapse_nyvsayafx__F0000.png")
                                        lastLinear.setBackgroundColor(Color.argb(255, 79, 118, 175))
                                        appbar.setBackgroundColor(Color.rgb(10, 29, 98))
                                    }
                                    "03n" -> {
                                        imageWeather.setImageResource(R.drawable.three_n)
                                        imagesetter("http://www.megacidade.com/images/noticias/5115/1644550362.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 75, 88, 122))
                                        appbar.setBackgroundColor(Color.rgb(104, 122, 165))
                                    }
                                    "04d" -> {
                                        imageWeather.setImageResource(R.drawable.three_d)
                                        imagesetter("https://img00.deviantart.net/6364/i/2017/030/4/1/broken_clouds_by_kevintheman-dax9bd4.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 85, 144, 207))
                                        appbar.setBackgroundColor(Color.rgb(16, 47, 94))
                                    }
                                    "04n" -> {
                                        imageWeather.setImageResource(R.drawable.three_n)
                                        imagesetter("http://s3.thingpic.com/images/4U/JKaaHQg8ot7bjGc26EeiESfs.jpeg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 9, 77, 126))
                                    }
                                    "09d" -> {
                                        imageWeather.setImageResource(R.drawable.nine_d)
                                        imagesetter("https://cdn1.bbend.net/media/com_news/story/2014/11/13/521218/main/Raineck.jpg")
                                        appbar.setBackgroundColor(Color.rgb(147, 152, 38))

                                        lastLinear.setBackgroundColor(Color.argb(255, 120, 121, 124))
                                    }
                                    "09n" -> {
                                        imageWeather.setImageResource(R.drawable.nine_d)
                                        imagesetter("https://photo-cdn.urdupoint.com/media/2017/04/_3/730x425/pic_1491298911.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 80, 96, 121))
                                        appbar.setBackgroundColor(Color.rgb(1, 11, 35))
                                    }
                                    "10d" -> {
                                        imageWeather.setImageResource(R.drawable.ten_d)
                                        imagesetter("https://sciencetrends-techmakaillc.netdna-ssl.com/wp-content/uploads/2017/10/rainbow.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 119, 82, 40))
                                        appbar.setBackgroundColor(Color.rgb(147, 176, 214))
                                    }
                                    "10n" -> {
                                        imageWeather.setImageResource(R.drawable.ten_n)
                                        imagesetter("https://www.wallpaperup.com/uploads/wallpapers/2013/10/20/163403/90bd6de711bd0d52ce0939064a388963-700.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 56, 95, 137))
                                        appbar.setBackgroundColor(Color.rgb(1, 11, 39))
                                    }
                                    "11d" -> {
                                        imageWeather.setImageResource(R.drawable.eleven_d)
                                        imagesetter("https://okcthunderbasketball.files.wordpress.com/2008/08/thunder.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 47, 53, 84))
                                        appbar.setBackgroundColor(Color.rgb(58, 64, 110))
                                    }
                                    "11n" -> {
                                        imageWeather.setImageResource(R.drawable.eleven_n)
                                        imagesetter("https://www.thoughtco.com/thmb/35HKmrimexItlqC0a1-vnimJe94=/768x0/filters:no_upscale():max_bytes(150000):strip_icc()/92173281-56a2acf75f9b58b7d0cd4cee.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 54, 52, 92))
                                        appbar.setBackgroundColor(Color.rgb(110, 74, 117))
                                    }
                                    "13d" -> {
                                        imageWeather.setImageResource(R.drawable.thirteen_d)
                                        imagesetter("http://www.trbimg.com/img-5aa059f5/turbine/bs-md-weather-20180305")
                                        lastLinear.setBackgroundColor(Color.argb(255, 139, 145, 155))
                                        appbar.setBackgroundColor(Color.rgb(180, 185, 193))
                                    }
                                    "13n" -> {
                                        imageWeather.setImageResource(R.drawable.thirteen_n)
                                        imagesetter("https://fournews-assets-prod-s3b-ew1-aws-c4-pml.s3.amazonaws.com/media/2017/12/snow_london_g_hd.jpg")
                                        lastLinear.setBackgroundColor(Color.argb(255, 109, 97, 193))
                                        appbar.setBackgroundColor(Color.rgb(112, 108, 213))
                                    }
                                    "50d" -> {
                                        imageWeather.setImageResource(R.drawable.fifty_d)
                                        imagesetter("https://bloximages.chicago2.vip.townnews.com/host.madison.com/content/tncms/assets/v3/editorial/d/47/d4757e35-03ee-504c-a42a-fa738f0e0f72/582269020f39e.image.jpg?resize=1200%2C803")
                                        lastLinear.setBackgroundColor(Color.argb(255, 207, 137, 69))
                                        appbar.setBackgroundColor(Color.rgb(161, 119, 72))
                                    }
                                    "50n" -> {
                                        imageWeather.setImageResource(R.drawable.fifty_n)
                                        imagesetter("https://bloximages.chicago2.vip.townnews.com/host.madison.com/content/tncms/assets/v3/editorial/d/47/d4757e35-03ee-504c-a42a-fa738f0e0f72/582269020f39e.image.jpg?resize=1200%2C803")
                                        lastLinear.setBackgroundColor(Color.argb(255, 207, 137, 69))
                                        appbar.setBackgroundColor(Color.rgb(161, 119, 72))
                                    }
                                }//  appbar.setBackgroundColor(Color.rgb());
                            }
                        }
                    }
                }
            })
        }
    fun  parse(url:String):ArrayList<WeatherList>{
        var arrayList= ArrayList<WeatherList>()
        try {
            var jsonObject= JSONObject(url)
            var code=jsonObject.getString("cod")
            if (code.toInt()==200)
            {var jsonArray=jsonObject.getJSONArray("weather")
                var jsonObject1=jsonArray.getJSONObject(0)
                var main=jsonObject1.getString("main");
                var icon=jsonObject1.getString("icon");
                var mainjson=jsonObject.getJSONObject("main");
                var Degree=mainjson.getString("temp");
                var temp=(Degree.toDouble()-273.15).toInt()
                var weatherList=WeatherList(temp,main,icon)
                arrayList.add(weatherList);
            }
            else
            {
                val message=jsonObject.getString("message");
                this@Main2Activity.runOnUiThread {
                        Toast.makeText(this@Main2Activity, message, Toast.LENGTH_SHORT).show()

                    }
                }

        } catch ( e:JSONException) {
            e.printStackTrace()
        }
        return arrayList;
    }
                fun imagesetter(imageUrl: String) {

                    var image = ImageView(baseContext)
                    Picasso.get().load(imageUrl).centerCrop().resize(1024, 768).into(image, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            linear.background = image.getDrawable()
                        }

                        override fun onError(e: Exception) {
                            //left here
                            linear.setBackgroundResource(R.drawable.ic_not_interested_blue_grey)
                            linear.setBackgroundColor(Color.rgb(61, 59, 59))
                        }
                    })
                }

    fun parseIt(url: String): ArrayList<tvSettingList> {
        val tvArray = ArrayList<tvSettingList>()
        try {
            val jsonObject = JSONObject(url)
            val coordJson = jsonObject.getJSONObject("coord")
            val lon = coordJson.getString("lon")

            val lat = coordJson.getString("lat")
            val weatherArray = jsonObject.getJSONArray("weather")
            val desjson = weatherArray.getJSONObject(0)

            val description = desjson.getString("description")
            val mainJson = jsonObject.getJSONObject("main")
            val pressure = mainJson.getString("pressure")
            val humidity = mainJson.getString("humidity")

            val Add_Pressure = tvSettingList("Pressure", java.lang.Double.valueOf(pressure))
            val Add_Humidity = tvSettingList("Humidity", java.lang.Double.valueOf(humidity))
            val Add_Lat = tvSettingList("Lattitude", java.lang.Double.valueOf(lat))
            val Add_lon = tvSettingList("Longitude", java.lang.Double.valueOf(lon))
            tvArray.add(Add_Humidity)
            tvArray.add(Add_Lat)
            tvArray.add(Add_lon)
            tvArray.add(Add_Pressure)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return tvArray
    }

    fun getInfo(url: String): ArrayList<tvSettingList> {
        val tvArrayList = ArrayList<tvSettingList>()

        try {
            val jsonObject = JSONObject(url)
            val mainJson = jsonObject.getJSONObject("main")
            val temp_min = mainJson.getString("temp_min")
            val temp_max = mainJson.getString("temp_max")
            if (mainJson.length() == 5) {
                val visibility = jsonObject.getString("visibility")
                val Add_visibility = tvSettingList("Visibility", java.lang.Double.valueOf(visibility))
                tvArrayList.add(Add_visibility)
            } else {
                val sea_level = mainJson.getString("sea_level")
                val grnd_level = mainJson.getString("grnd_level")
                val Add_sea_level = tvSettingList("Sea Level", java.lang.Double.valueOf(sea_level))
                val add_grnd_level = tvSettingList("Grnd Level", java.lang.Double.valueOf(grnd_level))
                tvArrayList.add(Add_sea_level)
                tvArrayList.add(add_grnd_level)
            }
            val windJson = jsonObject.getJSONObject("wind")
            val windSpeed = windJson.getString("speed")
            val Add_temp_min = tvSettingList("Min. Temp.", java.lang.Double.valueOf(temp_min))
            val Add_temp_max = tvSettingList("Max. Temp.", java.lang.Double.valueOf(temp_max))
            val Add_windSpeed = tvSettingList("Wind Speed", java.lang.Double.valueOf(windSpeed))
            tvArrayList.add(Add_temp_max)
            tvArrayList.add(Add_temp_min)
            tvArrayList.add(Add_windSpeed)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return tvArrayList
    }

    override fun onStop() {
        var cityList= CityList()
        cityList.city=city;
        cityTable.Update(db,cityList)
        super.onStop()
    }
}

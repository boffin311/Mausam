package com.example.ankit.mausum.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class cityTable {
    private static final String TAG = "cityTable";
    public static final String TABLE_NAME="CityName";
    public static final String City="city";
    public static final String[] ALL_Column={City};
    public static final String CREATETABLE=
            "Create table if not exists "+TABLE_NAME+"(city text);";
    public  static void insertCity(SQLiteDatabase db)
    {
        ContentValues row=new ContentValues();
        row.put(City,"India");
        db.insert(TABLE_NAME,null,row);
        Log.d(TAG, "insertCity: ");
    }
    public  static ArrayList<CityList> readCity(SQLiteDatabase db)
    {     ArrayList<CityList> arrayList=new ArrayList<>();
          Cursor query=db.query(TABLE_NAME,ALL_Column,null,null,null,null,null);
        while(query.moveToNext())
        {
            CityList cityList=new CityList();
            cityList.setCity(query.getString(0));
            arrayList.add(cityList);
            Log.d(TAG, "readCity: "+arrayList.size());
        }
    return arrayList;
    }
    public static void Update(SQLiteDatabase db,CityList cityList)
    {
        ContentValues values=new ContentValues();
        values.put(City,cityList.getCity());
        Log.d(TAG, "Update: ");
        db.update(TABLE_NAME,values,null,null);
    }
}

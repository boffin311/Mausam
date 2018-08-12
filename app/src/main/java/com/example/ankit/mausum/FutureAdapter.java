package com.example.ankit.mausum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FutureAdapter extends RecyclerView.Adapter<FutureAdapter.MyHolder> {
  ArrayList<WeatherList> arrayList;
    private static final String TAG = "hemu";
    public FutureAdapter(ArrayList<WeatherList> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li= (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View view=li.inflate(R.layout.future,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
WeatherList weatherList=arrayList.get(position);
String realTime=weatherList.getTime().substring(11,13);
holder.tvDifTemp.setText(String.valueOf(weatherList.getDegree()));
switch (realTime)
{
    case "03":holder.tvTime.setText("3");holder.tvTime1.setText(" AM");break;
    case "06":holder.tvTime.setText("6");holder.tvTime1.setText(" AM");break;
    case "09":holder.tvTime.setText("9");holder.tvTime1.setText(" AM");break;
    case "12":holder.tvTime.setText("12");holder.tvTime1.setText(" PM");break;
    case "00":holder.tvTime.setText("0");holder.tvTime1.setText(" AM");break;
    case "15":holder.tvTime.setText("3");holder.tvTime1.setText(" PM");break;
    case "18":holder.tvTime.setText("6");holder.tvTime1.setText(" PM");break;
    case "21":holder.tvTime.setText("9");holder.tvTime1.setText(" PM");break;

}

        switch (weatherList.getIcon())
              {
                  case "01d":holder.imageFuture.setImageResource(R.drawable.one_d);
                  break;
                  case "01n":holder.imageFuture.setImageResource(R.drawable.one_n);
                        break;
                  case "02d":holder.imageFuture.setImageResource(R.drawable.two_d);

                      break;
                  case "02n":holder.imageFuture.setImageResource(R.drawable.two_n);

                      break;
                  case "03d":holder.imageFuture.setImageResource(R.drawable.three_d);

                      break;
                  case "03n":holder.imageFuture.setImageResource(R.drawable.three_n);

                      break;
                  case "04d":holder.imageFuture.setImageResource(R.drawable.three_d);

                      break;
                  case "04n":holder.imageFuture.setImageResource(R.drawable.three_n);

                      break;
                  case "09d":holder.imageFuture.setImageResource(R.drawable.nine_d);
                       break;
                  case "09n":holder.imageFuture.setImageResource(R.drawable.nine_d);

                      break;
                  case "10d":holder.imageFuture.setImageResource(R.drawable.ten_d);

                      break;
                  case "10n":holder.imageFuture.setImageResource(R.drawable.ten_n);

                      break;
                  case "11d":holder.imageFuture.setImageResource(R.drawable.eleven_d);

                      break;
                  case "11n":holder.imageFuture.setImageResource(R.drawable.eleven_n);

                      break;
                  case "13d":holder.imageFuture.setImageResource(R.drawable.thirteen_d);

                      break;
                  case "13n":holder.imageFuture.setImageResource(R.drawable.thirteen_n);

                      break;
                  case "50d":holder.imageFuture.setImageResource(R.drawable.fifty_d);

                      break;
                  case "50n":holder.imageFuture.setImageResource(R.drawable.fifty_n);

                      break;
              }
holder.tvEnviro.setText(weatherList.getMain());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
      TextView tvEnviro,tvTime,tvTime1,tvDifTemp;
      ImageView imageFuture;
        public MyHolder(View itemView) {
            super(itemView);
       tvDifTemp=itemView.findViewById(R.id.tvDifTemp);
            tvEnviro=itemView.findViewById(R.id.tvEnviro);
       tvTime=itemView.findViewById(R.id.tvTime);
       tvTime1=itemView.findViewById(R.id.tvTime1);
       imageFuture=itemView.findViewById(R.id.imageFuture);
        }
    }
}

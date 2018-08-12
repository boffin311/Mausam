package com.example.ankit.mausum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.Myholder> {
    ArrayList<tvSettingList> arrayList;

    public DetailAdapter(ArrayList<tvSettingList> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li= (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new Myholder(li.inflate(R.layout.detail,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
     holder.tvMainName.setText(arrayList.get(position).getTopic());
     holder.tvSubMainName.setText(String.valueOf(arrayList.get(position).getValue()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
    TextView tvMainName,tvSubMainName;
        public Myholder(View itemView) {
        super(itemView);
        tvMainName=itemView.findViewById(R.id.tvMainName);
        tvSubMainName=itemView.findViewById(R.id.tvsubMainName);
    }
}
}

package com.example.aplicacion.ViewHolders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;
import com.example.aplicacion.cal;

import java.util.ArrayList;

public class AdapterCal extends RecyclerView.Adapter<AdapterCal.MyViewHolder> {

    Context context;
    ArrayList<cal> list;

    public AdapterCal(Context context, ArrayList<cal> list)
    {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_cal,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        cal c = list.get(position);
        holder.texto.setText(c.getData());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView texto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.tc);
        }
    }
}

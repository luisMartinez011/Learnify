package com.example.aplicacion.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NChatInfoHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.img_avatar)
    public ImageView img_avatar;

    @BindView(R.id.txt_name)
    public TextView txt_name;

    @BindView(R.id.txt_last_message)
    public TextView txt_last_message;

    @BindView(R.id.ntxt_time)
    public TextView txt_time;



    Unbinder unbinder;



    public NChatInfoHolder(@NonNull View itemView) {
        super(itemView);
        unbinder= ButterKnife.bind(this,itemView);
    }
}

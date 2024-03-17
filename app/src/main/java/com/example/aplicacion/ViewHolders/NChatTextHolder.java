package com.example.aplicacion.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NChatTextHolder extends RecyclerView.ViewHolder{
    private Unbinder unbinder;

    @BindView(R.id.ntxt_time)
    public TextView ntxt_time;

    @BindView(R.id.ntxt_chat_message)
    public TextView ntxt_chat_message;

    public NChatTextHolder(@NonNull View itemView) {
        super(itemView);
        unbinder = ButterKnife.bind(this,itemView);

    }





}

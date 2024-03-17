package com.example.aplicacion.ViewHolders;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicacion.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
//NO LO OCUPE AL FINAL
public class CalenInfoHolder extends RecyclerView.ViewHolder{
    Unbinder unbinder;


    @BindView(R.id.tc)
    public TextView llamado;







    public CalenInfoHolder(@NonNull View itemView) {
        super(itemView);

        unbinder= ButterKnife.bind(this,itemView);
    }
}

package com.example.aplicacion.Fragments;

import static com.example.aplicacion.SettingActivity.btn;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.aplicacion.MainActivity;
import com.example.aplicacion.R;

import com.example.aplicacion.RegistrarseActivity;
import com.example.aplicacion.SettingActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ConfigFragment extends Fragment  {
    @BindView(R.id.recycler_people)
    RecyclerView recycler_people;










    private ConfigViewModel mViewModel;

    static ConfigFragment instance;

    public static ConfigFragment getInstance() {
        return instance == null ? new ConfigFragment():instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView =  inflater.inflate(R.layout.activity_setting, container, false);

        itemView.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() { // Identifica el boton de logout y lo manda al menu principal
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getContext(),MainActivity.class);
                startActivity(i);
            }
        });



        return itemView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ConfigViewModel.class);
        // TODO: Use the ViewModel
    }



}
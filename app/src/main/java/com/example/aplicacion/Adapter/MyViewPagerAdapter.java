package com.example.aplicacion.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.aplicacion.Fragments.CalendarFragment;
import com.example.aplicacion.Fragments.ChatFragment;
import com.example.aplicacion.Fragments.ConfigFragment;
import com.example.aplicacion.Fragments.PeopleFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public MyViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) { //Se supone que agarra los datos dependiendo de la seccion que selecciones
        if(position ==0)//Carga los fragmentos en las posiciones que se encuentran en la carpeta fragments
        {
            return ChatFragment.getInstance();
        }
        else if(position==1)
        {
            return PeopleFragment.getInstance();
        }
        else if(position==2)
        {
            return CalendarFragment.getInstance();
        }
        else
        {
            return ConfigFragment.getInstance();
        }
    }

    @Override
    public int getItemCount() { //numero de secciones a contar
        return 4;
    }
}

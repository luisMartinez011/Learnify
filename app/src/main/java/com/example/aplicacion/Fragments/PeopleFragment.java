package com.example.aplicacion.Fragments;

import static com.example.aplicacion.Common.Common.chatUser;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.strictmode.UnbufferedIoViolation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.aplicacion.ChatActivity;
import com.example.aplicacion.R;
import com.example.aplicacion.Users;
import com.example.aplicacion.ViewHolders.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.BuildConfig;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import com.amulyakhare.*;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class PeopleFragment extends Fragment {

    @BindView(R.id.recycler_people)
    RecyclerView recycler_people;
FirebaseRecyclerAdapter adapter;
    private Unbinder unbinder;

    //public static Users chatUser = new Users();


    private PeopleViewModel mViewModel;

    static PeopleFragment instance;

    public static PeopleFragment getInstance() {
        return instance == null ? new PeopleFragment():instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.people_fragment, container, false);
        initView(itemView);
        loadPeople();
        return itemView;

    }

    private void loadPeople() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Usuarios");//carga a todas las personas en una lista
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().setQuery(query,Users.class).build();
        adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options){


            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_people,parent,false);
                return new UserViewHolder(view );
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {
                if(!adapter.getRef(position).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                {
                    //Hide Yourself
                    ColorGenerator  generator = ColorGenerator.MATERIAL; //estos 5 renglones son para la imagen con la letra de los usuarios
                    int color= generator.getColor(FirebaseAuth.getInstance().getCurrentUser().getUid());//
                    TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();//
                    TextDrawable drawable = builder.build(model.getNombre().substring(0,1),color);//
                    holder.img_avatar.setImageDrawable(drawable);//


                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(model.getNombre()).append(" ").append(model.getApellido());

                    holder.txt_name.setText(stringBuilder.toString());

                    holder.txt_bio.setText(model.getBio());

                    //Event
                    holder.itemView.setOnClickListener(view -> {//carga el chat aqui esta el error
                        chatUser= model;
                        chatUser.setUid(adapter.getRef(position).getKey());
                        startActivity(new Intent(getContext(), ChatActivity.class));

                    });

                }
                else
                {
                   holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }
            }
        };
        adapter.startListening();
        recycler_people.setAdapter(adapter);
    }




    private void initView(View itemView) {
        unbinder = ButterKnife.bind(this,itemView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_people.setLayoutManager(layoutManager);
        recycler_people.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));


    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PeopleViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(adapter !=null) adapter.startListening();
    }

    @Override
    public void onStop() {
        if(adapter != null) adapter.stopListening();
        super.onStop();
    }
}
package com.example.aplicacion.Fragments;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import static com.example.aplicacion.Common.Common.CHAT_LIST_REFERENCE;
import static com.example.aplicacion.Common.Common.calt;
import static com.example.aplicacion.Common.Common.chatUser;
import static com.example.aplicacion.Common.Common.users;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.aplicacion.ChatActivity;
import com.example.aplicacion.ChatInfoModel;
import com.example.aplicacion.Common.Common;
import com.example.aplicacion.MainActivity;
import com.example.aplicacion.R;
import com.example.aplicacion.Users;
import com.example.aplicacion.ViewHolders.AdapterCal;
import com.example.aplicacion.ViewHolders.CalenInfoHolder;
import com.example.aplicacion.ViewHolders.ChatInfoHolder;
import com.example.aplicacion.ViewHolders.PlanAdapter;
import com.example.aplicacion.ViewHolders.UserViewHolder;
import com.example.aplicacion.cal;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CalendarFragment extends Fragment {

    @BindView(R.id.recycler_cal)
    RecyclerView recycler_people;

    FirebaseRecyclerAdapter adapter;
    private Unbinder unbinder;
    private CalendarView calendarView;
    private EditText editText;

    private Button boton;
    private PlanAdapter planAdapter;
    private String stringDateSelected;
    private CalendarViewModel mViewModel;

    private EditText chatg;

    private DatabaseReference databaseReference;
    private DatabaseReference gdatabaseReference;

    private String chatText, texto;
    ArrayList<cal> list;

    ArrayList<cal> glist;
    static CalendarFragment instance;
    public static CalendarFragment getInstance() {
        return instance == null ? new CalendarFragment():instance;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView =  inflater.inflate(R.layout.fragment_calendar, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar").child(users.getUid()) ;
        gdatabaseReference=FirebaseDatabase.getInstance().getReference("Calendar").child("Grupos") ;
        calendarView= itemView.findViewById(R.id.calendarView) ; // Identifica y lo manda al menu principal
        editText = itemView.findViewById(R.id.editText);
        chatg= itemView.findViewById(R.id.group);


       // initView(itemView);
        unbinder = ButterKnife.bind(this,itemView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_people.setLayoutManager(layoutManager);
        planAdapter=new PlanAdapter();
        recycler_people.setAdapter(planAdapter);




        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                i1=i1+1;
                stringDateSelected = Integer.toString(i2)+ "-"+ Integer.toString(i1)+"-"+Integer.toString(i);
                calendarClicked();



            }
        });


        itemView.findViewById(R.id.botonp).setOnClickListener(new View.OnClickListener() { // Identifica el boton de logout y lo manda al menu principal
            @Override
            public void onClick(View view) {

                chatText = chatg.getText().toString();
                texto=editText.getText().toString();
                System.out.println(chatText);
                System.out.println(texto);

                if(chatText.isEmpty() && !texto.isEmpty())
                {
                    String key = databaseReference.child(stringDateSelected).push().getKey();
                    databaseReference.child(stringDateSelected).child(key).setValue(texto) ;//no se mueve

                    editText.setText("");

                    Toast.makeText(getContext(), "Enviado:,\n"+stringDateSelected, Toast.LENGTH_SHORT).show();
                }
                else if(!chatText.isEmpty() && !texto.isEmpty())
                {
                    String key=gdatabaseReference.child(chatText).child(stringDateSelected).push().getKey();
                    gdatabaseReference.child(chatText).child(stringDateSelected).child(key).setValue(texto);

                    editText.setText("");

                    Toast.makeText(getContext(), "Enviado a grupo :,\n"+stringDateSelected, Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                }






            }
        });


        return itemView;
    }








    private void calendarClicked()
    {
        if((!chatg.getText().toString().isEmpty()    ))
        {
            gdatabaseReference.child(chatg.getText().toString()).child(stringDateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue()!= null)
                    {
                        planAdapter.clearPlans();

                        editText.setText("");

                        DataSnapshot fechaSnapshot = snapshot;
                        for (DataSnapshot planSnapshot : fechaSnapshot.getChildren())
                        {
                            String plan = planSnapshot.getValue(String.class);
                            cal newPlan = new cal();
                            newPlan.setData(plan);
                            planAdapter.addPlan(newPlan);
                        }




                        if (adapter!=null)
                        {
                            adapter.startListening();
                            recycler_people.setAdapter(adapter);
                        }




                    }
                    else
                    {
                        planAdapter.clearPlans();
                        editText.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        else if((chatg.getText().toString().isEmpty()     ))
        {
            databaseReference.child(stringDateSelected).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue()!= null)
                    {
                        planAdapter.clearPlans();

                        editText.setText("");

                        DataSnapshot fechaSnapshot = snapshot;
                        for (DataSnapshot planSnapshot : fechaSnapshot.getChildren())
                        {
                            String plan = planSnapshot.getValue(String.class);
                            cal newPlan = new cal();
                            newPlan.setData(plan);
                            planAdapter.addPlan(newPlan);
                        }



                        if (adapter!=null)
                        {
                            adapter.startListening();
                            recycler_people.setAdapter(adapter);
                        }





                    }
                    else
                    {
                        planAdapter.clearPlans();
                        editText.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




    }

    // private void buttonSaveEvent()
    // {
    //     databaseReference.child(stringDateSelected).setValue(editText.getText().toString());
    //  }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        // TODO: Use the ViewModel
    }


    @Override
    public void onStart() {
        super.onStart();



        if(adapter!=null)
        {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        if(adapter != null) adapter.stopListening();
        super.onStop();
    }
}
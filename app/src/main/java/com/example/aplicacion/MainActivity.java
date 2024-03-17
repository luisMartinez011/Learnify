package com.example.aplicacion;

import static com.example.aplicacion.Common.Common.users;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG ="MainActivity";
    private EditText correo;
    private EditText contrasena;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;
    private FirebaseAuth mAuth;
    private String userID;
    Spinner spinner;
    Locale locale;
    String currentLanguage = "en", currentLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        correo=findViewById(R.id.correo);
        contrasena=findViewById(R.id.contrasena);
        mAuth = FirebaseAuth.getInstance();




        currentLanguage = getIntent().getStringExtra(currentLang);
        spinner = findViewById(R.id.spinner);
        List<String> list = new ArrayList<>();
        list.add("Selecciona un lenguaje");
        list.add("Español");
        list.add("Ingles");
        list.add("Frances");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        setLocale("es");
                        break;
                    case 2:
                        setLocale("en");
                        break;
                    case 3:
                        setLocale("fr");
                        break;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            locale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = locale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this,
                    MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        }
        else {
            Toast.makeText(MainActivity.this, "Ya fue seleccionado este lenguaje!", Toast.LENGTH_SHORT).show();
        }
    }
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }




    public void irRegistrarse(View view) {
        Intent i = new Intent(this, RegistrarseActivity.class);
        startActivity(i);
    }


    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    public void iniciarSesion(View view)
    {

        FirebaseUser user = mAuth.getCurrentUser();
        if(contrasena.length()<6)
        {
            Toast.makeText(getApplicationContext(), "La contraseña debe ser mayor a 6 digitos",
                    Toast.LENGTH_SHORT).show();
        }
        if(correo.length()==0 ||contrasena.length()==0)
        {
            Toast.makeText(getApplicationContext(), "Falta llenar los campos",
                    Toast.LENGTH_SHORT).show();
        }

        else
        {
            mAuth.signInWithEmailAndPassword(correo.getText().toString().trim(), contrasena.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            FirebaseUser user = mAuth.getCurrentUser();



                            if(user.isEmailVerified()) {
                                String tipo = task.getResult().getUser().getUid();

                                mDatabase = FirebaseDatabase.getInstance();

                                mDatabase.getReference("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.exists())
                                                        {
                                                            Users model = snapshot.getValue(Users.class);
                                                            model.setUid(snapshot.getKey());

                                                            users = model;
                                                            String usuariop="0";
                                                            String admin="1";


                                                            if(model.getUsertype().equals(usuariop))
                                                            {

                                                                Intent i= new Intent(getApplicationContext(), HomeActivity.class);
                                                                startActivity(i);
                                                                Toast.makeText(getApplicationContext(), "Bienvenido",
                                                                        Toast.LENGTH_SHORT).show();

                                                            }
                                                            if(model.getUsertype().equals(admin))
                                                            {
                                                                Intent i= new Intent(getApplicationContext(), AdminBoardActivity.class);
                                                                startActivity(i);
                                                                Toast.makeText(getApplicationContext(), "Bienvenido",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }


                                                        }


                                                    }


                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(MainActivity.this, "Este correo no existe", Toast.LENGTH_SHORT).show();

                                                    }
                                                });



                            }
                            else if(!user.isEmailVerified())
                            {
                                Toast.makeText(getApplicationContext(), "No se ha autenticado su correo",
                                        Toast.LENGTH_SHORT).show();

                            }



                            else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getApplicationContext(), "Error, su correo o contraseña es incorrecto.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        }

    }



}
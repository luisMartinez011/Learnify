package com.example.aplicacion;

import static android.content.ContentValues.TAG;

import static com.example.aplicacion.Common.Common.users;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRegistrar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrarseActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String apodo;
    private EditText correo;
    private EditText contrasena;
    private EditText contrasenaConfirmacion;

    private EditText usuario;

    private EditText nombre;

    private EditText apellido;

    private EditText bio;
    //17:23

    private static final String TAG ="RegistrarseActivity";
    private static final String USER = "Usuarios";  //user




    private String mail,con,tipo,nom,ape,us,bi;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;
    private String userId;
    private Users userr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);


        // apodo=findViewById(R.id.apodo);
        usuario=findViewById(R.id.usuario);
        nombre=findViewById(R.id.nombre);
        apellido=findViewById(R.id.apellido);
        bio=findViewById(R.id.bio);
        correo = findViewById(R.id.correo);
        contrasena= findViewById(R.id.contrasena) ;
        contrasenaConfirmacion=findViewById(R.id.contrasenaConfirmacion);


        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference(USER);
        mAuth = FirebaseAuth.getInstance();



    }






    boolean isEmail(EditText correo) {
        CharSequence email = correo.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }


    public void registrarUsuario(View view)
    {
        if(bio.getText().toString().isEmpty())
        {
            bio.setError("Introduce una biografia");
        }
        if(nombre.getText().toString().isEmpty())
        {
            nombre.setError("Introduce un nombre");
        }
        if(usuario.getText().toString().isEmpty())
        {
            usuario.setError("Introduce un usuario");
        }
        if(apellido.getText().toString().isEmpty())
        {
            apellido.setError("Introduce un apellido");
        }

        if(contrasenaConfirmacion.getText().toString().trim().isEmpty())
        {
            contrasenaConfirmacion.setError("Introduce de nuevo la contaseña");
        }

        if(contrasena.getText().toString().trim().isEmpty())
        {
            contrasena.setError("Introduce una contraseña");
        }

        if (isEmail(correo) == false) {
            correo.setError("Pon un email valido!");
        }

        if(contrasena.length()<6 || contrasenaConfirmacion.length()<6)
        {
            Toast.makeText(this, "La contraseña debe ser minimo de 6 digitos", Toast.LENGTH_SHORT).show() ;

        }

        if(contrasena.getText().toString().equals(contrasenaConfirmacion.getText().toString()  ) && !correo.getText().toString().trim().isEmpty() && !nombre.getText().toString().isEmpty() && !usuario.getText().toString().isEmpty() && !apellido.getText().toString().isEmpty() && !bio.getText().toString().isEmpty())
        {
            nom = nombre.getText().toString();
            ape= apellido.getText().toString();
            us= usuario.getText().toString().trim();
            mail = correo.getText().toString().trim();
            con = contrasena.getText().toString().trim();
            tipo= "0";
            bi=bio.getText().toString();



            mAuth.createUserWithEmailAndPassword(correo.getText().toString().trim(),contrasena.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {



                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid=task.getResult().getUser().getUid();
                                userr = new Users(uid,nom,ape,us,bi,mail,con,tipo);

                                updateUI(user,uid);





                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(getApplicationContext(), "Registro exitoso.Verifica tu email", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });



                                //updateUI(user);



                                if (!user.sendEmailVerification().isSuccessful()) {
                                    task.getResult().getUser().sendEmailVerification();

                                }

                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);

                            }

                            if (!task.isSuccessful()) {
                                Log.d(TAG, "onComplete: Failed=" + task.getException().getMessage()); //ADD THIS
                                Toast.makeText( getApplicationContext(), "Hubo un error al crear usuario.",Toast.LENGTH_SHORT).show();


                            }




                        }
                    });


        }


        else
        {
            Toast.makeText(this, "Las contraseñas no coinciden  ", Toast.LENGTH_SHORT).show() ;

        }



    }

    public void updateUI(FirebaseUser currentUser, String userId)
    {

        mDbRef.child(userId).setValue(userr);




    }




}
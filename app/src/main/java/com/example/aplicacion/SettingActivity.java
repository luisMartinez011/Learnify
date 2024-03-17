package com.example.aplicacion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.MissingFormatArgumentException;

import butterknife.BindView;


public class SettingActivity extends AppCompatActivity {

public static Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btn = (Button) findViewById(R.id.logout);







    }

    private void openActivity(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void irCerrar(View view) {
        Intent i = new Intent(this, RegistrarseActivity.class);
        startActivity(i);
    }


}
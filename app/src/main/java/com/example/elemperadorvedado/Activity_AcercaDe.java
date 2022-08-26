package com.example.elemperadorvedado;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class Activity_AcercaDe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);
        try{


            //Toolbar
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.acerca_de_appBar);
            Toolbar toolbar = (Toolbar) findViewById(R.id.acerca_de_toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });




        }catch (Exception e){
            Toast.makeText(Activity_AcercaDe.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    public void acerca_de_click_licencia(View view) {
        LayoutInflater inflater=LayoutInflater.from(Activity_AcercaDe.this);
        View vista= inflater.inflate(R.layout.li_licencia, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(Activity_AcercaDe.this);
        builder.setView(vista);
        final AlertDialog alertDialog=builder.create();

        //Declaraciones
        ImageView imageView_cerrar=(ImageView)vista.findViewById(R.id.li_licencia_cerrar);

        //Llenado


        //Listener
        imageView_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        //Finalizado
        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

    public void acerca_de_click_privacidad(View view) {
        LayoutInflater inflater=LayoutInflater.from(Activity_AcercaDe.this);
        View vista= inflater.inflate(R.layout.li_privacidad, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(Activity_AcercaDe.this);
        builder.setView(vista);
        final AlertDialog alertDialog=builder.create();

        //Declaraciones
        ImageView imageView_cerrar=(ImageView)vista.findViewById(R.id.li_privacidad_cerrar);

        //Llenado


        //Listener
        imageView_cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        //Finalizado
        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

}
package com.example.elemperadorvedado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import java.util.Objects;

public class Activity_EmpleadoJefe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleado_jefe);
        try {


            //Toolbar
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.empleado_jefe_appBar);
            Toolbar toolbar = (Toolbar) findViewById(R.id.empleado_jefe_toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            String jefe_empleado_choice=getIntent().getStringExtra("jefe_empleado_choice");
            CardView cardView =(CardView) findViewById(R.id.empleado_jefe_cv_contrasenas);
            if(jefe_empleado_choice.equals("Jefe")){
                cardView.setVisibility(View.VISIBLE);
            }else{
                cardView.setVisibility(View.GONE);
            }


        }catch(Exception e){
            Toast.makeText(Activity_EmpleadoJefe.this, getString(R.string.error), Toast.LENGTH_LONG).show();
        }
    }


    public void EmpleadoJefe_Click_Pedidos(View v){
        if(Utiles.Internet.isOnline(Activity_EmpleadoJefe.this,true)) {
            Intent intent = new Intent(Activity_EmpleadoJefe.this, Activity_Pedidos.class);
            startActivity(intent);
        }

    }

    public void EmpleadoJefe_Click_Menu(View v){
        if(Utiles.Internet.isOnline(Activity_EmpleadoJefe.this,true)) {
            Intent intent = new Intent(Activity_EmpleadoJefe.this, Activity_Personalizar_Menu.class);
            startActivity(intent);
        }
    }

    public void EmpleadoJefe_Click_Categoria(View v){
        if(Utiles.Internet.isOnline(Activity_EmpleadoJefe.this,true)) {
            Intent intent = new Intent(Activity_EmpleadoJefe.this, Activity_Personalizar_Categorias.class);
            startActivity(intent);
        }
    }

    public void EmpleadoJefe_Click_Distancias(View v){
        if(Utiles.Internet.isOnline(Activity_EmpleadoJefe.this,true)) {
            Intent intent = new Intent(Activity_EmpleadoJefe.this, Activity_Distancias.class);
            startActivity(intent);
        }
    }

    public void EmpleadoJefe_Click_Contrasenas(View v){
        if(Utiles.Internet.isOnline(Activity_EmpleadoJefe.this,true)) {
            Intent intent = new Intent(Activity_EmpleadoJefe.this, Activity_Contrasena.class);
            startActivity(intent);
        }
    }


}
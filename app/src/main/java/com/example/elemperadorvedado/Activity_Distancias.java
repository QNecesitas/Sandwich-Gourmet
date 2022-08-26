package com.example.elemperadorvedado;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_Distancias extends AppCompatActivity {

    //Recycler
    private RecyclerView recycler;
    private ArrayList<Pila_Distancia> pila_distancias;
    private AdaptadorR_Distancias adapter;
    private EditText et_desde, et_precio, et_hasta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distancias);

        //Toolbar
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.distancias_appBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.distancias_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recycler=(RecyclerView) findViewById(R.id.distancias_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(Activity_Distancias.this));
        pila_distancias=new ArrayList<>();
        adapter=new AdaptadorR_Distancias(this,pila_distancias);

        cargardatosInternet();

    }

    //Añadir una nueva distancia
    public void boton_Add_Dist(View view) {
        li_insertar_distancia();
    }
    private void li_insertar_distancia(){
        LayoutInflater inflater=LayoutInflater.from(Activity_Distancias.this);
        View vista= inflater.inflate(R.layout.li_agregar_distancia, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(Activity_Distancias.this);
        builder.setView(vista);
        final AlertDialog alertDialog=builder.create();

        //Declaraciones
        et_desde=(EditText)vista.findViewById(R.id.agregar_distancia_desde);
        et_hasta=(EditText)vista.findViewById(R.id.agregar_distancia_hasta);
        et_precio=(EditText)vista.findViewById(R.id.agregar_distancia_precio);
        Button tv=(Button)vista.findViewById(R.id.agregar_distancia_aceptar);
        TextView texto_toast=(TextView)vista.findViewById(R.id.li_agregar_distancia_texto_toast);
        CardView texto_cardview=(CardView)vista.findViewById(R.id.li_agregar_distancia_texto_cardview);
        ImageView imageView_cerrar=(ImageView)vista.findViewById(R.id.li_agregar_distancia_cerrar);

        //Listener
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utiles.Internet.isOnline(Activity_Distancias.this,true)) {
                    //chequear si esta vacio algun edit text
                    if (isEtsLlenos()) {
                        if (Utiles.Internet.isOnline(Activity_Distancias.this, true)) {

                            //pasar la nueva pila con sus 3 valores al Const
                            int dist_desde = Integer.parseInt(et_desde.getText().toString());
                            int dist_hasta = Integer.parseInt(et_hasta.getText().toString());
                            int dist_precio = Integer.parseInt(et_precio.getText().toString());

                            if (dist_desde < dist_hasta) {
                                if (!isValoresExistente(dist_desde, dist_hasta)) {
                                    alertDialog.dismiss();
                                    insertarDatosInternet(dist_desde, dist_hasta, dist_precio);
                                } else {
                                    texto_cardview.setVisibility(View.VISIBLE);
                                    texto_toast.setText(getString(R.string.km_comprendidos_otras_distancias));
                                }
                            } else {
                                texto_cardview.setVisibility(View.VISIBLE);
                                texto_toast.setText(R.string.desde_menor_que_hasta);
                            }
                        }
                    }
                }
            }
        });
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
    private void insertarDatosInternet(int desde, int hasta, int precio){

        ProgressDialog  pdialog = ProgressDialog.show(Activity_Distancias.this,getString(R.string.actualizando_info),getString(R.string.por_favor_espere),false,false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ColocarDistancia.php?Desde="+desde+"&Hasta="+hasta+"&Precio="+precio, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                pila_distancias.add(new Pila_Distancia(desde,hasta,precio));
                actualizarAdaptador();
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private boolean isValoresExistente(int desdeTraid, int hastaTraid){
        boolean existente=false;

        for(int pos=0;pos<pila_distancias.size();pos++){
            int desdeActual=pila_distancias.get(pos).getDistancia_inicial();
            int hastaActual=pila_distancias.get(pos).getDistancia_final();

            //          /  0  / 0
            if(desdeTraid>=desdeActual&&desdeTraid<=hastaActual)existente=true;

            //        0 / 0 /
            if(hastaTraid>=desdeActual&&hastaTraid<=hastaActual)existente=true;

            //       0 /  / 0
            if(desdeTraid<=desdeActual&&hastaTraid>=hastaActual)existente=true;
        }
        return existente;
    }

    //Eliminar distancia
    private void showAlertDialogConfirmacionDelete(int position, int desde) {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Distancias.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.eliminar_distancia_titulo);
        builder.setMessage(R.string.eliminar_distancia_confirmacion);

        //set listeners for dialog buttons
        //positive button
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Utiles.Internet.isOnline(Activity_Distancias.this,true)) {
                    dialog.dismiss();
                    eliminardatosInternet(position, desde);
                }
            }
        });

        builder.setNegativeButton(getString(R.string.cancelar),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }
    private void eliminardatosInternet(int position, int desde){

        ProgressDialog  pdialog = ProgressDialog.show(Activity_Distancias.this,getString(R.string.actualizando_info),getString(R.string.por_favor_espere),false,false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"EliminarDistancia.php?Desde="+desde, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                pila_distancias.remove(position);
                actualizarAdaptador();

            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }

    //Editar distancia
    private void li_editar_distancia(int position){
        LayoutInflater inflater=LayoutInflater.from(Activity_Distancias.this);
        View vista= inflater.inflate(R.layout.li_agregar_distancia, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(Activity_Distancias.this);
        builder.setView(vista);
        final AlertDialog alertDialog=builder.create();

        //Declaraciones
        et_desde=(EditText)vista.findViewById(R.id.agregar_distancia_desde);
        et_hasta=(EditText)vista.findViewById(R.id.agregar_distancia_hasta);
        et_precio=(EditText)vista.findViewById(R.id.agregar_distancia_precio);
        Button tv=(Button)vista.findViewById(R.id.agregar_distancia_aceptar);
        TextView texto_toast=(TextView)vista.findViewById(R.id.li_agregar_distancia_texto_toast);
        CardView texto_cardview=(CardView)vista.findViewById(R.id.li_agregar_distancia_texto_cardview);
        ImageView imageView_cerrar=(ImageView)vista.findViewById(R.id.li_agregar_distancia_cerrar);

        //Llenado
        String desde=String.valueOf(pila_distancias.get(position).getDistancia_inicial());
        String hasta=String.valueOf(pila_distancias.get(position).getDistancia_final());
        String precio=String.valueOf(pila_distancias.get(position).getPrecio());
        et_desde.setText(desde);
        et_hasta.setText(hasta);
        et_precio.setText(precio);


        //Listener
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utiles.Internet.isOnline(Activity_Distancias.this,true)) {
                    //chequear si esta vacio algun edit text
                    if (isEtsLlenos()) {
                        if (Utiles.Internet.isOnline(Activity_Distancias.this, true)) {
                            //pasar la nueva pila con sus 3 valores al Const
                            int dist_desde = Integer.parseInt(et_desde.getText().toString());
                            int dist_hasta = Integer.parseInt(et_hasta.getText().toString());
                            int dist_precio = Integer.parseInt(et_precio.getText().toString());

                            if (dist_desde < dist_hasta) {
                                if (!isValoresExistente(dist_desde, dist_hasta, position)) {
                                    alertDialog.dismiss();
                                    actualizarDatosInternet(dist_desde, dist_hasta, dist_precio, position);
                                } else {
                                    texto_cardview.setVisibility(View.VISIBLE);
                                    texto_toast.setText(getString(R.string.km_comprendidos_otras_distancias));
                                }
                            } else {
                                texto_cardview.setVisibility(View.VISIBLE);
                                texto_toast.setText(R.string.desde_menor_que_hasta);
                            }
                        }
                    }
                }

            }
        });
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
    private boolean isEtsLlenos(){
        //chequear si esta vacio algun edit text
        if(!et_desde.getText().toString().trim().isEmpty()){
            if(!et_hasta.getText().toString().trim().isEmpty()){
                if(!et_precio.getText().toString().trim().isEmpty()){
                    return true;
                }else{
                    et_precio.setError(getString(R.string.campo_vacio));
                    return false;
                }
            }else{
                et_hasta.setError(getString(R.string.campo_vacio));
                return false;
            }
        }else{
            et_desde.setError(getString(R.string.campo_vacio));
            return false;
        }
    }
    private void actualizarDatosInternet(int desde, int hasta, int precio,int position){
        int desdeId=pila_distancias.get(position).getDistancia_inicial();
        ProgressDialog  pdialog = ProgressDialog.show(Activity_Distancias.this,getString(R.string.actualizando_info),getString(R.string.por_favor_espere),false,false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ActualizarDistancia.php?DesdeId="+desdeId+"&Desde="+desde+"&Hasta="+hasta+"&Precio="+precio, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                pila_distancias.add(position,new Pila_Distancia(desde,hasta,precio));
                pila_distancias.remove(position+1);
                actualizarAdaptador();
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private boolean isValoresExistente(int desdeTraid, int hastaTraid,int positionAcambiar){
        boolean existente=false;

        for(int pos=0;pos<pila_distancias.size();pos++){
            if(pos==positionAcambiar)continue;
            int desdeActual=pila_distancias.get(pos).getDistancia_inicial();
            int hastaActual=pila_distancias.get(pos).getDistancia_final();

            //          /  0  / 0
            if(desdeTraid>=desdeActual&&desdeTraid<=hastaActual)existente=true;

            //        0 / 0 /
            if(hastaTraid>=desdeActual&&hastaTraid<=hastaActual)existente=true;

            //       0 /  / 0
            if(desdeTraid<=desdeActual&&hastaTraid>=hastaActual)existente=true;
        }
        return existente;
    }

    //Actualizar Recycler
    private void actualizarAdaptador(){
        adapter=new AdaptadorR_Distancias(Activity_Distancias.this, pila_distancias);
        recycler.setAdapter(adapter);
        //metodo de editar
        adapter.setEditar_listener(new AdaptadorR_Distancias.RecyclerEditarListener() {
            @Override
            public void onClickItem(View v, int position) {
                li_editar_distancia(position);
            }
        });

        //metodo de eliminar
        adapter.setEliminar_listener(new AdaptadorR_Distancias.RecyclerEliminarListener() {
            @Override
            public void onClickItem(View v, int position) {
                showAlertDialogConfirmacionDelete(position,pila_distancias.get(position).getDistancia_inicial());
            }
        });
    }

    //Internet
    private void cargardatosInternet(){
        ProgressDialog  pdialog = ProgressDialog.show(Activity_Distancias.this,getString(R.string.cargando_datos),getString(R.string.por_favor_espere),false,false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerRecyclerDistancias.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                pila_distancias.clear();
                try{
                    JSONArray array =new JSONArray(response);
                    for (int i=0; i<array.length();i++) {

                        JSONObject json_objet = array.getJSONObject(i);
                        if(json_objet.getString("Desde").length()>0) {
                            pila_distancias.add(new Pila_Distancia(
                                    json_objet.getInt("Desde"),
                                    json_objet.getInt("Hasta"),
                                    json_objet.getInt("Precio")
                            ));
                        }
                    }

                    actualizarAdaptador();

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }

    public void showAlertDialogNoInternet() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Distancias.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.revise_conx);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cargardatosInternet();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }


}
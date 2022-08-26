package com.example.elemperadorvedado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Activity_Carrito extends AppCompatActivity {

    //Declaraciones
    private TextView tv_precio_total;
    private TextView tv_ubicacion;

    //Hora
    private Calendar calendarObtenido;
    private TextView tv_hora;
    private String hora_momentanea;

    //Recycler
    private RecyclerView recyclerView_elementos;
    private RecyclerView recyclerView_sanwiches;

    //Mapa
    private final int RESULT_CODE_MAPA=31;
    private String userLatitud="no";
    private String userLongitud="no";
    private int precioEnvioAcasa=0;
    private int distanciaEnvioAcasa=0;
    private double precioTotalDeProductos=0.0;

    //Direccion
    private TextInputLayout TILDIreccion;
    private TextInputEditText TIETDireccion;
    //Numero movil
    private EditText et_NumMovil;
    //Numero wapp
    private EditText et_NumWapp;
    //Spinner
    private Spinner spinner;

    //Internet
    private String pedidoId, pedidoHora, pedidoDireccion, pedidoNumeroWapp, pedidoNumeroMovil, pedidoFormaPago;
    private String[] JSON_sandwiches;
    private String JSON_elementos, JSON_sandwich1, JSON_sandwich2, JSON_sandwich3, JSON_sandwich4, JSON_sandwich5;

    //SharedPreferencias
    private String idsEnviados;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        //Llenado
        tv_precio_total=(TextView) findViewById(R.id.carrito_tv_precio_total);
        tv_hora=(TextView) findViewById(R.id.carrito_tv_hora);
        tv_ubicacion=(TextView) findViewById(R.id.carrito_tv_ubicacion);
        TIETDireccion=(TextInputEditText) findViewById(R.id.carrito_tiet_direccion);
        et_NumMovil=(EditText) findViewById(R.id.carrito_et_numcel);
        et_NumWapp=(EditText) findViewById(R.id.carrito_et_numwapp);

        spinner=(Spinner) findViewById(R.id.activity_carrito_sp_forma_de_pago);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(Activity_Carrito.this,R.array.formas_de_pago_array,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pedidoFormaPago=adapter.getItem(position).toString();

                switch (position){
                    case  0:
                        break;
                    case 1:
                        showAlertDialogPagoTransfermovil();
                        break;
                    case 2:
                        showAlertDialogPagoEnZona();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        //Recyclers
        recyclerView_elementos=(RecyclerView)findViewById(R.id.carrito_recycler_elementos);
        recyclerView_sanwiches=(RecyclerView)findViewById(R.id.carrito_recycler_sandwiches);
        recyclerView_elementos.setLayoutManager(new LinearLayoutManager(Activity_Carrito.this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        });
        recyclerView_sanwiches.setLayoutManager(new LinearLayoutManager(Activity_Carrito.this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically(){
                return false;
            }
        });
        recyclerView_elementos.setHasFixedSize(true);
        recyclerView_sanwiches.setHasFixedSize(true);

        //Preferencias
        sharedPreferences = getSharedPreferences("SandwichGourmet", 0);
        sharedEditor=sharedPreferences.edit();
        idsEnviados = sharedPreferences.getString("idsEnviados", " ");

        actualizarDatosDelCarrito();
    }

    //ubicacion-hora
    private void ubicacionObtenida(Intent data){
        userLatitud=data.getStringExtra("latitud");
        userLongitud=data.getStringExtra("longitud");
        precioEnvioAcasa=data.getIntExtra("precio",0);
        distanciaEnvioAcasa=data.getIntExtra("distanciaKm",0);

        double sumaDePrecios=precioEnvioAcasa+precioTotalDeProductos;
        String textoPrecio = sumaDePrecios + " CUP";
        tv_precio_total.setText(textoPrecio);

        TextView textView_ubicacion=(TextView) findViewById(R.id.carrito_tv_ubicacion);
        String texto_textView=getString(R.string.obtenida);
        textView_ubicacion.setText(texto_textView);

        if(precioEnvioAcasa !=0) showAlertDialogPrecioAumentadoDistancia(precioEnvioAcasa);
        else showAlertDialogDireccionNoPermitida();
    }
    public void carrito_click_hora_a_entregar(View view) {
        li_escogerHora();
    }
    public void carrito_click_escoger_ubicacion(View view) {
        if(Utiles.Internet.isOnline(Activity_Carrito.this,true)){
            Intent intent = new Intent(Activity_Carrito.this, Activity_Mapa_Obtener.class);
            startActivityForResult(intent, RESULT_CODE_MAPA);
        }
    }
    private void showAlertDialogPrecioAumentadoDistancia(int precio) {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.ubicacion_obtenida);
        builder.setMessage(getString(R.string.precio_envio_sumado)+precio+" CUP");
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create the alert dialog and show it
        builder.create().show();
    }

    //Recycler
    private void actualizarDatosDelCarrito(){
        LinearLayout imagen_de_empty=(LinearLayout)findViewById(R.id.carrito_layout_imagen_empty);
        TextView precioTotal=(TextView)findViewById(R.id.carrito_tv_precio_total);

        if(Carrito_Elementos.getCantidadElementos()==0&&Carrito_Elementos.getCantidadSandwich()==0){
            imagen_de_empty.setVisibility(View.VISIBLE);
            AdaptadorR_Carrito_Elementos adaptadorR_carritoElementos =new AdaptadorR_Carrito_Elementos(Activity_Carrito.this,Carrito_Elementos.getArrayList_elementos());
            AdaptadorR_Carrito_Sandwiches adaptadorR_carrito_sandwiches=new AdaptadorR_Carrito_Sandwiches(Activity_Carrito.this,Carrito_Elementos.getArrayList_sandwiches());
            adaptadorR_carritoElementos.setRecyclerClickListener(new AdaptadorR_Carrito_Elementos.RecyclerDeleteListener() {
                @Override
                public void onClick(View v, int position) {
                    showAlertDialogEliminarElemento(position);
                }
            });
            adaptadorR_carrito_sandwiches.setRecyclerClickListener(new AdaptadorR_Carrito_Sandwiches.RecyclerDeleteListener() {
                @Override
                public void onClick(View v, int position) {
                    showAlertDialogEliminarSandwich(position);
                }
            });
            recyclerView_elementos.setHasFixedSize(true);
            recyclerView_sanwiches.setHasFixedSize(true);
            recyclerView_elementos.setAdapter(adaptadorR_carritoElementos);
            recyclerView_sanwiches.setAdapter(adaptadorR_carrito_sandwiches);
            precioTotalDeProductos=0.0;
            precioTotal.setText("0 CUP");
        }else{
            imagen_de_empty.setVisibility(View.GONE);
            AdaptadorR_Carrito_Elementos adaptadorR_carritoElementos =new AdaptadorR_Carrito_Elementos(Activity_Carrito.this,Carrito_Elementos.getArrayList_elementos());
            AdaptadorR_Carrito_Sandwiches adaptadorR_carrito_sandwiches=new AdaptadorR_Carrito_Sandwiches(Activity_Carrito.this,Carrito_Elementos.getArrayList_sandwiches());
            adaptadorR_carritoElementos.setRecyclerClickListener(new AdaptadorR_Carrito_Elementos.RecyclerDeleteListener() {
                @Override
                public void onClick(View v, int position) {
                    showAlertDialogEliminarElemento(position);
                }
            });
            adaptadorR_carrito_sandwiches.setRecyclerClickListener(new AdaptadorR_Carrito_Sandwiches.RecyclerDeleteListener() {
                @Override
                public void onClick(View v, int position) {
                    showAlertDialogEliminarSandwich(position);
                }
            });
            precioTotalDeProductos=adaptadorR_carritoElementos.getPrecioTotalDeLosElementos()+adaptadorR_carrito_sandwiches.getPrecioTotalDeLosSandwiches();
            String texto_precioTotal= precioTotalDeProductos + " CUP";
            precioTotal.setText(texto_precioTotal);
            recyclerView_elementos.setHasFixedSize(true);
            recyclerView_sanwiches.setHasFixedSize(true);
            recyclerView_elementos.setAdapter(adaptadorR_carritoElementos);
            recyclerView_sanwiches.setAdapter(adaptadorR_carrito_sandwiches);
        }
    }
    public void showAlertDialogVaciarCarrito() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.desea_vaciar_carrito);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Carrito_Elementos.vaciarCarrito();
                actualizarDatosDelCarrito();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create the alert dialog and show it
        builder.create().show();
    }
    public void showAlertDialogEliminarElemento(int position) {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.realmente_desea_eliminar_elemento_carrito);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Carrito_Elementos.delElemento(position);
                actualizarDatosDelCarrito();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create the alert dialog and show it
        builder.create().show();
    }
    public void showAlertDialogEliminarSandwich(int position) {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.realmente_desea_eliminar_elemento_carrito);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Carrito_Elementos.delSandwich(position);
                actualizarDatosDelCarrito();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create the alert dialog and show it
        builder.create().show();
    }
    public void carrito_click_vaciar_carrito(View v){
        showAlertDialogVaciarCarrito();
    }

    //Finalizar
    public void carrito_click_enviar_pedido(View v){
        TILDIreccion=(TextInputLayout)findViewById(R.id.carrito_til_direccion);
        TIETDireccion=(TextInputEditText) findViewById(R.id.carrito_tiet_direccion);

        if(Utiles.Internet.isOnline(Activity_Carrito.this,true)) {
                if (!TIETDireccion.getText().toString().trim().isEmpty()) {
                    if(!et_NumMovil.getText().toString().isEmpty()) {
                        if(!et_NumWapp.getText().toString().isEmpty()) {
                            if (!tv_hora.getText().toString().equals(getString(R.string.no_seleccionada))) {
                                if (!tv_ubicacion.getText().toString().equals(getString(R.string.no_seleccionada))) {
                                    if (precioEnvioAcasa > 0) {
                                        if (!Carrito_Elementos.isEmpty()) {
                                            llenarDatosPedido();
                                            llenarJSONs();
                                            subirDatosAInternetJs();
                                        } else {
                                            Toast.makeText(Activity_Carrito.this, getString(R.string.carrito_vacio), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        showAlertDialogDireccionNoPermitida();
                                    }
                                } else {
                                    showAlertDialogUbicacionMal();
                                }
                            } else {
                                showAlertDialogHoraMal();
                            }
                        }else{
                            et_NumWapp.setError(getText(R.string.campo_vacio));
                        }
                    }else{
                        et_NumMovil.setError(getText(R.string.campo_vacio));
                    }
                } else {
                    TILDIreccion.setError(getString(R.string.debe_espec_direccion));
                    showAlertDialogDireccionMal();
                }
        }

    }
    private void showAlertDialogDireccionMal() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.no_se_puod_enviar_pedido);
        builder.setMessage(R.string.debe_colocar_direccion);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create the alert dialog and show it
        builder.create().show();
    }
    private void showAlertDialogUbicacionMal() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.no_se_puod_enviar_pedido);
        builder.setMessage(R.string.debe_selecc_ubic);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create the alert dialog and show it
        builder.create().show();
    }
    private void showAlertDialogHoraMal() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.no_se_puod_enviar_pedido);
        builder.setMessage(R.string.debe_especificar_hora);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //create the alert dialog and show it
        builder.create().show();
    }
    private void showAlertDialogNoInternet() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.revise_conx);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }
    private void showAlertDialogDireccionNoPermitida() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Carrito.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.lo_sentimos);
        builder.setMessage(R.string.no_ofrecemos_servicios_a_esa_dist);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }
    private void showAlertDialogPagoTransfermovil() {
        LayoutInflater inflater=LayoutInflater.from(Activity_Carrito.this);
        View vista= inflater.inflate(R.layout.li_pago_transfermovil, null);
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setView(vista);
        final androidx.appcompat.app.AlertDialog alertDialog=builder.create();

        //Declaraciones
        TextView tv_aceptar=(TextView) vista.findViewById(R.id.li_pago_transfermovil_tv_aceptar);

        //LLenar

        //Listener
        tv_aceptar.setOnClickListener(new View.OnClickListener() {
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
    private void showAlertDialogPagoEnZona() {
        LayoutInflater inflater=LayoutInflater.from(Activity_Carrito.this);
        View vista= inflater.inflate(R.layout.li_pago_enzona, null);
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setView(vista);
        final androidx.appcompat.app.AlertDialog alertDialog=builder.create();

        //Declaraciones
        TextView tv_aceptar=(TextView) vista.findViewById(R.id.li_pago_enzona_tv_aceptar);
        //LLenar

        //Listener
        tv_aceptar.setOnClickListener(new View.OnClickListener() {
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
    //Internet
    private void llenarDatosPedido(){
        pedidoId=userLatitud + new SimpleDateFormat("MMddHHmmss",Locale.getDefault()).format(new Date());
        pedidoDireccion=TIETDireccion.getText().toString();
        pedidoHora=tv_hora.getText().toString();
        pedidoNumeroMovil=et_NumMovil.getText().toString();
        pedidoNumeroWapp=et_NumWapp.getText().toString();
    }
    private void llenarJSONs(){

        //Elementos
        if(Carrito_Elementos.getCantidadElementos()==0) {
            JSON_elementos="no";
        }else{
            JSON_elementos = new Gson().toJson(Carrito_Elementos.getArrayList_elementos());
        }

        //Sandwich
        JSON_sandwiches=new String[5];
        JSON_sandwiches[0]="no";
        JSON_sandwiches[1]="no";
        JSON_sandwiches[2]="no";
        JSON_sandwiches[3]="no";
        JSON_sandwiches[4]="no";

        for (int x=0;x<Carrito_Elementos.getCantidadSandwich();x++){
            JSON_sandwiches[x]=new Gson().toJson(Carrito_Elementos.getArrayList_sandwiches().get(x));
        }


        JSON_sandwich1= JSON_sandwiches[0];
        JSON_sandwich2= JSON_sandwiches[1];
        JSON_sandwich3= JSON_sandwiches[2];
        JSON_sandwich4= JSON_sandwiches[3];
        JSON_sandwich5= JSON_sandwiches[4];

    }
    private void subirDatosAInternetJs(){
        ProgressDialog progressDialog=ProgressDialog.show(Activity_Carrito.this,getString(R.string.enviando_pedido),getString(R.string.por_favor_espere),false,false);

        RequestQueue requestQALL = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ColocarPedidoJs.php?Id="+pedidoId+"&Direccion="+pedidoDireccion+"&Hora="+pedidoHora+"&Latitud="+userLatitud+"&Longitud="+userLongitud+"&Precio="+tv_precio_total.getText() +"&Elementos="+JSON_elementos+"&Sandwich1="+JSON_sandwich1+"&Sandwich2="+JSON_sandwich2+"&Sandwich3="+JSON_sandwich3+"&Sandwich4="+JSON_sandwich4+"&Sandwich5="+JSON_sandwich5+"&FormaPago="+pedidoFormaPago+"&NumWapp="+pedidoNumeroWapp+"&NumCel="+pedidoNumeroMovil,new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                progressDialog.dismiss();
                li_finalizado();
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQALL.add(stringRequest);
    }
    private void li_finalizado(){
        LayoutInflater inflater=LayoutInflater.from(Activity_Carrito.this);
        View vista= inflater.inflate(R.layout.li_pedido_enviado, null);
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setView(vista);
        final androidx.appcompat.app.AlertDialog alertDialog=builder.create();

        //Declaraciones
        Carrito_Elementos.vaciarCarrito();
        actualizarDatosDelCarrito();
        ImageView imagenCruz=(ImageView)vista.findViewById(R.id.li_pedido_enviado_cerrar);
        CardView cardView=(CardView)vista.findViewById(R.id.li_pedido_enviado_cv_ir_a_mis_pedidos);

        //Listener
        imagenCruz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ir_a_mis_pedidos();
            }
        });

        //Guardar preferencias
        sharedEditor.putString("idsEnviados",idsEnviados+"/"+pedidoId);
        sharedEditor.apply();

        //Finalizado
        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Mapa
        if(requestCode==RESULT_CODE_MAPA) {
            if(resultCode== Activity.RESULT_OK){
                ubicacionObtenida(data);
            }else{
                Toast.makeText(Activity_Carrito.this, R.string.error_obtener_ubic, Toast.LENGTH_LONG).show();
            }
        }


    }
    public void carrito_click_mis_pedidos(View v){
        if(Utiles.Internet.isOnline(Activity_Carrito.this,true)) {
            ir_a_mis_pedidos();
        }
    }
    private void ir_a_mis_pedidos(){
        if(Utiles.Internet.isOnline(Activity_Carrito.this,true)){
            Intent intent=new Intent(Activity_Carrito.this,Activity_Mis_Pedidos.class);
            startActivity(intent);
        }
    }
    private void li_escogerHora(){
        LayoutInflater inflater=LayoutInflater.from(Activity_Carrito.this);
        View vista= inflater.inflate(R.layout.li_esoger_hora, null);
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(Activity_Carrito.this);
        builder.setView(vista);
        final androidx.appcompat.app.AlertDialog alertDialog=builder.create();

        //Declaraciones
        SwitchCompat switch_ahora=(SwitchCompat) vista.findViewById(R.id.li_escoger_hora_switch);
        TimePicker timePicker=(TimePicker) vista.findViewById(R.id.li_escoger_hora_time_picker);
        TextView tv_tiempo_demora=(TextView) vista.findViewById(R.id.li_escoger_hora_tv_tiempo_demora_pedido);
        TextView tv_hora_despues=(TextView) vista.findViewById(R.id.li_escoger_hora_tv_hora_despues);
        TextView tv_aceptar=(TextView) vista.findViewById(R.id.li_escoger_hora_tv_aceptar);
        TextView tv_cancelar=(TextView) vista.findViewById(R.id.li_escoger_hora_tv_cancelar);

        //LLenar
        calendarObtenido=Calendar.getInstance();
        calendarObtenido.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
        calendarObtenido.set(Calendar.MINUTE,timePicker.getMinute());
        hora_momentanea=new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(calendarObtenido.getTime());

        //Listener
        switch_ahora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch_ahora.isChecked()){
                    hora_momentanea=getString(R.string.ahora);
                    timePicker.setVisibility(View.GONE);
                    tv_tiempo_demora.setVisibility(View.VISIBLE);
                }else{
                    Calendar calendarObtenido=Calendar.getInstance();
                    calendarObtenido.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                    calendarObtenido.set(Calendar.MINUTE,timePicker.getMinute());
                    hora_momentanea=new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(calendarObtenido.getTime());
                    timePicker.setVisibility(View.VISIBLE);
                    tv_tiempo_demora.setVisibility(View.GONE);
                }
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                tv_hora_despues.setVisibility(View.GONE);
                calendarObtenido=Calendar.getInstance();
                calendarObtenido.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendarObtenido.set(Calendar.MINUTE,minute);
                hora_momentanea=new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(calendarObtenido.getTime());

            }
        });

        tv_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tv_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendarActual=Calendar.getInstance();

                if(!switch_ahora.isChecked()) {
                    if (calendarObtenido.getTimeInMillis() - calendarActual.getTimeInMillis() > 3600000) {
                        if (calendarObtenido.get(Calendar.HOUR_OF_DAY) >= 9 && calendarObtenido.get(Calendar.HOUR_OF_DAY) <= 18 && calendarActual.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) {
                            tv_hora.setText(hora_momentanea);
                            alertDialog.dismiss();
                        } else {
                            tv_hora_despues.setVisibility(View.VISIBLE);
                            tv_hora_despues.setText(R.string.error_fuera_de_horario);
                        }
                    } else {
                        tv_hora_despues.setVisibility(View.VISIBLE);
                        tv_hora_despues.setText(getString(R.string.seleccione_minimo_hora_despues));
                    }
                }else{
                    if (calendarActual.get(Calendar.HOUR_OF_DAY) >= 9 && calendarActual.get(Calendar.HOUR_OF_DAY) <= 18 && calendarActual.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) {
                        tv_hora.setText(hora_momentanea);
                        alertDialog.dismiss();
                    }else{
                        tv_hora_despues.setVisibility(View.VISIBLE);
                        tv_hora_despues.setText(R.string.error_fuera_de_horario);
                    }
                }
            }
        });

        //Finalizado
        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }





}
package com.example.elemperadorvedado;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_Detalles extends AppCompatActivity {


    //Recycler
    private RecyclerView recycler_panes,recycler_embutidos,recycler_quesos,recycler_salsas,recycler_vegetales;
    private ArrayList<Pila_Menu> array_panes, array_embutidos,array_quesos,array_salsas,array_vegetales;
    private AdaptadorR_Menu_MultiSelection adapter_embutidos,adapter_quesos,adapter_salsas,adapter_vegetales;
    private AdaptadorR_Menu_Selection adapter_panes;
    private double precioMV_internet =0;

    //Precio
    double precioTotal=0,precio_panes=0,precio_embutidos=0,precio_quesos=0,precio_salsas=0,precio_vegetales=0,precio_modoV=0;

    //CheckBox
    CheckBox checkBox_modo_vegetariano;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        //Toolbar
        Toolbar toolbar=(Toolbar)findViewById(R.id.detalles_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //asignar imagen
        ImageView imagen_general = (ImageView) findViewById(R.id.activity_detalles_imagen);
        Glide.with(Activity_Detalles.this)
                .load(Utiles.DIRECWEB_IMG+"Categorias_Sandwich.jpg")
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imagen_general);


        //Recycler
        array_panes=new ArrayList<>();
        array_embutidos =new ArrayList<>();
        array_quesos=new ArrayList<>();
        array_salsas=new ArrayList<>();
        array_vegetales=new ArrayList<>();
        recycler_panes=(RecyclerView)findViewById(R.id.detalles_recycler_panes);
        recycler_panes.setHasFixedSize(true);
        recycler_panes.setLayoutManager(new LinearLayoutManager(Activity_Detalles.this,LinearLayoutManager.HORIZONTAL,false));
        recycler_embutidos=(RecyclerView)findViewById(R.id.detalles_recycler_car_y_pes);
        recycler_embutidos.setHasFixedSize(true);
        recycler_embutidos.setLayoutManager(new LinearLayoutManager(Activity_Detalles.this,LinearLayoutManager.HORIZONTAL,false));
        recycler_quesos=(RecyclerView)findViewById(R.id.detalles_recycler_quesos);
        recycler_quesos.setHasFixedSize(true);
        recycler_quesos.setLayoutManager(new LinearLayoutManager(Activity_Detalles.this,LinearLayoutManager.HORIZONTAL,false));
        recycler_salsas=(RecyclerView)findViewById(R.id.detalles_recycler_salsas);
        recycler_salsas.setHasFixedSize(true);
        recycler_salsas.setLayoutManager(new LinearLayoutManager(Activity_Detalles.this,LinearLayoutManager.HORIZONTAL,false));
        recycler_vegetales=(RecyclerView)findViewById(R.id.detalles_recycler_vegetales);
        recycler_vegetales.setHasFixedSize(true);
        recycler_vegetales.setLayoutManager(new LinearLayoutManager(Activity_Detalles.this,LinearLayoutManager.HORIZONTAL,false));

        cargardatosInternet();
        
    }

    //Internet
    private void cargardatosInternet() {
        ProgressDialog pDialog = ProgressDialog.show(Activity_Detalles.this, getString(R.string.cargando_datos), getString(R.string.espere), false, false);
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerRecyclerMenu.php?Categoria=Sandwich", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                array_panes.clear();
                array_embutidos.clear();
                array_quesos.clear();
                array_salsas.clear();
                array_vegetales.clear();
                try{
                    JSONArray array =new JSONArray(response);
                    for (int i=0; i<array.length();i++) {

                        JSONObject json_objet = array.getJSONObject(i);
                        if(json_objet.getString("Producto").length()>0) {
                            if(json_objet.getInt("Estado")==1) {

                                double precio = json_objet.getDouble("Precio");
                                String subcategoria = json_objet.getString("Subcategoria");
                                String nombre_prod = json_objet.getString("Producto");
                                int estado = json_objet.getInt("Estado");

                                distribuirXcategoria(subcategoria, nombre_prod, precio, estado);

                            }
                        }
                    }

                    actualizarRecyclers();

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        pDialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    public void showAlertDialogNoInternet() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Detalles.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.rev_conx);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cargardatosInternet();
                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    //Trabajo con recyler
    private void distribuirXcategoria(String subcategoria, String nombre_prod, double precio, int estado) {

        switch (subcategoria){
            case "panes":
                array_panes.add(new Pila_Menu(nombre_prod,precio, estado));
                break;
            case "embutidos":
                array_embutidos.add(new Pila_Menu(nombre_prod,precio, estado));
                break;
            case "quesos":
                array_quesos.add(new Pila_Menu(nombre_prod,precio, estado));
                break;
            case "salsas":
                array_salsas.add(new Pila_Menu(nombre_prod,precio, estado));
                break;
            case "vegetales":
                array_vegetales.add(new Pila_Menu(nombre_prod,precio, estado));
                break;
            case "modo_vegetariano":
                precioMV_internet=precio;
        }


    }
    private void actualizarRecyclers(){

        //panes
        adapter_panes=new AdaptadorR_Menu_Selection(Activity_Detalles.this,array_panes);
        recycler_panes.setAdapter(adapter_panes);
        adapter_panes.setListener(new AdaptadorR_Menu_Selection.RecyclerTouchListener() {
            @Override
            public void onClick(View v, int position) {
                if(adapter_panes.getPositionSeleccionada()==-1){
                    precio_panes=0;
                }else{
                    precio_panes=array_panes.get(position).getPrecio();
                }
                actualizarPrecio();
            }
        });

        //embutidos
        adapter_embutidos=new AdaptadorR_Menu_MultiSelection(Activity_Detalles.this, array_embutidos);
        recycler_embutidos.setAdapter(adapter_embutidos);
        adapter_embutidos.setClickListener(new AdaptadorR_Menu_MultiSelection.RecyclerTouchListener() {
            @Override
            public void onClickItem(View v, int position) {
                precio_embutidos=adapter_embutidos.getPrecioTotal();
                actualizarPrecio();
            }
        });

        //quesos
        adapter_quesos=new AdaptadorR_Menu_MultiSelection(Activity_Detalles.this, array_quesos);
        recycler_quesos.setAdapter(adapter_quesos);
        adapter_quesos.setClickListener(new AdaptadorR_Menu_MultiSelection.RecyclerTouchListener() {
            @Override
            public void onClickItem(View v, int position) {
                precio_quesos=adapter_quesos.getPrecioTotal();
                actualizarPrecio();
            }
        });

        //salsas
        adapter_salsas=new AdaptadorR_Menu_MultiSelection(Activity_Detalles.this, array_salsas);
        recycler_salsas.setAdapter(adapter_salsas);
        adapter_salsas.setClickListener(new AdaptadorR_Menu_MultiSelection.RecyclerTouchListener() {
            @Override
            public void onClickItem(View v, int position) {
                precio_salsas=adapter_salsas.getPrecioTotal();
                actualizarPrecio();
            }
        });

        //vegetales
        adapter_vegetales=new AdaptadorR_Menu_MultiSelection(Activity_Detalles.this, array_vegetales);
        recycler_vegetales.setAdapter(adapter_vegetales);
        adapter_vegetales.setClickListener(new AdaptadorR_Menu_MultiSelection.RecyclerTouchListener() {
            @Override
            public void onClickItem(View v, int position) {
                precio_vegetales=adapter_vegetales.getPrecioTotal();
                actualizarPrecio();
            }
        });

        //modoVegetariano
        checkBox_modo_vegetariano =(CheckBox)findViewById(R.id.activity_detalles_checkbox);
        checkBox_modo_vegetariano.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    precio_modoV=precioMV_internet;
                    ocultarCardViews(true);
                }else{
                    precio_modoV=0;
                    ocultarCardViews(false);
                }
                actualizarPrecio();
            }
        });
        TextView textViewCB=(TextView)findViewById(R.id.activity_detalles_precioMV);
        String textoCB="+"+precioMV_internet+" CUP";
        textViewCB.setText(textoCB);

    }
    private void ocultarCardViews(boolean ocultar){
        CardView cv_embutidos=(CardView)findViewById(R.id.detalles_cardview_embutidos);
        CardView cv_quesos=(CardView)findViewById(R.id.detalles_cardview_quesos);

        if(ocultar){
            cv_embutidos.setVisibility(View.GONE);
            cv_quesos.setVisibility(View.GONE);
        }else{
            cv_embutidos.setVisibility(View.VISIBLE);
            cv_quesos.setVisibility(View.VISIBLE);
        }
    }

    //Precio
    private void actualizarPrecio(){
        if(!checkBox_modo_vegetariano.isChecked()) {
            precioTotal = precio_panes + precio_embutidos + precio_quesos + precio_salsas + precio_vegetales + precio_modoV;
            TextView textView = (TextView) findViewById(R.id.detalles_TVPrecio);
            String texto = getString(R.string.precio) + " " + precioTotal + " CUP";
            textView.setText(texto);
        }else{
            precioTotal = precio_panes+precio_salsas + precio_vegetales + precio_modoV;
            TextView textView = (TextView) findViewById(R.id.detalles_TVPrecio);
            String texto = getString(R.string.precio) + " " + precioTotal + " CUP";
            textView.setText(texto);
        }
    }
    

    //Añadir al carrito
    public void detalles_click_anadir(View v){
        if(Carrito_Elementos.getCantidadSandwich()<5) {
            if (precio_panes != 0) {
                //Pan seleccionado

                if (precio_modoV == 0) {
                    //Modo vegetariano desactivado

                    if ((precio_embutidos + precio_quesos) != 0) {
                        //Embutidos o quesos seleccionados
                        finalizar_proceso();
                    } else {
                        //Embutidos o quesos no seleccionados
                        showAlertDialogSinEmbutidos();
                    }


                } else {
                    //Modo vegetariano activado
                    finalizar_proceso();
                }
            } else {
                //Pan no seleccionado
                showAlertDialogSinPan();
            }
        }else{
            showAlertDialogDemasiados();
        }

    }
    private void finalizar_proceso(){
        LayoutInflater inflater = LayoutInflater.from(Activity_Detalles.this);
        final View view = inflater.inflate(R.layout.li_cuantos_sandwich, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Detalles.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        //Declaraciones
        EditText edittext=(EditText) view.findViewById(R.id.li_cuantos_sandwiches_et);
        Button button=(Button) view.findViewById(R.id.li_cuantos_sandwiches_btn);
        ImageView cruzEsquina=(ImageView)view.findViewById(R.id.li_cuantos_sandwiches_cerrar);


        //Listener
        cruzEsquina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edittext.getText().toString().trim().isEmpty()) {
                    alertDialog.dismiss();
                    enviarAlCarrito(Integer.parseInt(edittext.getText().toString()));
                }else{
                    edittext.setError(getString(R.string.campo_vacio));
                }
            }
        });

        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
    public void enviarAlCarrito(int cant){
        //Agregar Subproductos
        ArrayList<Pila_Sandwich_Subproductos> arrayList_subproductos=new ArrayList<>();

        //Pan
        int posicion_pan=adapter_panes.getPositionSeleccionada();
        arrayList_subproductos.add(new Pila_Sandwich_Subproductos(array_panes.get(posicion_pan).getNombre(),array_panes.get(posicion_pan).getPrecio()));

        //Embutidos
        if(!checkBox_modo_vegetariano.isChecked()){
            ArrayList<String> arrayList_posiciones_embutidos=adapter_embutidos.getPosiciones_seleccionadas();
            for (int f=0;f<arrayList_posiciones_embutidos.size();f++){
                int posicion=Integer.parseInt(arrayList_posiciones_embutidos.get(f));
                arrayList_subproductos.add(new Pila_Sandwich_Subproductos(array_embutidos.get(posicion).getNombre(),array_embutidos.get(posicion).getPrecio()));
            }
        }

        //Quesos
        if(!checkBox_modo_vegetariano.isChecked()){
            ArrayList<String> arrayList_posiciones_quesos=adapter_quesos.getPosiciones_seleccionadas();
            for (int f=0;f<arrayList_posiciones_quesos.size();f++){
                int posicion=Integer.parseInt(arrayList_posiciones_quesos.get(f));
                arrayList_subproductos.add(new Pila_Sandwich_Subproductos(array_quesos.get(posicion).getNombre(),array_quesos.get(posicion).getPrecio()));
            }
        }

        //Salsas
        ArrayList<String> arrayList_posiciones_salsas=adapter_salsas.getPosiciones_seleccionadas();
        for (int f=0;f<arrayList_posiciones_salsas.size();f++){
            int posicion=Integer.parseInt(arrayList_posiciones_salsas.get(f));
            arrayList_subproductos.add(new Pila_Sandwich_Subproductos(array_salsas.get(posicion).getNombre(),array_salsas.get(posicion).getPrecio()));
        }

        //Vegetales
        ArrayList<String> arrayList_posiciones_vegetales=adapter_vegetales.getPosiciones_seleccionadas();
        for (int f=0;f<arrayList_posiciones_vegetales.size();f++){
            int posicion=Integer.parseInt(arrayList_posiciones_vegetales.get(f));
            arrayList_subproductos.add(new Pila_Sandwich_Subproductos(array_vegetales.get(posicion).getNombre(),array_vegetales.get(posicion).getPrecio()));
        }

        //Finalizar
        Pila_Sandwiches pila_sandwiches = new Pila_Sandwiches(cant,precioTotal,arrayList_subproductos);
        Carrito_Elementos.addSandwich(pila_sandwiches);
        showAlertDialogIrACarrito();

    }


    public void showAlertDialogIrACarrito() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Detalles.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.producto_a_carrito_con_exito);
        builder.setMessage(R.string.seguir_agregando_o_dirijirse_a_este);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.ir_a_carrito, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent=new Intent(Activity_Detalles.this,Activity_Carrito.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.continuar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        //create the alert dialog and show it
        builder.create().show();
    }
    public void showAlertDialogSinPan() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Detalles.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.sandwich_no_admitido);
        builder.setMessage(R.string.debe_seleccionar_pan);
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
    public void showAlertDialogSinEmbutidos() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Detalles.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.sandwich_no_admitido);
        builder.setMessage(R.string.debe_selecc_embutido);
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
    public void showAlertDialogDemasiados() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Detalles.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.demasiados_sandwiches_diferentes);
        builder.setMessage(R.string.solo_colocar_5_sandwiches);
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


}
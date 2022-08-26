package com.example.elemperadorvedado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_Menu extends AppCompatActivity {

    //Recycler
    private RecyclerView recycler;
    private ArrayList<Pila_Menu> array_pila_menu;
    private AdaptadorR_Menu adapter;
    private String categoriaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Toolbar
        Toolbar toolbar=(Toolbar)findViewById(R.id.menu_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        categoriaSeleccionada=getIntent().getBundleExtra("bdle_categoria_menu").getString("categoria");

        //recycler
        recycler=(RecyclerView) findViewById(R.id.menu_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(Activity_Menu.this,3));
        array_pila_menu =new ArrayList<>();
        adapter=new AdaptadorR_Menu(this, array_pila_menu,false);
        cargardatosInternet();


    }

    private void cargardatosInternet(){
        ProgressDialog pDialog = ProgressDialog.show(Activity_Menu.this, getString(R.string.cargando_datos), getString(R.string.espere), false, false);
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerRecyclerMenu.php?Categoria="+categoriaSeleccionada, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                array_pila_menu.clear();
                try{
                    JSONArray array =new JSONArray(response);
                    for (int i=0; i<array.length();i++) {

                        JSONObject json_objet = array.getJSONObject(i);
                        if(json_objet.getString("Producto").length()>0) {
                            if(json_objet.getInt("Estado")==1) {
                                array_pila_menu.add(new Pila_Menu(
                                        json_objet.getString("Producto"),
                                        json_objet.getDouble("Precio"),
                                        json_objet.getInt("Estado")
                                ));
                            }
                        }
                    }

                    adapter=new AdaptadorR_Menu(Activity_Menu.this, array_pila_menu,false);
                    recycler.setAdapter(adapter);
                    adapter.setClickListener(new AdaptadorR_Menu.RecyclerTouchListener(){
                        @Override
                        public void onClickItem(View v, int position,Drawable drawable, int color) {
                            confirmar_compra(position,drawable,color);
                        }
                    });

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
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Menu.this);
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

    public void showAlertDialogIrACarrito() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Menu.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.producto_a_carrito_con_exito);
        builder.setMessage(R.string.seguir_agregando_o_dirijirse_a_este);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.ir_a_carrito, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent=new Intent(Activity_Menu.this,Activity_Carrito.class);
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

    public void menu_click_carrrito(View v){
        Intent intent=new Intent(Activity_Menu.this,Activity_Carrito.class);
        startActivity(intent);
    }

    public void confirmar_compra(int position, Drawable imagen_traida, int color){

        LayoutInflater inflater = LayoutInflater.from(Activity_Menu.this);
        final View view = inflater.inflate(R.layout.li_confirmar_compra, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Menu.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        //Declaraciones
        ImageView imagenview=(ImageView)view.findViewById(R.id.conf_compra_imagen);
        TextView textview=(TextView) view.findViewById(R.id.conf_compra_nombre_producto);
        EditText edittext=(EditText) view.findViewById(R.id.conf_compra_cantidad);
        Button button=(Button) view.findViewById(R.id.conf_compra_boton);
        ImageView cruzEsquina=(ImageView)view.findViewById(R.id.conf_compra_cerrar);
        ImageView imagenview_back=(ImageView)view.findViewById(R.id.conf_compra_imagen_backg);

        //Llenado
        imagenview_back.setBackgroundColor(color);
        imagenview.setImageDrawable(imagen_traida);
        textview.setText(array_pila_menu.get(position).getNombre());
        String textoBoton=getString(R.string.añadir_al_carrito) + " " +array_pila_menu.get(position).getPrecio()+" CUP";
        button.setText(textoBoton);

        //Listener
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str=edittext.getText().toString();
                if(!str.trim().isEmpty()){
                    if(Integer.parseInt(str)!=0) {
                        button.setEnabled(true);
                        double precio = Integer.parseInt(str) * array_pila_menu.get(position).getPrecio();
                        String textButton = getString(R.string.añadir_al_carrito) + " " + precio + " CUP";
                        button.setText(textButton);
                    }else{
                        button.setEnabled(false);
                        button.setText(R.string.selecc_ctdad_deseada);
                    }
                }else{
                    button.setEnabled(false);
                    button.setText(R.string.selecc_ctdad_deseada);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cruzEsquina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Carrito_Elementos.getCantidadElementos()<30) {
                    String nombre = array_pila_menu.get(position).getNombre();
                    double precio = array_pila_menu.get(position).getPrecio();
                    Carrito_Elementos.addElemento(new Pila_Elementos(nombre, Integer.parseInt(edittext.getText().toString()), precio));
                    alertDialog.dismiss();
                    showAlertDialogIrACarrito();
                }else{
                    showAlertDialogDemasiados();
                }
            }
        });

        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }

    public void showAlertDialogDemasiados() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Menu.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.demasiados_elementos);
        builder.setMessage(R.string.solo_colocar_30_elementos);
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
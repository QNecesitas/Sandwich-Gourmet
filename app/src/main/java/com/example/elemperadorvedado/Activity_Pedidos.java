package com.example.elemperadorvedado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_Pedidos extends AppCompatActivity {

    private ArrayList<Pila_Pedidos> arrayList_pedidos;
    private RecyclerView recyclerViewPedidos;
    private AdaptadorR_Pedidos adaptadorR_pedidos;
    private String filtro="Todos";
    private final int TIMEPO_DE_ESPERA=15000;
    private boolean inActivity;
    private Handler handler;
    private ExtendedFloatingActionButton floatingButtonVaciar;
    private int contadorVecesErrorConex=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        try {

            //Toolbar
            Toolbar toolbar=(Toolbar)findViewById(R.id.activity_pedidos_categ_toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            inActivity=true;

            arrayList_pedidos = new ArrayList<>();
            recyclerViewPedidos = (RecyclerView) findViewById(R.id.pedidos_recyler);
            recyclerViewPedidos.setHasFixedSize(true);
            recyclerViewPedidos.setLayoutManager(new LinearLayoutManager(Activity_Pedidos.this));
            floatingButtonVaciar=(ExtendedFloatingActionButton)findViewById(R.id.pedidos_button_vaciar);



        } catch (Exception e) {
            Toast.makeText(Activity_Pedidos.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    //Internet
    private void cargardatosInternet() {
        ProgressDialog pdialog = ProgressDialog.show(Activity_Pedidos.this, getString(R.string.cargando_datos), getString(R.string.por_favor_espere), false, false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP + "ObtenerRecyclerPedidosJs.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                arrayList_pedidos.clear();
                try {
                    arrayList_pedidos.clear();
                    JSONArray array = new JSONArray(response);
                    llenarArrays(array);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        contadorVecesErrorConex=0;
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void actualizarDatosInternet() {
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP + "ObtenerRecyclerPedidosJs.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    contadorVecesErrorConex=0;
                    arrayList_pedidos.clear();
                    JSONArray array = new JSONArray(response);
                    llenarArrays(array);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(contadorVecesErrorConex==4){
                            contadorVecesErrorConex=0;
                            showAlertDialogNoInternet();
                        }else{
                            contadorVecesErrorConex++;
                        }
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void llenarArrays(JSONArray jsonArrayPedidos) {
        try {
            for (int posPedidos = 0; posPedidos < jsonArrayPedidos.length(); posPedidos++) {

                //Declaraciones
                Pila_Pedidos pila_este_pedido = new Pila_Pedidos();
                JSONObject jsonObject_este_pedido = jsonArrayPedidos.getJSONObject(posPedidos);

                //Acciones
                llenarCamposGenerales(jsonObject_este_pedido, pila_este_pedido);
                llenarElementos(jsonObject_este_pedido,pila_este_pedido);
                llenarSandwiches(jsonObject_este_pedido,pila_este_pedido);

                //Finalizar
                arrayList_pedidos.add(pila_este_pedido);
                actualizarRecycler();

            }
        } catch (Exception e) {
            Toast.makeText(Activity_Pedidos.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }
    private void llenarCamposGenerales(JSONObject jsonObject_este_pedido, Pila_Pedidos pila_este_pedido) {
        try {
            if (jsonObject_este_pedido.getString("Id").length() > 0) {

                //Extraer
                String idDeCadaCliente = jsonObject_este_pedido.getString("Id");
                String direccionDeCadaCliente = jsonObject_este_pedido.getString("Direccion");
                String horaEntregaDeCadaCliente = jsonObject_este_pedido.getString("Hora");
                String latitudDeCadaCliente = jsonObject_este_pedido.getString("Latitud");
                String longitudDeCadaCliente = jsonObject_este_pedido.getString("Longitud");
                String precioDeCadaCliente = jsonObject_este_pedido.getString("Precio");
                String estadoDeCadaCliente = jsonObject_este_pedido.getString("Estado");
                String explicacionDeCadaCliente = jsonObject_este_pedido.getString("Explicacion");
                String formaPagoDeCadaCliente = jsonObject_este_pedido.getString("FormaPago");
                String numCelDeCadaCliente = jsonObject_este_pedido.getString("NumCel");
                String numWappDeCadaCliente = jsonObject_este_pedido.getString("NumWapp");


                //LLenar
                pila_este_pedido.setId(idDeCadaCliente);
                pila_este_pedido.setDireccion(direccionDeCadaCliente);
                pila_este_pedido.setHora(horaEntregaDeCadaCliente);
                pila_este_pedido.setLatitud(latitudDeCadaCliente);
                pila_este_pedido.setLongitud(longitudDeCadaCliente);
                pila_este_pedido.setPrecio(precioDeCadaCliente);
                pila_este_pedido.setEstado(estadoDeCadaCliente);
                pila_este_pedido.setExplicacion(explicacionDeCadaCliente);
                pila_este_pedido.setFormaPago(formaPagoDeCadaCliente);
                pila_este_pedido.setNumCel(numCelDeCadaCliente);
                pila_este_pedido.setNumWapp(numWappDeCadaCliente);

            }
        } catch (Exception e) {
            Toast.makeText(Activity_Pedidos.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }
    private void llenarElementos(JSONObject jsonObject_este_pedido, Pila_Pedidos pila_este_pedido) {
        try {
            if (jsonObject_este_pedido.getString("Elementos").length() > 2) {

                //Extraer
                ArrayList<Pila_Elementos> arrayList_elementos=new ArrayList<>();
                String elementosDeCadaCliente = jsonObject_este_pedido.getString("Elementos");
                JSONArray JSONArrayElementos = new JSONArray(elementosDeCadaCliente);
                for (int x=0;x<JSONArrayElementos.length();x++){
                    JSONObject jsonObject_elemento_especifico=JSONArrayElementos.getJSONObject(x);

                    String nombre=jsonObject_elemento_especifico.getString("nombre");
                    int cantidad=jsonObject_elemento_especifico.getInt("cantidad");
                    double precio=jsonObject_elemento_especifico.getDouble("precioProducto");

                    arrayList_elementos.add(new Pila_Elementos(nombre,cantidad,precio));
                }

                //LLenar
                pila_este_pedido.setArray_varios_elementos(arrayList_elementos);

            }
        } catch (Exception e) {
            Toast.makeText(Activity_Pedidos.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }
    private void llenarSandwiches(JSONObject jsonObject_este_pedido, Pila_Pedidos pila_este_pedido) {
        try {
            if (jsonObject_este_pedido.getString("Id").length() > 0) {
                //General de los sandwiches
                ArrayList<Pila_Sandwiches> arrayList_sandwiches=new ArrayList<>();


                //Sandwich1
                String sandwich1DeCadaCliente = jsonObject_este_pedido.getString("Sandwich1");
                if(!sandwich1DeCadaCliente.equals("no")) {
                    JSONObject jsonObject_sandwich1 = new JSONObject(sandwich1DeCadaCliente);
                    //Cantidad precioProducto subproductoSandwiches(JSON_Array)
                    int cantidad_sandwich1 = jsonObject_sandwich1.getInt("cantidad");
                    double precio_sandwich1 = jsonObject_sandwich1.getDouble("precioProducto");
                    String subproducto_sandwiches_sandwich1 = jsonObject_sandwich1.getString("subproducto_sandwiches");
                    //Extraer subproductos
                    JSONArray jsonArray_subproductos_sandwich1 = new JSONArray(subproducto_sandwiches_sandwich1);
                    ArrayList<Pila_Sandwich_Subproductos> array_subproductos_sandwich1 = new ArrayList<>();
                    for (int x = 0; x < jsonArray_subproductos_sandwich1.length(); x++) {
                        JSONObject jsonObject_subproducto_especifico = jsonArray_subproductos_sandwich1.getJSONObject(x);

                        String nombre = jsonObject_subproducto_especifico.getString("nombre");
                        double precioProducto = jsonObject_subproducto_especifico.getDouble("precioProducto");
                        array_subproductos_sandwich1.add(new Pila_Sandwich_Subproductos(nombre, precioProducto));

                    }
                    arrayList_sandwiches.add(new Pila_Sandwiches(cantidad_sandwich1, precio_sandwich1, array_subproductos_sandwich1));
                }


                //Sandwich2
                String sandwich2DeCadaCliente = jsonObject_este_pedido.getString("Sandwich2");
                if(!sandwich2DeCadaCliente.equals("no")) {
                    JSONObject jsonObject_sandwich2 = new JSONObject(sandwich2DeCadaCliente);
                    //Cantidad precioProducto subproductoSandwiches(JSON_Array)
                    int cantidad_sandwich2 = jsonObject_sandwich2.getInt("cantidad");
                    double precio_sandwich2 = jsonObject_sandwich2.getDouble("precioProducto");
                    String subproducto_sandwiches_sandwich2 = jsonObject_sandwich2.getString("subproducto_sandwiches");
                    //Extraer subproductos
                    JSONArray jsonArray_subproductos_sandwich2 = new JSONArray(subproducto_sandwiches_sandwich2);
                    ArrayList<Pila_Sandwich_Subproductos> array_subproductos_sandwich2 = new ArrayList<>();
                    for (int x = 0; x < jsonArray_subproductos_sandwich2.length(); x++) {
                        JSONObject jsonObject_subproducto_especifico = jsonArray_subproductos_sandwich2.getJSONObject(x);

                        String nombre = jsonObject_subproducto_especifico.getString("nombre");
                        double precioProducto = jsonObject_subproducto_especifico.getDouble("precioProducto");
                        array_subproductos_sandwich2.add(new Pila_Sandwich_Subproductos(nombre, precioProducto));

                    }
                    arrayList_sandwiches.add(new Pila_Sandwiches(cantidad_sandwich2, precio_sandwich2, array_subproductos_sandwich2));
                }


                //Sandwich3
                String sandwich3DeCadaCliente = jsonObject_este_pedido.getString("Sandwich3");
                if(!sandwich3DeCadaCliente.equals("no")) {
                    JSONObject jsonObject_sandwich3 = new JSONObject(sandwich3DeCadaCliente);
                    //Cantidad precioProducto subproductoSandwiches(JSON_Array)
                    int cantidad_sandwich3 = jsonObject_sandwich3.getInt("cantidad");
                    double precio_sandwich3 = jsonObject_sandwich3.getDouble("precioProducto");
                    String subproducto_sandwiches_sandwich3 = jsonObject_sandwich3.getString("subproducto_sandwiches");
                    //Extraer subproductos
                    JSONArray jsonArray_subproductos_sandwich3 = new JSONArray(subproducto_sandwiches_sandwich3);
                    ArrayList<Pila_Sandwich_Subproductos> array_subproductos_sandwich3 = new ArrayList<>();
                    for (int x = 0; x < jsonArray_subproductos_sandwich3.length(); x++) {
                        JSONObject jsonObject_subproducto_especifico = jsonArray_subproductos_sandwich3.getJSONObject(x);

                        String nombre = jsonObject_subproducto_especifico.getString("nombre");
                        double precioProducto = jsonObject_subproducto_especifico.getDouble("precioProducto");
                        array_subproductos_sandwich3.add(new Pila_Sandwich_Subproductos(nombre, precioProducto));

                    }
                    arrayList_sandwiches.add(new Pila_Sandwiches(cantidad_sandwich3, precio_sandwich3, array_subproductos_sandwich3));
                }


                //Sandwich4
                String sandwich4DeCadaCliente = jsonObject_este_pedido.getString("Sandwich4");
                if(!sandwich4DeCadaCliente.equals("no")) {
                    JSONObject jsonObject_sandwich4 = new JSONObject(sandwich4DeCadaCliente);
                    //Cantidad precioProducto subproductoSandwiches(JSON_Array)
                    int cantidad_sandwich4 = jsonObject_sandwich4.getInt("cantidad");
                    double precio_sandwich4 = jsonObject_sandwich4.getDouble("precioProducto");
                    String subproducto_sandwiches_sandwich4 = jsonObject_sandwich4.getString("subproducto_sandwiches");
                    //Extraer subproductos
                    JSONArray jsonArray_subproductos_sandwich4 = new JSONArray(subproducto_sandwiches_sandwich4);
                    ArrayList<Pila_Sandwich_Subproductos> array_subproductos_sandwich4 = new ArrayList<>();
                    for (int x = 0; x < jsonArray_subproductos_sandwich4.length(); x++) {
                        JSONObject jsonObject_subproducto_especifico = jsonArray_subproductos_sandwich4.getJSONObject(x);

                        String nombre = jsonObject_subproducto_especifico.getString("nombre");
                        double precioProducto = jsonObject_subproducto_especifico.getDouble("precioProducto");
                        array_subproductos_sandwich4.add(new Pila_Sandwich_Subproductos(nombre, precioProducto));

                    }
                    arrayList_sandwiches.add(new Pila_Sandwiches(cantidad_sandwich4, precio_sandwich4, array_subproductos_sandwich4));
                }


                //Sandwich5
                String sandwich5DeCadaCliente = jsonObject_este_pedido.getString("Sandwich5");
                if(!sandwich5DeCadaCliente.equals("no")) {
                    JSONObject jsonObject_sandwich5 = new JSONObject(sandwich5DeCadaCliente);
                    //Cantidad precioProducto subproductoSandwiches(JSON_Array)
                    int cantidad_sandwich5 = jsonObject_sandwich5.getInt("cantidad");
                    double precio_sandwich5 = jsonObject_sandwich5.getDouble("precioProducto");
                    String subproducto_sandwiches_sandwich5 = jsonObject_sandwich5.getString("subproducto_sandwiches");
                    //Extraer subproductos
                    JSONArray jsonArray_subproductos_sandwich5 = new JSONArray(subproducto_sandwiches_sandwich5);
                    ArrayList<Pila_Sandwich_Subproductos> array_subproductos_sandwich5 = new ArrayList<>();
                    for (int x = 0; x < jsonArray_subproductos_sandwich5.length(); x++) {
                        JSONObject jsonObject_subproducto_especifico = jsonArray_subproductos_sandwich5.getJSONObject(x);

                        String nombre = jsonObject_subproducto_especifico.getString("nombre");
                        double precioProducto = jsonObject_subproducto_especifico.getDouble("precioProducto");
                        array_subproductos_sandwich5.add(new Pila_Sandwich_Subproductos(nombre, precioProducto));

                    }
                    arrayList_sandwiches.add(new Pila_Sandwiches(cantidad_sandwich5, precio_sandwich5, array_subproductos_sandwich5));
                }


                //LLenar
                pila_este_pedido.setArray_varios_sandwiches(arrayList_sandwiches);
            }
        } catch (Exception e) {
            Toast.makeText(Activity_Pedidos.this, getString(R.string.error)+e, Toast.LENGTH_SHORT).show();
        }
    }
    private void showAlertDialogNoInternet() {
        //init alert dialog
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Activity_Pedidos.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.revise_conx);
        inActivity=false;
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onResume();
                dialog.dismiss();
                cargardatosInternet();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    private void actualizarRecycler() {
        adaptadorR_pedidos = new AdaptadorR_Pedidos(Activity_Pedidos.this, arrayList_pedidos,filtro);
        adaptadorR_pedidos.setUbicListener(new AdaptadorR_Pedidos.RecyclerUbicacionListener() {
            @Override
            public void onClickUbicacion(int position) {
                clickUbicacion(position);
            }
        });
        adaptadorR_pedidos.setAceptarListener(new AdaptadorR_Pedidos.RecyclerAceptarListener() {
            @Override
            public void onClickAceptar(int position) {
                clickAceptar(position);
            }
        });
        adaptadorR_pedidos.setCancelarListener(new AdaptadorR_Pedidos.RecyclerCancelarListener() {
            @Override
            public void onClickCancelar(int position) {
                clickCancelar(position);
            }
        });
        adaptadorR_pedidos.setFinalizarlistener(new AdaptadorR_Pedidos.RecyclerFinalizarListener() {
            @Override
            public void onClickFinalizar(int position) {
                clickFinalizar(position);
            }
        });
        recyclerViewPedidos.setAdapter(adaptadorR_pedidos);

        if(arrayList_pedidos.isEmpty()){
            LinearLayout layout_imagen_vsacio=(LinearLayout) findViewById(R.id.pedidos_imagen_empty);
            layout_imagen_vsacio.setVisibility(View.VISIBLE);
        }else{
            LinearLayout layout_imagen_vsacio=(LinearLayout) findViewById(R.id.pedidos_imagen_empty);
            layout_imagen_vsacio.setVisibility(View.GONE);
        }

    }
    private void clickUbicacion(int position){
        if(Utiles.Internet.isOnline(Activity_Pedidos.this,true)) {
            String latitud = arrayList_pedidos.get(position).getLatitud();
            String longitud = arrayList_pedidos.get(position).getLongitud();

            Intent intent = new Intent(Activity_Pedidos.this, Activity_Mapa_Colocar.class);
            intent.putExtra("Latitud", latitud);
            intent.putExtra("Longitud", longitud);
            startActivity(intent);
        }
    }
    private void clickAceptar(int position){
        if(Utiles.Internet.isOnline(Activity_Pedidos.this,true)) {
            aceptarPedidoInternet(arrayList_pedidos.get(position).getId(), position);
        }
    }
    private void clickCancelar(int position){
        if(Utiles.Internet.isOnline(Activity_Pedidos.this,true)) {
            li_cancelar(arrayList_pedidos.get(position).getId(), position);
        }
    }
    private void clickFinalizar(int position){
        if(Utiles.Internet.isOnline(Activity_Pedidos.this,true)) {
            finalizarPedidoInternet(arrayList_pedidos.get(position).getId(), position);
        }
    }
    public void pedidos_click_vaciar_internet(View view){
        eliminarPedidosInternet();
    }

    //Cambio de estado
    private void li_cancelar(String id, int position){
        LayoutInflater inflater=LayoutInflater.from(Activity_Pedidos.this);
        View vista= inflater.inflate(R.layout.li_pedido_cancelado, null);
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(Activity_Pedidos.this);
        builder.setView(vista);
        final androidx.appcompat.app.AlertDialog alertDialog=builder.create();

        //Declaraciones
        EditText editText=(EditText) vista.findViewById(R.id.li_pedido_cancelado_et_respuesta_cliente);
        TextView btn_cancelar=(TextView)vista.findViewById(R.id.li_pedido_cancelado_tv_cancelar);
        TextView btn_aceptar=(TextView)vista.findViewById(R.id.li_pedido_cancelado_tv_aceptar);


        //Listene
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().trim().isEmpty()){
                    alertDialog.dismiss();
                    cancelarPedidoInternet(id,position,editText.getText().toString());
                }else{
                    editText.setError(getString(R.string.campo_vacio));
                }
            }
        });

        //Finalizado
        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
    private void aceptarPedidoInternet(String id, int posicion) {
        ProgressDialog pdialog = ProgressDialog.show(Activity_Pedidos.this, getString(R.string.realizando_operacion), getString(R.string.por_favor_espere), false, false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP + "ActualizarPedidoEstado.php?Id="+id+"&Estado=Aceptado&Explicacion=no", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                arrayList_pedidos.get(posicion).setEstado("Aceptado");
                actualizarRecycler();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void cancelarPedidoInternet(String id, int posicion, String explicacion) {
        ProgressDialog pdialog = ProgressDialog.show(Activity_Pedidos.this, getString(R.string.realizando_operacion), getString(R.string.por_favor_espere), false, false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP + "ActualizarPedidoEstado.php?Id="+id+"&Estado=Cancelado&Explicacion="+explicacion, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                arrayList_pedidos.get(posicion).setEstado("Cancelado");
                actualizarRecycler();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void finalizarPedidoInternet(String id, int posicion) {
        ProgressDialog pdialog = ProgressDialog.show(Activity_Pedidos.this, getString(R.string.realizando_operacion), getString(R.string.por_favor_espere), false, false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP + "ActualizarPedidoEstado.php?Id="+id+"&Estado=Finalizado&Explicacion=no", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                arrayList_pedidos.get(posicion).setEstado("Finalizado");
                actualizarRecycler();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void eliminarPedidosInternet() {
        ProgressDialog pdialog = ProgressDialog.show(Activity_Pedidos.this, getString(R.string.realizando_operacion), getString(R.string.por_favor_espere), false, false);

        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP + "EliminarPedidos.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pdialog.dismiss();
                finish();

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pdialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    public void pedidos_click_filtros(View v){
        final PopupMenu popupMenu=new PopupMenu(Activity_Pedidos.this,v);
        popupMenu.getMenuInflater().inflate(R.menu.filtro_pedidos, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.menu_pedidos_todos){
                    filtro="Todos";
                    actualizarRecycler();
                    floatingButtonVaciar.setVisibility(View.GONE);
                }else if(item.getItemId()==R.id.menu_pedidos_aceptados){
                    filtro="Aceptados";
                    actualizarRecycler();
                    floatingButtonVaciar.setVisibility(View.GONE);
                }else if (item.getItemId()==R.id.menu_pedidos_cancelados){
                    filtro="Cancelados";
                    actualizarRecycler();
                    floatingButtonVaciar.setVisibility(View.GONE);
                }else if (item.getItemId()==R.id.menu_pedidos_espera){
                    filtro="En espera";
                    actualizarRecycler();
                    floatingButtonVaciar.setVisibility(View.GONE);
                }else if(item.getItemId()==R.id.menu_pedidos_finalizados){
                    filtro="Finalizados";
                    actualizarRecycler();
                    floatingButtonVaciar.setVisibility(View.VISIBLE);
                }

                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onPause(){
        super.onPause();
        inActivity=false;
    }

    @Override
    public void onResume(){
        super.onResume();
        inActivity=true;

        //Handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.arg1 == 2) {
                    actualizarDatosInternet();
                }
            }
        };

        //Thread
        Thread thread = new Thread(() -> {
            while (inActivity) {
                Message message = Message.obtain();
                message.arg1 = 2;
                handler.sendMessage(message);

                try {
                    Thread.sleep(TIMEPO_DE_ESPERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
package com.example.elemperadorvedado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class Activity_Personalizar_Menu extends AppCompatActivity {

    //Recycler
    private RecyclerView recycler_menu, recycler_selector, recycler_subselector;
    private ArrayList<Pila_Menu>  array_menus;
    private ArrayList<Pila_Selector_PersMenu> array_selector, array_subselector;
    private AdaptadorR_Menu adapter_menu;
    private AdaptadorR_PersMenu_SelectorSelection adapter_selector, adapter_subselector;

    //Internet
    private ProgressDialog progressDialogCargando, progressDialogSubiendo;
    private String categoriaSeleccionada;
    private String subcategoriaSeleccionada="panes";
    private final int ANCHO_DE_FOTO_A_SUBIR=400;
    private final int ALTO_DE_FOTO_A_SUBIR=400;
    private final int INTENT_RESULT_GALERIA=5;
    private final int PERMISO_GALERIA=3;
    private Uri uriLLegadaRecortada;

    //Agregar
    private ImageView li_imagen;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizar_menu);

        //Toolbar
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_personalizar_menu_appBar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_personalizar_menu_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Otros
        floatingActionButton=(FloatingActionButton)findViewById(R.id.activity_personalizar_menu_fbutton);

        //Recycler
        recycler_menu=(RecyclerView) findViewById(R.id.activity_personalizar_menu_recycler_general);
        recycler_selector=(RecyclerView) findViewById(R.id.activity_personalizar_menu_recycler_selector);
        recycler_subselector=(RecyclerView) findViewById(R.id.activity_personalizar_menu_recycler_subselector);
        recycler_menu.setHasFixedSize(true);
        recycler_selector.setHasFixedSize(true);
        recycler_subselector.setHasFixedSize(true);
        recycler_menu.setLayoutManager(new GridLayoutManager(Activity_Personalizar_Menu.this,3));
        recycler_selector.setLayoutManager(new LinearLayoutManager(Activity_Personalizar_Menu.this,LinearLayoutManager.HORIZONTAL,false));
        recycler_subselector.setLayoutManager(new LinearLayoutManager(Activity_Personalizar_Menu.this,LinearLayoutManager.HORIZONTAL,false));
        array_menus=new ArrayList<>();
        array_selector=new ArrayList<>();
        array_subselector =new ArrayList<>();

        cargardatosInternet();

    }

    //Carga de datos
    private void cargardatosInternet(){
        progressDialogCargando=ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.cargando_datos), getString(R.string.por_favor_espere), false, false);
        cargarSubcategoriasParaSubselector();
    }
    //Subselector
    private void cargarSubcategoriasParaSubselector(){
        array_subselector.clear();
        array_subselector.add(new Pila_Selector_PersMenu("Panes","panes"));
        array_subselector.add(new Pila_Selector_PersMenu("Carnes y atún","embutidos"));
        array_subselector.add(new Pila_Selector_PersMenu("Quesos","quesos"));
        array_subselector.add(new Pila_Selector_PersMenu("Salsas","salsas"));
        array_subselector.add(new Pila_Selector_PersMenu("Vegetales","vegetales"));
        array_subselector.add(new Pila_Selector_PersMenu("Modo Vegetariano","modo_vegetariano"));

        actualizarRecyclerParaSubselector();
    }
    private void actualizarRecyclerParaSubselector(){
        adapter_subselector=new AdaptadorR_PersMenu_SelectorSelection(Activity_Personalizar_Menu.this,array_subselector);
        adapter_subselector.setListener(new AdaptadorR_PersMenu_SelectorSelection.RecyclerTouchListener() {
            @Override
            public void onClick(View v, int position) {
                if (Utiles.Internet.isOnline(Activity_Personalizar_Menu.this, true)) {
                    subcategoriaSeleccionada = array_subselector.get(position).getValorEnBd();
                    progressDialogCargando = ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.cargando_datos), getString(R.string.por_favor_espere), false, false);
                    cargarElementosDelMenu(categoriaSeleccionada, subcategoriaSeleccionada);

                    //Modo vegetariano
                    if (categoriaSeleccionada.equals("Sandwich")) {
                        if (subcategoriaSeleccionada.equals("modo_vegetariano")) {
                            floatingActionButton.setVisibility(View.GONE);
                        } else {
                            floatingActionButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }


                }
            }
        });
        recycler_subselector.setAdapter(adapter_subselector);

        cargarCategoriasParaSelector();
    }
    //Selector
    private void cargarCategoriasParaSelector(){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerRecyclerCategorias.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                array_selector.clear();
                try{
                    JSONArray array =new JSONArray(response);
                    for (int i=0; i<array.length();i++) {

                        JSONObject json_objet = array.getJSONObject(i);
                        if(json_objet.getString("Nombre").length()>0) {
                            if(json_objet.getString("Nombre").equals(getString(R.string.sandwich))){
                                array_selector.add(0,new Pila_Selector_PersMenu(
                                        json_objet.getString("Nombre"),
                                        json_objet.getString("Nombre")
                                ));
                            }else {
                                array_selector.add(new Pila_Selector_PersMenu(
                                        json_objet.getString("Nombre"),
                                        json_objet.getString("Nombre")
                                ));
                            }
                        }
                    }
                    actualizarRecyclerParaSelector();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogCargando.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void actualizarRecyclerParaSelector(){
        CardView cardView=(CardView)findViewById(R.id.activity_personalizar_menu_cv_subfiltros);

        categoriaSeleccionada=array_selector.get(0).getNombreVisible();
        adapter_selector=new AdaptadorR_PersMenu_SelectorSelection(Activity_Personalizar_Menu.this,array_selector);
        adapter_selector.setListener(new AdaptadorR_PersMenu_SelectorSelection.RecyclerTouchListener() {
            @Override
            public void onClick(View v, int position) {
                if(Utiles.Internet.isOnline(Activity_Personalizar_Menu.this,true)) {

                    categoriaSeleccionada = array_selector.get(position).getNombreVisible();
                    if (categoriaSeleccionada.equals("Sandwich")) {
                        cardView.setVisibility(View.VISIBLE);
                    } else {
                        cardView.setVisibility(View.GONE);
                    }
                    progressDialogCargando = ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.cargando_datos), getString(R.string.por_favor_espere), false, false);
                    cargarElementosDelMenu(categoriaSeleccionada, subcategoriaSeleccionada);

                    //Modo vegetariano
                    if (categoriaSeleccionada.equals("Sandwich")) {
                        if (subcategoriaSeleccionada.equals("modo_vegetariano")) {
                            floatingActionButton.setVisibility(View.GONE);
                        } else {
                            floatingActionButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        floatingActionButton.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        recycler_selector.setAdapter(adapter_selector);

        cargarElementosDelMenu(categoriaSeleccionada,subcategoriaSeleccionada);


    }
    //Menu
    private void cargarElementosDelMenu(String categoria, String subcategoria){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerRecyclerMenu.php?Categoria="+categoria, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    array_menus.clear();
                    JSONArray array =new JSONArray(response);
                    for (int i=0; i<array.length();i++) {

                        JSONObject json_objet = array.getJSONObject(i);
                        if(json_objet.getString("Producto").length()>0) {
                            if(categoria.equals("Sandwich")) {
                                if (json_objet.getString("Subcategoria").equals(subcategoria)){
                                    array_menus.add(new Pila_Menu(
                                            json_objet.getString("Producto"),
                                            json_objet.getDouble("Precio"),
                                            json_objet.getInt("Estado")));
                                }
                            }else{
                                array_menus.add(new Pila_Menu(
                                        json_objet.getString("Producto"),
                                        json_objet.getDouble("Precio"),
                                        json_objet.getInt("Estado")));
                            }
                        }
                    }

                    actualizarRecyclerParaMenu();

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogCargando.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void actualizarRecyclerParaMenu(){

        LinearLayout linearLayoutEmpty=(LinearLayout)findViewById(R.id.personalizar_menu_layout_imagen_empty);
        if(array_menus.isEmpty()){
            linearLayoutEmpty.setVisibility(View.VISIBLE);
        }else{
            linearLayoutEmpty.setVisibility(View.GONE);
        }

        adapter_menu=new AdaptadorR_Menu(Activity_Personalizar_Menu.this,array_menus,true);
        adapter_menu.setClickListener(new AdaptadorR_Menu.RecyclerTouchListener() {
            @Override
            public void onClickItem(View v, int position, Drawable drawable, int color) {
                final PopupMenu popupMenu=new PopupMenu(Activity_Personalizar_Menu.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.editar_eliminar, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.menu_editar_eliminar_tvEditar){
                            editarMenu(position,drawable);
                        }else if(item.getItemId()==R.id.menu_editar_eliminar_tvEliminar){
                            eliminarCategoria(position);
                        }else if(item.getItemId()==R.id.menu_editar_eliminar_tvCambiarVisibilidad){
                            cambiarVisibilidadElemento(position);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        recycler_menu.setAdapter(adapter_menu);
        progressDialogCargando.dismiss();
    }
    private void cambiarVisibilidadElemento(int position){
        if(Utiles.Internet.isOnline(Activity_Personalizar_Menu.this,true)) {
            progressDialogSubiendo = ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);

            if (array_menus.get(position).getEstado() == 0) {
                actualizarDatosMenuInternet(array_menus.get(position).getNombre(),array_menus.get(position).getNombre(),position,array_menus.get(position).getPrecio(),1);
            } else {
                actualizarDatosMenuInternet(array_menus.get(position).getNombre(),array_menus.get(position).getNombre(),position,array_menus.get(position).getPrecio(),0);
            }
        }
    }

    //Generalidades
    public void showAlertDialogNoInternet() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Personalizar_Menu.this);
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

    //Agregar elemento al menu
    public void personalizar_menu_click_fab(View view) {
        if(array_menus.size()<100) {
            liAgregarElemento();
        }else{
            showAlertDialogMuchosElementos();
        }
    }
    private void liAgregarElemento(){
        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Personalizar_Menu.this);
        View vista = layoutInflater.inflate(R.layout.li_personalizar_menu_agregar_o_editar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Personalizar_Menu.this);
        builder.setView(vista);
        AlertDialog alertDialog = builder.create();

        //Declaraciones
        li_imagen = (ImageView) vista.findViewById(R.id.li_persmenu_IV);
        EditText et_nombre = (EditText) vista.findViewById(R.id.li_personalizar_menu_et_nombre);
        EditText et_precio = (EditText) vista.findViewById(R.id.li_personalizar_menu_et_precio);
        TextView tv_cancelar = (TextView) vista.findViewById(R.id.li_personalizar_menu_tv_cancelar);
        TextView tv_aceptar = (TextView) vista.findViewById(R.id.li_personalizar_menu_tv_aceptar);
        CardView cardView_general=(CardView)vista.findViewById(R.id.li_personalizar_menu_cv_imagen);
        TextView tv_categoria=(TextView)vista.findViewById(R.id.li_personalizar_menu_tv_categoria);
        TextView tv_subcategoria=(TextView)vista.findViewById(R.id.li_personalizar_menu_tv_subcategoria);


        //LLenar
        if(categoriaSeleccionada.equals("Sandwich")){
            tv_subcategoria.setVisibility(View.VISIBLE);
            String subcategoria=array_subselector.get(adapter_subselector.getPositionSeleccionada()).nombreVisible;
            tv_subcategoria.setText(subcategoria);
            tv_categoria.setText(categoriaSeleccionada);
        }else{
            tv_subcategoria.setVisibility(View.GONE);
            tv_categoria.setText(categoriaSeleccionada);
        }

        //Listeners
        cardView_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escogerimagenGaleria();
            }
        });
        //boton de cancelar
        tv_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        //boton de aceptar
        tv_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utiles.Internet.isOnline(Activity_Personalizar_Menu.this, true)) {
                    if (!et_nombre.getText().toString().trim().isEmpty()) {
                        if(!et_precio.getText().toString().trim().isEmpty()) {
                            if (!isRepetidoElNombre(et_nombre.getText().toString())) {
                                alertDialog.dismiss();
                                if (li_imagen.getDrawable() != null) {
                                    progressDialogSubiendo = ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                                    Bitmap bitmap = ((BitmapDrawable) li_imagen.getDrawable()).getBitmap();
                                    subirImagenCrear(bitmap, et_nombre.getText().toString(),et_precio.getText().toString());
                                } else {
                                    progressDialogSubiendo = ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                                    subirDatosCrearElementoInternet(et_nombre.getText().toString(),et_precio.getText().toString());
                                }
                            } else {
                                et_nombre.setError(getString(R.string.existe_categoria_con_este_nombre));
                            }
                        }else{
                            et_precio.setError(getString(R.string.campo_vacio));
                        }
                    } else {
                        et_nombre.setError(getString(R.string.campo_vacio));
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
    private void subirImagenCrear(Bitmap bitmap, String nombreCategoria, String precio){

        Bitmap BitRec = Bitmap.createScaledBitmap(bitmap, ANCHO_DE_FOTO_A_SUBIR, ALTO_DE_FOTO_A_SUBIR, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitRec.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utiles.DIRECWEB_IMG + "_SubirImagen.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                subirDatosCrearElementoInternet(nombreCategoria, precio);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogSubiendo.dismiss();
                showAlertDialogNoInternet();
                //alert dialog de error
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String imagen = ConvertImage;
                String nombre = "Menu_"+nombreCategoria;

                Map<String, String> params = new Hashtable<String, String>();
                params.put("imagen", imagen);
                params.put("nombre", nombre);

                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    private void subirDatosCrearElementoInternet(String nombre, String precio){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ColocarMenu.php?Producto="+nombre+"&Precio="+precio+"&Categoria="+categoriaSeleccionada+"&Subcategoria="+subcategoriaSeleccionada , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialogSubiendo.dismiss();
                array_menus.add(new Pila_Menu(nombre,Double.parseDouble(precio), 1));
                actualizarRecyclerParaMenu();
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogSubiendo.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    public void showAlertDialogMuchosElementos() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Personalizar_Menu.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.demasiados_elementos);
        builder.setMessage(R.string.excedido_numero_maximo_elementos);
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

    //Auxiliares
    private void escogerimagenGaleria() {

        if (Utiles.Permisos.siHayPermisoDeAlmacenamiento(getApplicationContext())) {

            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, INTENT_RESULT_GALERIA);

        } else {
            Utiles.Permisos.pedirPermisoDeAlmacenamiento(Activity_Personalizar_Menu.this, PERMISO_GALERIA);
        }

    }
    private void recortarImagen(Uri uri1, Uri uri2) {
        try {
            UCrop.of(uri1, uri2)
                    .withAspectRatio(3, 3)
                    .withMaxResultSize(ANCHO_DE_FOTO_A_SUBIR, ALTO_DE_FOTO_A_SUBIR)
                    .start(Activity_Personalizar_Menu.this);
        } catch (Exception e) {
            Toast.makeText(Activity_Personalizar_Menu.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

    }
    private boolean isRepetidoElNombre(String nombre){
        boolean repetido=false;
        for (int x=0;x<array_menus.size();x++){
            if(array_menus.get(x).getNombre().equalsIgnoreCase(nombre)){
                repetido=true;
            }
        }
        return repetido;
    }

    //Eliminar
    private void eliminarCategoria(int position){
        showAlertEliminarCategoria(position);
    }
    private void eliminarImagenDeEliminarDatos(String nombre, int position){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_IMG+"_EliminarImagen.php?Nombre=Categorias_"+nombre, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialogSubiendo.dismiss();
                array_menus.remove(position);
                actualizarRecyclerParaMenu();
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogSubiendo.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void eliminarDatosCategoriaInternet(String nombre, int position){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"EliminarMenu.php?Producto="+nombre+"&Categoria="+categoriaSeleccionada, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                eliminarImagenDeEliminarDatos(nombre, position);
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogSubiendo.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    public void showAlertEliminarCategoria(int position) {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Personalizar_Menu.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.eliminar_elemento);
        builder.setMessage(R.string.esta_seguro_eliminar_menu);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Utiles.Internet.isOnline(Activity_Personalizar_Menu.this,true)) {
                    dialog.dismiss();
                    progressDialogSubiendo = ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                    eliminarDatosCategoriaInternet(array_menus.get(position).getNombre(), position);
                }
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    //Editar categoria
    private void editarMenu(int position, Drawable drawable){
        liEditarMenu(position,drawable);
    }
    private void liEditarMenu(int position, Drawable drawable){
        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Personalizar_Menu.this);
        View vista = layoutInflater.inflate(R.layout.li_personalizar_menu_agregar_o_editar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Personalizar_Menu.this);
        builder.setView(vista);
        AlertDialog alertDialog = builder.create();

        //Declaraciones
        li_imagen = (ImageView) vista.findViewById(R.id.li_persmenu_IV);
        EditText et_nombre = (EditText) vista.findViewById(R.id.li_personalizar_menu_et_nombre);
        EditText et_precio = (EditText) vista.findViewById(R.id.li_personalizar_menu_et_precio);
        TextView tv_cancelar = (TextView) vista.findViewById(R.id.li_personalizar_menu_tv_cancelar);
        TextView tv_aceptar = (TextView) vista.findViewById(R.id.li_personalizar_menu_tv_aceptar);
        CardView cardView_general=(CardView)vista.findViewById(R.id.li_personalizar_menu_cv_imagen);
        TextView tv_categoria=(TextView)vista.findViewById(R.id.li_personalizar_menu_tv_categoria);
        TextView tv_subcategoria=(TextView)vista.findViewById(R.id.li_personalizar_menu_tv_subcategoria);
        ImageView imageView_eliminarPh=(ImageView)vista.findViewById(R.id.li_personalizar_menu_iv_eliminar);


        //Llenar
        if(categoriaSeleccionada.equals("Sandwich")){
            tv_subcategoria.setVisibility(View.VISIBLE);
            String subcategoria=array_subselector.get(adapter_subselector.getPositionSeleccionada()).nombreVisible;
            tv_subcategoria.setText(subcategoria);
            tv_categoria.setText(categoriaSeleccionada);
        }else{
            tv_subcategoria.setVisibility(View.GONE);
            tv_categoria.setText(categoriaSeleccionada);
        }
        li_imagen.setImageDrawable(drawable);
        et_nombre.setText(array_menus.get(position).getNombre());
        et_precio.setText(String.valueOf(array_menus.get(position).getPrecio()));
        if(li_imagen.getDrawable()!=null)imageView_eliminarPh.setVisibility(View.VISIBLE);


        //Modo vegetariano
        if(categoriaSeleccionada.equals("Sandwich")){
            if(subcategoriaSeleccionada.equals("modo_vegetariano")){
                et_nombre.setEnabled(false);
            }else{
                et_nombre.setEnabled(true);
            }
        }else{
            et_nombre.setEnabled(true);
        }

        //Listeners
        cardView_general.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escogerimagenGaleria();
            }
        });
        //boton de cancelar
        tv_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        //boton de aceptar
        tv_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utiles.Internet.isOnline(Activity_Personalizar_Menu.this, true)) {
                    if (!et_nombre.getText().toString().trim().isEmpty()) {
                        if(!et_precio.getText().toString().trim().isEmpty()) {
                            alertDialog.dismiss();
                            if (li_imagen.getDrawable() != null) {
                                progressDialogSubiendo = ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                                Bitmap bitmap = ((BitmapDrawable) li_imagen.getDrawable()).getBitmap();
                                eliminarImagenDeActualizarDatos(et_nombre.getText().toString(),position,true,bitmap,array_menus.get(position).getNombre(),Double.parseDouble(et_precio.getText().toString()),array_menus.get(position).getEstado());
                            } else {
                                progressDialogSubiendo= ProgressDialog.show(Activity_Personalizar_Menu.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                                eliminarImagenDeActualizarDatos(et_nombre.getText().toString(),position,false,null,array_menus.get(position).getNombre(), Double.parseDouble(et_precio.getText().toString()),array_menus.get(position).getEstado());
                            }
                        }else{
                            et_precio.setError(getString(R.string.campo_vacio));
                        }
                    } else {
                        et_nombre.setError(getString(R.string.campo_vacio));
                    }
                }
            }
        });
        //boton eliminar imagen
        imageView_eliminarPh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                li_imagen.setImageDrawable(null);
                imageView_eliminarPh.setVisibility(View.GONE);
            }
        });



        //Finalizado
        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
    private void eliminarImagenDeActualizarDatos(String nombre, int position,boolean luegoSubirImagen, Bitmap bitmap, String nombreID, double precio, int estado){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_IMG+"_EliminarImagen.php?Nombre=Menu_"+nombreID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(luegoSubirImagen){
                    subirImagenEditar(bitmap, nombre,nombreID,position, precio,estado);
                }else{
                    actualizarDatosMenuInternet(array_menus.get(position).getNombre(),nombre,position, precio, estado);
                }
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogSubiendo.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void subirImagenEditar(Bitmap bitmap, String nombreCategoria, String nombreId,int position, double precio, int estado){

        Bitmap BitRec = Bitmap.createScaledBitmap(bitmap, ANCHO_DE_FOTO_A_SUBIR, ALTO_DE_FOTO_A_SUBIR, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitRec.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utiles.DIRECWEB_IMG + "_SubirImagen.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                actualizarDatosMenuInternet(nombreId,nombreCategoria,position, precio, estado);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogSubiendo.dismiss();
                showAlertDialogNoInternet();
                //alert dialog de error
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String imagen = ConvertImage;
                String nombre = "Menu_"+nombreCategoria;

                Map<String, String> params = new Hashtable<String, String>();
                params.put("imagen", imagen);
                params.put("nombre", nombre);

                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    private void actualizarDatosMenuInternet(String nombreId, String nombre, int position, double precio, int estado){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ActualizarMenu.php?ProductoId="+nombreId+"&Producto="+nombre+"&Precio="+precio+"&Categoria="+categoriaSeleccionada+"&Subcategoria="+subcategoriaSeleccionada +"&Estado="+estado, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialogSubiendo.dismiss();
                array_menus.add(position,new Pila_Menu(nombre,precio, estado));
                array_menus.remove(position+1);
                actualizarRecyclerParaMenu();
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogSubiendo.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }

    //Metodos Auxiliares
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        //Galeria
        if (requestCode == INTENT_RESULT_GALERIA) {
            if (data != null) {
                Uri contentUri = data.getData();
                try {
                    File file = Utiles.Imagenes.createTempImageFile(getApplicationContext(), Utiles.Imagenes.getHoraActual("yyMMddHHmmss"));
                    recortarImagen(contentUri, Uri.fromFile(file));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Activity_Personalizar_Menu.this, R.string.error_obtener_imagen, Toast.LENGTH_SHORT).show();
                }
            }
        }

        //UCrop
        if (requestCode == UCrop.REQUEST_CROP) {
            if(data!=null){
                uriLLegadaRecortada = UCrop.getOutput(data);
                li_imagen.setImageURI(uriLLegadaRecortada);
            }else{
                Toast.makeText(Activity_Personalizar_Menu.this, getString(R.string.error_obtener_imagen), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == UCrop.RESULT_ERROR) {
            Toast.makeText(Activity_Personalizar_Menu.this, getString(R.string.error_obtener_imagen), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_GALERIA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                escogerimagenGaleria();
            }
        }
    }



}
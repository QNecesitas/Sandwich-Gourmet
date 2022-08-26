package com.example.elemperadorvedado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

public class Activity_Personalizar_Categorias extends AppCompatActivity {

    //Recycler
    private RecyclerView recycler_categorias;
    private ArrayList<Pila_Categorias> arrayList_categorias;

    //Constantes
    private final int INTENT_RESULT_GALERIA = 1;
    private final int PERMISO_GALERIA = 11;
    private final int ANCHO_DE_FOTO_A_SUBIR=800;
    private final int ALTO_DE_FOTO_A_SUBIR=373;

    //Internet
    private Uri uriLLegadaRecortada;
    private FloatingActionButton floatingButton;
    private ProgressDialog progressDialogInternet;

    //LI_Agregar o Editar
    private ImageView li_imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalizar_categorias);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_personalizar_categ_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //LLenar recycler
        recycler_categorias=(RecyclerView)findViewById(R.id.activity_personalizar_categ_recycler);
        recycler_categorias.setHasFixedSize(true);
        recycler_categorias.setLayoutManager(new LinearLayoutManager(Activity_Personalizar_Categorias.this));
        arrayList_categorias=new ArrayList<>();

        floatingButton = (FloatingActionButton) findViewById(R.id.activity_personalizar_categ_fbutton);
        cargardatosInternet();

    }


    //Generales, precargados
    private void cargardatosInternet(){
        ProgressDialog pDialog = ProgressDialog.show(Activity_Personalizar_Categorias.this, getString(R.string.cargando_datos), getString(R.string.por_favor_espere), false, false);
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerRecyclerCategorias.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                arrayList_categorias.clear();
                try{
                    JSONArray array =new JSONArray(response);
                    for (int i=0; i<array.length();i++) {

                        JSONObject json_objet = array.getJSONObject(i);
                        if(json_objet.getString("Nombre").length()>0) {
                            if(json_objet.getString("Nombre").equals(getString(R.string.sandwich))){
                                arrayList_categorias.add(0,new Pila_Categorias(
                                        json_objet.getString("Nombre"),
                                        json_objet.getInt("CtdElementos")
                                ));
                            }else {
                                arrayList_categorias.add(new Pila_Categorias(
                                        json_objet.getString("Nombre"),
                                        json_objet.getInt("CtdElementos")
                                ));
                            }
                        }
                    }
                    actualizarRecycler();

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
    private void actualizarRecycler(){
        AdaptadorR_PersCategorias adapter=new AdaptadorR_PersCategorias(Activity_Personalizar_Categorias.this, arrayList_categorias);
        adapter.setListener_editar(new AdaptadorR_PersCategorias.RecyclerEditarTouchListener() {
            @Override
            public void onClickItem(View v, int position, Drawable drawable) {
                editarCategoria(position, drawable);
            }
        });
        adapter.setListener_eliminar(new AdaptadorR_PersCategorias.RecyclerEliminarTouchListener() {
            @Override
            public void onClickItem(View v, int position) {
                eliminarCategoria(position);
            }
        });
        recycler_categorias.setAdapter(adapter);
    }
    public void showAlertDialogNoInternet() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Personalizar_Categorias.this);
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
    public void showAlertEliminarCategoria(int position) {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Personalizar_Categorias.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.eliminar_categoria);
        builder.setMessage(R.string.seguro_eliminar_categoria);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Utiles.Internet.isOnline(Activity_Personalizar_Categorias.this,true)) {
                    progressDialogInternet = ProgressDialog.show(Activity_Personalizar_Categorias.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                    eliminarDatosCategoriaInternet(arrayList_categorias.get(position).getNombre(), position);
                    dialog.dismiss();
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

    //Agregar categoria
    public void fbAgregarCateg(View view) {
        if(arrayList_categorias.size()<20) {
            liAgregarCategoria();
        }else{
            showAlertDialogMuchasCategorias();
        }
    }
    private void liAgregarCategoria(){
        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Personalizar_Categorias.this);
        View vista = layoutInflater.inflate(R.layout.li_personalizar_categorias_agregar_o_editar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Personalizar_Categorias.this);
        builder.setView(vista);
        AlertDialog alertDialog = builder.create();

        //Declaraciones
        li_imagen = (ImageView) vista.findViewById(R.id.li_persCategorias_IV);
        EditText et_nombre = (EditText) vista.findViewById(R.id.li_personalizar_categ_et_nombre);
        TextView tv_cancelar = (TextView) vista.findViewById(R.id.li_personalizar_categ_tv_cancelar);
        TextView tv_aceptar = (TextView) vista.findViewById(R.id.li_personalizar_categ_tv_aceptar);
        CardView cardView_general=(CardView)vista.findViewById(R.id.li_personalizar_categ_cv_imagen);

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
                if (Utiles.Internet.isOnline(Activity_Personalizar_Categorias.this, true)) {
                    if (!et_nombre.getText().toString().trim().isEmpty()) {
                        if(!isRepetidoElNombre(et_nombre.getText().toString())) {
                            alertDialog.dismiss();
                            if (li_imagen.getDrawable() != null) {
                                progressDialogInternet = ProgressDialog.show(Activity_Personalizar_Categorias.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                                Bitmap bitmap = ((BitmapDrawable) li_imagen.getDrawable()).getBitmap();
                                subirImagenCrear(bitmap, et_nombre.getText().toString());
                            } else {
                                progressDialogInternet = ProgressDialog.show(Activity_Personalizar_Categorias.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                                subirDatosCrearCategoriaInternet(et_nombre.getText().toString());
                            }
                        }else {
                            et_nombre.setError(getString(R.string.existe_categoria_con_este_nombre));
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
    private void subirImagenCrear(Bitmap bitmap, String nombreCategoria){

        Bitmap BitRec = Bitmap.createScaledBitmap(bitmap, ANCHO_DE_FOTO_A_SUBIR, ALTO_DE_FOTO_A_SUBIR, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitRec.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utiles.DIRECWEB_IMG + "_SubirImagen.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                subirDatosCrearCategoriaInternet(nombreCategoria);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogInternet.dismiss();
                showAlertDialogNoInternet();
                //alert dialog de error
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String imagen = ConvertImage;
                String nombre = "Categorias_"+nombreCategoria;

                Map<String, String> params = new Hashtable<String, String>();
                params.put("imagen", imagen);
                params.put("nombre", nombre);

                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    private void subirDatosCrearCategoriaInternet(String nombre){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ColocarCategoria.php?Nombre="+nombre, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialogInternet.dismiss();
                arrayList_categorias.add(new Pila_Categorias(nombre,0));
                actualizarRecycler();
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogInternet.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    public void showAlertDialogMuchasCategorias() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Personalizar_Categorias.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.demasiadas_categorias);
        builder.setMessage(R.string.excedido_maximo_categorias);
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

    //Editar categoria
    private void editarCategoria(int position, Drawable drawable){
        liEditarCategoria(position,drawable);
    }
    private void liEditarCategoria(int position, Drawable drawable){
        LayoutInflater layoutInflater = LayoutInflater.from(Activity_Personalizar_Categorias.this);
        View vista = layoutInflater.inflate(R.layout.li_personalizar_categorias_agregar_o_editar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Personalizar_Categorias.this);
        builder.setView(vista);
        AlertDialog alertDialog = builder.create();

        //Declaraciones
        li_imagen = (ImageView) vista.findViewById(R.id.li_persCategorias_IV);
        EditText et_nombre = (EditText) vista.findViewById(R.id.li_personalizar_categ_et_nombre);
        TextView tv_cancelar = (TextView) vista.findViewById(R.id.li_personalizar_categ_tv_cancelar);
        TextView tv_aceptar = (TextView) vista.findViewById(R.id.li_personalizar_categ_tv_aceptar);
        CardView cardView_general=(CardView)vista.findViewById(R.id.li_personalizar_categ_cv_imagen);
        ImageView imageView_eliminarPh=(ImageView)vista.findViewById(R.id.li_personalizar_categ_iv_eliminar);

        //Llenar
        li_imagen.setImageDrawable(drawable);
        et_nombre.setText(arrayList_categorias.get(position).getNombre());
        if(li_imagen.getDrawable()!=null)imageView_eliminarPh.setVisibility(View.VISIBLE);


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
                if (Utiles.Internet.isOnline(Activity_Personalizar_Categorias.this, true)) {
                    if (!et_nombre.getText().toString().trim().isEmpty()) {
                        alertDialog.dismiss();
                        if (li_imagen.getDrawable() != null) {
                            progressDialogInternet = ProgressDialog.show(Activity_Personalizar_Categorias.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                            Bitmap bitmap = ((BitmapDrawable) li_imagen.getDrawable()).getBitmap();
                            eliminarImagenDeActualizarDatos(et_nombre.getText().toString(),position,true,bitmap,arrayList_categorias.get(position).getNombre());
                        } else {
                            progressDialogInternet = ProgressDialog.show(Activity_Personalizar_Categorias.this, getString(R.string.actualizando_info), getString(R.string.por_favor_espere), false, false);
                            eliminarImagenDeActualizarDatos(et_nombre.getText().toString(),position,false,null,arrayList_categorias.get(position).getNombre());
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
    private void eliminarImagenDeActualizarDatos(String nombre, int position,boolean luegoSubirImagen, Bitmap bitmap, String nombreID){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_IMG+"_EliminarImagen.php?Nombre=Categorias_"+nombreID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(luegoSubirImagen){
                    subirImagenEditar(bitmap, nombre,nombreID,position);
                }else{
                    actualizarDatosCategoriaInternet(arrayList_categorias.get(position).getNombre(),nombre,position);
                }
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogInternet.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void subirImagenEditar(Bitmap bitmap, String nombreCategoria, String nombreId,int position){

        Bitmap BitRec = Bitmap.createScaledBitmap(bitmap, ANCHO_DE_FOTO_A_SUBIR, ALTO_DE_FOTO_A_SUBIR, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BitRec.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utiles.DIRECWEB_IMG + "_SubirImagen.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                actualizarDatosCategoriaInternet(nombreId,nombreCategoria,position);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialogInternet.dismiss();
                showAlertDialogNoInternet();
                //alert dialog de error
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String imagen = ConvertImage;
                String nombre = "Categorias_"+nombreCategoria;

                Map<String, String> params = new Hashtable<String, String>();
                params.put("imagen", imagen);
                params.put("nombre", nombre);

                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    private void actualizarDatosCategoriaInternet(String nombreId, String nombre, int position){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ActualizarCategoria.php?NombreId="+nombreId+"&Nombre="+nombre, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialogInternet.dismiss();
                int cant=arrayList_categorias.get(position).getCant_productos();
                arrayList_categorias.add(position,new Pila_Categorias(nombre,cant));
                arrayList_categorias.remove(position+1);
                actualizarRecycler();
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogInternet.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }

    //Auxiliares
    private void escogerimagenGaleria() {

        if (Utiles.Permisos.siHayPermisoDeAlmacenamiento(getApplicationContext())) {

            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, INTENT_RESULT_GALERIA);

        } else {
            Utiles.Permisos.pedirPermisoDeAlmacenamiento(Activity_Personalizar_Categorias.this, PERMISO_GALERIA);
        }

    }
    private void recortarImagen(Uri uri1, Uri uri2) {
        try {
            UCrop.of(uri1, uri2)
                    .withAspectRatio(3, 1.4f)
                    .withMaxResultSize(ANCHO_DE_FOTO_A_SUBIR, ALTO_DE_FOTO_A_SUBIR)
                    .start(Activity_Personalizar_Categorias.this);
        } catch (Exception e) {
            Toast.makeText(Activity_Personalizar_Categorias.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }

    }
    private boolean isRepetidoElNombre(String nombre){
        boolean repetido=false;
        for (int x=0;x<arrayList_categorias.size();x++){
            if(arrayList_categorias.get(x).getNombre().equalsIgnoreCase(nombre)){
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
                progressDialogInternet.dismiss();
                arrayList_categorias.remove(position);
                actualizarRecycler();
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogInternet.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }
    private void eliminarDatosCategoriaInternet(String nombre, int position){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"EliminarCategoria.php?Nombre="+nombre, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                eliminarImagenDeEliminarDatos(nombre, position);
            }
        },

                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialogInternet.dismiss();
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
                    Toast.makeText(Activity_Personalizar_Categorias.this, "Error al obtener imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //UCrop
        if (requestCode == UCrop.REQUEST_CROP) {
            if(data!=null){
                uriLLegadaRecortada = UCrop.getOutput(data);
                li_imagen.setImageURI(uriLLegadaRecortada);
            }else{
                Toast.makeText(Activity_Personalizar_Categorias.this, getString(R.string.error_obtener_imagen), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == UCrop.RESULT_ERROR) {
            Toast.makeText(Activity_Personalizar_Categorias.this, getString(R.string.error_obtener_imagen), Toast.LENGTH_SHORT).show();
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



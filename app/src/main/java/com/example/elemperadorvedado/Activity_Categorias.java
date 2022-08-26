package com.example.elemperadorvedado;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class Activity_Categorias extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //Recycler
    private RecyclerView recycler;
    private  ArrayList<Pila_Categorias> pila_categorias;
    private AdaptadorR_Categorias adapter;
    private ProgressDialog progressDialog;
    private String contrasenaJefeActualBD;
    private String contrasenaEmpleadoActualBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        //Toolbar
        Toolbar toolbar=(Toolbar)findViewById(R.id.categorias_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        //DrawerLayout
        DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(Activity_Categorias.this,drawer,toolbar,R.string.open,R.string.closed);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationDrawer=(NavigationView)findViewById(R.id.categorias_nav_drawer);
        navigationDrawer.setNavigationItemSelectedListener(this);



        //RecyclerView
        recycler=(RecyclerView) findViewById(R.id.categorias_recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(Activity_Categorias.this));
        pila_categorias=new ArrayList<>();
        adapter=new AdaptadorR_Categorias(this,pila_categorias);
        cargar_contrasena();

    }

    public void showAlertDialogNoInternet() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Categorias.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.revise_conx);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cargar_contrasena();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    public void categorias_click_carrrito(View v){
        Intent intent=new Intent(Activity_Categorias.this,Activity_Carrito.class);
        startActivity(intent);
    }

    private void cargar_contrasena(){
        progressDialog=ProgressDialog.show(Activity_Categorias.this,getString(R.string.cargando_datos),getString(R.string.por_favor_espere),false,false);
        RequestQueue requestQALL = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerContrasenas.php",new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                try {
                    JSONArray array=new JSONArray(response);

                    //Contraseña Jefe
                    JSONObject json_ObjectJefe=array.getJSONObject(0);
                    if(json_ObjectJefe.getString("Tipo").length()>0){
                        contrasenaJefeActualBD =json_ObjectJefe.getString("Contrasena");
                    }

                    //Contraseña Empleado
                    JSONObject json_ObjectEmpleado=array.getJSONObject(1);
                    if(json_ObjectEmpleado.getString("Tipo").length()>0){
                        contrasenaEmpleadoActualBD =json_ObjectEmpleado.getString("Contrasena");
                    }

                    cargardatosInternet();

                }catch(JSONException e){
                    e.printStackTrace();
                }
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
    private void cargardatosInternet(){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerRecyclerCategorias.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                pila_categorias.clear();
                try{
                    JSONArray array =new JSONArray(response);
                    for (int i=0; i<array.length();i++) {

                        JSONObject json_objet = array.getJSONObject(i);
                        if(json_objet.getString("Nombre").length()>0) {
                            if(json_objet.getString("Nombre").equals(getString(R.string.sandwich))){
                                pila_categorias.add(0,new Pila_Categorias(
                                        json_objet.getString("Nombre"),
                                        json_objet.getInt("CtdElementos")
                                ));
                            }else {
                                pila_categorias.add(new Pila_Categorias(
                                        json_objet.getString("Nombre"),
                                        json_objet.getInt("CtdElementos")
                                ));
                            }
                        }
                    }
                    adapter=new AdaptadorR_Categorias(Activity_Categorias.this, pila_categorias);
                    recycler.setAdapter(adapter);
                    adapter.setClickListener(new AdaptadorR_Categorias.RecyclerTouchListener(){
                        @Override
                        public void onClickItem(View v, int position) {
                            if (Utiles.Internet.isOnline(Activity_Categorias.this,true)) {
                                if(position==0){
                                    Intent intent=new Intent(Activity_Categorias.this,Activity_Detalles.class);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(Activity_Categorias.this, Activity_Menu.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putString("categoria",pila_categorias.get(position).getNombre());
                                    intent.putExtra("bdle_categoria_menu",bundle);
                                    startActivity(intent);
                                }
                            }
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
                        progressDialog.dismiss();
                        showAlertDialogNoInternet();
                    }
                });
        requestQAll.add(stringRequest);
    }

    private void li_contrasenas(){
        LayoutInflater inflater=LayoutInflater.from(Activity_Categorias.this);
        View vista= inflater.inflate(R.layout.li_contrasenas, null);
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(Activity_Categorias.this);
        builder.setView(vista);
        final androidx.appcompat.app.AlertDialog alertDialog=builder.create();

        //Declaraciones
        TextInputEditText tiet=(TextInputEditText) vista.findViewById(R.id.li_contrasenas_tiet);
        TextInputLayout til=(TextInputLayout) vista.findViewById(R.id.li_contrasenas_til);
        TextView button=(TextView)vista.findViewById(R.id.li_contrasenas_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tiet.getText().toString().equals(contrasenaEmpleadoActualBD)||tiet.getText().toString().equals(contrasenaJefeActualBD)){
                    if(tiet.getText().toString().equals(contrasenaEmpleadoActualBD)){
                        Intent intent=new Intent(Activity_Categorias.this,Activity_EmpleadoJefe.class);
                        intent.putExtra("jefe_empleado_choice","Empleado");
                        startActivity(intent);
                    }else if(tiet.getText().toString().equals(contrasenaJefeActualBD)){
                        Intent intent=new Intent(Activity_Categorias.this,Activity_EmpleadoJefe.class);
                        intent.putExtra("jefe_empleado_choice","Jefe");
                        startActivity(intent);
                    }
                }else{
                    til.setError(getString(R.string.contrasena_no_coincidente));
                }
            }
        });


        //Finalizado
        builder.setCancelable(true);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.menu_admin){
            li_contrasenas();
        }

        if(id==R.id.menu_mis_pedidos){
            if(Utiles.Internet.isOnline(Activity_Categorias.this,true)) {
                Intent intent = new Intent(Activity_Categorias.this, Activity_Mis_Pedidos.class);
                startActivity(intent);
            }
        }

        if(id==R.id.menu_acerca_de){
            Intent intent=new Intent(Activity_Categorias.this,Activity_AcercaDe.class);
            startActivity(intent);
        }

        DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onBackPressed(){
        showAlertDialog();
    }
    private void showAlertDialog() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.salir);
        builder.setMessage(R.string.seguro_desea_salir);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish the activity
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog gone
                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

}
package com.example.elemperadorvedado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Activity_Contrasena extends AppCompatActivity {


    //Jefe
    private String contrasenaJefeActualBD;
    private TextInputLayout TIL_Jefe_Actual,TIL_Jefe_Nueva,TIL_Jefe_NuevaConf;
    private TextInputEditText TIET_Jefe_Actual,TIET_Jefe_Nueva,TIET_Jefe_NuevaConf;

    //Empleado
    private String contrasenaEmpleadoActualBD;
    private TextInputLayout TIL_Empleado_Actual,TIL_Empleado_Nueva,TIL_Empleado_NuevaConf;
    private TextInputEditText TIET_Empleado_Actual,TIET_Empleado_Nueva,TIET_Empleado_NuevaConf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrasena);
        try{


            //Toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.contrasena_toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            //Jefe
            TIL_Jefe_Actual=(TextInputLayout)findViewById(R.id.contrasena_Jefe_TIL_Actual);
            TIET_Jefe_Actual=(TextInputEditText) findViewById(R.id.contrasena_Jefe_TIET_Actual);
            TIL_Jefe_Nueva=(TextInputLayout)findViewById(R.id.contrasena_Jefe_TIL_Nueva);
            TIET_Jefe_Nueva=(TextInputEditText) findViewById(R.id.contrasena_Jefe_TIET_Nueva);
            TIL_Jefe_NuevaConf=(TextInputLayout)findViewById(R.id.contrasena_Jefe_TIL_NuevaConf);
            TIET_Jefe_NuevaConf=(TextInputEditText) findViewById(R.id.contrasena_Jefe_TIET_NuevaConf);

            //Empleado
            TIL_Empleado_Actual=(TextInputLayout)findViewById(R.id.contrasena_Empleado_TIL_Actual);
            TIET_Empleado_Actual=(TextInputEditText) findViewById(R.id.contrasena_Empleado_TIET_Actual);
            TIL_Empleado_Nueva=(TextInputLayout)findViewById(R.id.contrasena_Empleado_TIL_Nueva);
            TIET_Empleado_Nueva=(TextInputEditText) findViewById(R.id.contrasena_Empleado_TIET_Nueva);
            TIL_Empleado_NuevaConf=(TextInputLayout)findViewById(R.id.contrasena_Empleado_TIL_NuevaConf);
            TIET_Empleado_NuevaConf=(TextInputEditText) findViewById(R.id.contrasena_Empleado_TIET_NuevaConf);

            cargar_contrasena();



        }catch (Exception e){
            Toast.makeText(Activity_Contrasena.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private void cargar_contrasena(){
        ProgressDialog progressDialog=ProgressDialog.show(Activity_Contrasena.this,getString(R.string.cargando_datos),getString(R.string.por_favor_espere),false,false);
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

                    progressDialog.dismiss();

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        showAlertDialogNoInternet();
                    }
                });
        requestQALL.add(stringRequest);
    }
    private void showAlertDialogNoInternet() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Contrasena.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.revise_conx);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    //Jefe
    public void contrasena_click_cambiarContJefe(View  v){
        if(Utiles.Internet.isOnline(Activity_Contrasena.this,true)) {
            jefe_revisarIfVacio();
        }
    }
    private void jefe_revisarIfVacio(){
        if(!TIET_Jefe_Actual.getText().toString().trim().isEmpty()){
            if(!TIET_Jefe_Nueva.getText().toString().trim().isEmpty()){
                if(!TIET_Jefe_NuevaConf.getText().toString().trim().isEmpty()){
                    jefe_revisarIgualdades();
                }else{
                    TIL_Jefe_Actual.setError(getString(R.string.campo_vacio));
                }
            }else{
                TIL_Jefe_Nueva.setError(getString(R.string.campo_vacio));
            }
        }else{
            TIL_Jefe_NuevaConf.setError(getString(R.string.campo_vacio));
        }
    }
    private void jefe_revisarIgualdades(){
        if(TIET_Jefe_Actual.getText().toString().equals(contrasenaJefeActualBD)){
            if(!TIET_Jefe_Actual.getText().toString().equals(TIET_Jefe_Nueva.getText().toString())){
                if(TIET_Jefe_Nueva.getText().toString().equals(TIET_Jefe_NuevaConf.getText().toString())){
                    if(!TIET_Jefe_Nueva.getText().toString().equals(contrasenaEmpleadoActualBD)){
                        subirContrasenaCreada("Jefe",TIET_Jefe_Nueva.getText().toString());
                    }else{
                        TIL_Jefe_NuevaConf.setError(getString(R.string.contr_noDebeCoinc_contrEmpleados));
                    }
                }else{
                    TIL_Jefe_NuevaConf.setError(getString(R.string.contrNueva_noCoincide_contrConf));
                }
            }else{
                TIL_Jefe_Actual.setError(getString(R.string.contrActual_noDebe_coincidir_contrNueva));
            }
        }else{
            TIL_Jefe_Actual.setError(getString(R.string.contrasena_actual_incorrecta));
        }
    }

    //Empleado
    public void contrasena_click_cambiarContEmpleado(View v){
        if(Utiles.Internet.isOnline(Activity_Contrasena.this,true)) {
            empleado_revisarIfVacio();
        }
    }
    private void empleado_revisarIfVacio(){
        if(!TIET_Empleado_Actual.getText().toString().trim().isEmpty()){
            if(!TIET_Empleado_Nueva.getText().toString().trim().isEmpty()){
                if(!TIET_Empleado_NuevaConf.getText().toString().trim().isEmpty()){
                    empleado_revisarIgualdades();
                }else{
                    TIL_Empleado_Actual.setError(getString(R.string.campo_vacio));
                }
            }else{
                TIL_Empleado_Nueva.setError(getString(R.string.campo_vacio));
            }
        }else{
            TIL_Empleado_NuevaConf.setError(getString(R.string.campo_vacio));
        }
    }
    private void empleado_revisarIgualdades(){
        if(TIET_Empleado_Actual.getText().toString().equals(contrasenaEmpleadoActualBD)){
            if(!TIET_Empleado_Actual.getText().toString().equals(TIET_Empleado_Nueva.getText().toString())){
                if(TIET_Empleado_Nueva.getText().toString().equals(TIET_Empleado_NuevaConf.getText().toString())){
                    if(!TIET_Empleado_Nueva.getText().toString().equals(contrasenaJefeActualBD)){
                        subirContrasenaCreada("Empleado",TIET_Empleado_Nueva.getText().toString());
                    }else{
                        TIL_Empleado_NuevaConf.setError(getString(R.string.contr_noDebeCoinc_contrJefe));
                    }
                }else{
                    TIL_Empleado_NuevaConf.setError(getString(R.string.contrNueva_noCoincide_contrConf));
                }
            }else{
                TIL_Empleado_Actual.setError(getString(R.string.contrActual_noDebe_coincidir_contrNueva));
            }
        }else{
            TIL_Empleado_Actual.setError(getString(R.string.contrasena_actual_incorrecta));
        }
    }


    //Ambos
    private void subirContrasenaCreada(String tipo,String nuevaContrasena){
        ProgressDialog progressDialog=ProgressDialog.show(Activity_Contrasena.this,getString(R.string.guardando_contrasena),getString(R.string.por_favor_espere),false,false);
        RequestQueue requestQALL = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ColocarContrasena.php?Tipo="+tipo+"&Contrasena="+nuevaContrasena,new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                progressDialog.dismiss();
                if(tipo.equals("Jefe")){
                    contrasenaJefeActualBD=nuevaContrasena;
                }else{
                    contrasenaEmpleadoActualBD=nuevaContrasena;
                }
                showAlertDialogContrasenaSubida();
            }

        },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        showAlertDialogNoInternet();
                    }
                });
        requestQALL.add(stringRequest);
    }
    private void showAlertDialogContrasenaSubida() {
        //init alert dialog
        AlertDialog.Builder builder =  new AlertDialog.Builder(Activity_Contrasena.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.cambio_satisfactorio);
        builder.setMessage(R.string.contrCambiada_satisfactoriamente);
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

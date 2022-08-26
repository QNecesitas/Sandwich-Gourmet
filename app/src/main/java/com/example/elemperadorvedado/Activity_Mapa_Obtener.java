package com.example.elemperadorvedado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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

public class Activity_Mapa_Obtener extends AppCompatActivity {

    private String url = "https://maps.google.com/maps";
    private WebView webView;
    private boolean algunElementoCargado=false;

    //Hacer Coordenadas
    private String Latitud = "no";
    private String Longitud = "no";
    private final String LATITUD_NEGOCIO = "23.139707";
    private final String LONGITUD_NEGOCIO = "-82.397338";
    private final double DIFERENCIA = 0.004;

    //Internet
    private ProgressDialog progressDialog;
    ArrayList<Pila_Distancia> arrayList_distancias;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_obtener);
        try {


            //Toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.mapa_toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });

            //ProgressDialog
            progressDialog=ProgressDialog.show(Activity_Mapa_Obtener.this,getString(R.string.cargando_mapa),getString(R.string.por_favor_espere),false,false);

            //webView
            webView = (WebView) findViewById(R.id.mapa_webView);
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient());
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url){
                    super.onPageFinished(view,url);
                    if(algunElementoCargado){
                        progressDialog.dismiss();
                    }else{
                        algunElementoCargado=true;
                    }
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                    super.onReceivedError(view, request, error);
                    algunElementoCargado=false;
                    showAlertDialogNoInternet();
                    webView.loadUrl(url);
                }
            });


            //cargar datos de internert
            arrayList_distancias=new ArrayList<>();
            cargardatosInternet();

        } catch (Exception e) {
            Toast.makeText(Activity_Mapa_Obtener.this, getString(R.string.error), Toast.LENGTH_LONG).show();
        }
    }

    public void Mapa_Click_AddUbic(View v) {
        if (webView.getUrl().contains(",") && webView.getUrl().contains("@")) {
            hacerCordenada();
        } else {
            Toast.makeText(Activity_Mapa_Obtener.this, getString(R.string.ubicacion_incorrecta), Toast.LENGTH_LONG).show();
        }

    }

    private void hacerCordenada() {
        String total;
        int arroba = 0;
        int coma1 = 0;
        int coma2 = 0;

        total = webView.getUrl();

        while (total.charAt(arroba) != '@') {
            arroba++;
        }

        coma1 = arroba;

        while (total.charAt(coma1) != ',') {
            coma1++;
        }

        coma2 = coma1 + 1;

        while (total.charAt(coma2) != ',') {
            coma2++;
        }

        Latitud = total.substring(arroba + 1, coma1);
        Longitud = total.substring(coma1 + 1, coma2);
        calcularKm();

    }

    private void calcularKm() {

        double latitudTraid, longitudTraid;
        double latitudMy, longitudMy;
        double result;
        int resultIntFinal;

        latitudMy = Double.parseDouble(Latitud);
        longitudMy = Double.parseDouble(Longitud);
        latitudTraid = Double.parseDouble(LATITUD_NEGOCIO);
        longitudTraid = Double.parseDouble(LONGITUD_NEGOCIO);

        //Calculos
        result = Math.sqrt(((latitudMy - latitudTraid) * (latitudMy - latitudTraid)) + ((longitudMy - longitudTraid) * (longitudMy - longitudTraid)));
        result += DIFERENCIA;
        result *= 100;

        resultIntFinal = (int) result;

        calcularPrecio_terminar(resultIntFinal);


    }

    private void calcularPrecio_terminar(int distanciaKm){
        int precio=0;

        for (int f=0;f<arrayList_distancias.size();f++){
            int distanciaInicial=arrayList_distancias.get(f).getDistancia_inicial();
            int distanciaFinal=arrayList_distancias.get(f).getDistancia_final();

            if(distanciaKm>=distanciaInicial&&distanciaKm<=distanciaFinal){
                precio=arrayList_distancias.get(f).getPrecio();
            }

        }


        Intent intent=new Intent();
        intent.putExtra("longitud",Longitud);
        intent.putExtra("latitud",Latitud);
        intent.putExtra("distanciaKm",distanciaKm);
        intent.putExtra("precio",precio);
        setResult(Activity.RESULT_OK,intent);
        finish();

    }

    private void cargardatosInternet(){
        RequestQueue requestQAll = Volley.newRequestQueue(this);
        StringRequest stringRequest= new StringRequest(Request.Method.GET, Utiles.DIRECWEB_ARCHIVOSPHP+"ObtenerRecyclerDistancias.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(algunElementoCargado){
                    progressDialog.dismiss();
                }else{
                    algunElementoCargado=true;
                }


                try{
                    JSONArray array =new JSONArray(response);
                    for (int i=0; i<array.length();i++) {

                        JSONObject json_objet = array.getJSONObject(i);
                        if(json_objet.getString("Desde").length()>0) {
                            arrayList_distancias.add(new Pila_Distancia(
                                    json_objet.getInt("Desde"),
                                    json_objet.getInt("Hasta"),
                                    json_objet.getInt("Precio")
                            ));
                        }
                    }

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
    public void showAlertDialogNoInternet() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Mapa_Obtener.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.revise_conx);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                algunElementoCargado=false;
                cargardatosInternet();
                webView.loadUrl(url);
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    @Override
    public void onBackPressed(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

}
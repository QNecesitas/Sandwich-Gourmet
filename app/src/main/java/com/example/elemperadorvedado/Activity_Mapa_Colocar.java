package com.example.elemperadorvedado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import java.util.Objects;

public class Activity_Mapa_Colocar extends AppCompatActivity {


    private String url="https://www.google.com/maps/search/?api=1&query=";
    private WebView webView;
    private String Latitud;
    private String Longitud;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_colocar);
        try {
            //cordenadas
            Latitud=getIntent().getStringExtra("Latitud");
            Longitud=getIntent().getStringExtra("Longitud");

            //Toolbar
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.mapa_colocar_appBar);
            Toolbar toolbar = (Toolbar) findViewById(R.id.mapa_colocar_toolbar);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            progressDialog= ProgressDialog.show(Activity_Mapa_Colocar.this,getString(R.string.cargando_mapa),getString(R.string.por_favor_espere),false,false);


            //webView
            webView=(WebView) findViewById(R.id.mapa_colocar_webView);
            webView.loadUrl(url+Latitud+","+Longitud);
            webView.setWebViewClient(new WebViewClient());
            WebSettings webSettings=webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url){
                    super.onPageFinished(view,url);
                        progressDialog.dismiss();
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
                    super.onReceivedError(view, request, error);
                    showAlertDialogNoInternet();
                }
            });




        }catch(Exception e){
            Toast.makeText(Activity_Mapa_Colocar.this, getString(R.string.error), Toast.LENGTH_LONG).show();
        }
    }
    public void showAlertDialogNoInternet() {
        //init alert dialog
        android.app.AlertDialog.Builder builder =  new android.app.AlertDialog.Builder(Activity_Mapa_Colocar.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.error);
        builder.setMessage(R.string.revise_conx);
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                webView.loadUrl(url);
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

}
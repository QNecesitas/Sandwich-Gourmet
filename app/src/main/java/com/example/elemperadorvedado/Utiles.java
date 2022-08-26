package com.example.elemperadorvedado;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.elemperadorvedado.R;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Utiles {
    public static final String DIRECWEB_ARCHIVOSPHP="http://srv250253-225181.vps.etecsa.cu/SandwichGourmet/SandwichGourmet/ArchivosPhp/";
    public static final String DIRECWEB_IMG ="http://srv250253-225181.vps.etecsa.cu/SandwichGourmet/SandwichGourmet/Imagenes/";

    public static class Internet{

        //ISONLINE@@@@@@@@@@@@@@@@
        public static boolean isOnline(Context context, boolean mostrarAlertDialg){
            boolean resutl;
            ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni=cm.getActiveNetworkInfo();

            resutl= ni != null&&ni.isConnected();
            if(!resutl&&mostrarAlertDialg){
                showAlertDialogNoInternet(context);

            }
            return resutl;
        }
        public static void showAlertDialogNoInternet(Context context) {
            //init alert dialog
            AlertDialog.Builder builder =  new AlertDialog.Builder(context);
            builder.setCancelable(true);
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
        //ISONLINEXXXXXXXXXXXXXXXXXXXXXXX


        //Otros@@@@@@@@@@@
        public static String downloadUrl(String myurl) throws IOException {
            Log.i("URL",""+myurl);
            myurl = myurl.replace(" ","%20");
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 2000;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("respuesta", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        private static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXx


    }

    public static class Permisos{

        //Comprobar si hay permisos
        public static boolean siHayPermisoDeAlmacenamiento(Context context){

            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            if(result== PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                return false;
            }

        }
        public static boolean siHayPermisoDeInfoTelefono(Context context){
            int result= ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            if(result== PackageManager.PERMISSION_GRANTED){
                return true;
            }else{ return false;}
        }
        public static boolean siHayPermisoDeAbrirCamara(Context context){

            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
            if(result== PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                return false;
            }

        }

        //PedirPermisos
        public static void pedirPermisoDeAlmacenamiento(Activity activity, int STORAGE_PERMISSION_CODE) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
        public static void pedirPermisoDeInfoTelefono(Activity activity, int PERMISSION_READ_STATE_CODE){

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_PHONE_STATE},PERMISSION_READ_STATE_CODE);
        }
        public static void pedirPermisoDeAbrirCamara(Activity activity, int CAMERA_PERMISSION_CODE){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

    }

    public static class Imagenes {

        static String getHoraActual(String SumaDeFormatos) {
            return new SimpleDateFormat(SumaDeFormatos,
                    Locale.getDefault()).format(new Date());
        }

        public static File createTempImageFile(Context context, String nombre) throws IOException {
            File storageDir = context.getExternalCacheDir();
            return File.createTempFile(nombre, ".png", storageDir);
        }

        public static File obtenerTempImageFile(Context context, String nombre) throws IOException {
            File storageDir = context.getExternalCacheDir();
            return new File(storageDir, nombre);
        }
    }
}

package com.example.elemperadorvedado;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int TIMEPO_DE_ESPERA=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View escondedor=getWindow().getDecorView();
        escondedor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        //Handler
        @SuppressLint("HandlerLeak") Handler handler=new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.arg1==1){
                    Intent intent=new Intent(MainActivity.this, Activity_Categorias.class);
                    startActivity(intent);
                }
            }
        };


        //Thread
        Thread thread=new Thread(() ->{
            try {
                Thread.sleep(TIMEPO_DE_ESPERA);
            }catch (Exception e){
                e.printStackTrace();
            }

            Message message=Message.obtain();
            message.arg1=1;
            handler.sendMessage(message);
        });
        thread.start();

    }

}
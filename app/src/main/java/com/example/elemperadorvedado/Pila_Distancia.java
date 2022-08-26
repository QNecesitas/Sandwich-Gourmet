package com.example.elemperadorvedado;

import java.util.ArrayList;

public class Pila_Distancia {

    private int distancia_inicial;;
    private int distancia_final;
    private int precio;

    public Pila_Distancia(int distancia_inicial, int distancia_final, int precio) {
        this.distancia_inicial = distancia_inicial;
        this.distancia_final = distancia_final;
        this.precio = precio;
    }


    public int getDistancia_inicial() {
        return distancia_inicial;
    }

    public int getDistancia_final() {
        return distancia_final;
    }

    public int getPrecio() {
        return precio;
    }


    public void setDistancia_inicial(int distancia_inicial) {
        this.distancia_inicial = distancia_inicial;
    }

    public void setDistancia_final(int distancia_final) {
        this.distancia_final = distancia_final;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }



}

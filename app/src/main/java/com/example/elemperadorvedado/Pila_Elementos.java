package com.example.elemperadorvedado;

import java.util.ArrayList;

class
Pila_Elementos {
    private String nombre;
    private int cantidad;
    private double precioProducto;

    public Pila_Elementos(String nombre, int cantidad, double precioProducto) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioProducto = precioProducto;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }
    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }
}

package com.example.elemperadorvedado;

import java.util.ArrayList;

class Pila_Carrito_General {
    private String nombre;
    private int cantidad;
    private double precioProducto;
    private ArrayList<Pila_Carrito_Sandwich> pila_carrito_sandwiches;
    private boolean ifSandwich;

    public Pila_Carrito_General(String nombre, int cantidad, double precioProducto, ArrayList<Pila_Carrito_Sandwich> pila_carrito_sandwiches, boolean ifSandwich) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioProducto = precioProducto;
        this.pila_carrito_sandwiches = pila_carrito_sandwiches;
        this.ifSandwich = ifSandwich;
    }

    public ArrayList<Pila_Carrito_Sandwich> getPila_carrito_sandwiches() {
        return pila_carrito_sandwiches;
    }

    public void setPila_carrito_sandwiches(ArrayList<Pila_Carrito_Sandwich> pila_carrito_sandwiches) {
        this.pila_carrito_sandwiches = pila_carrito_sandwiches;
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

    public boolean isIfSandwich() {
        return ifSandwich;
    }
    public void setIfSandwich(boolean ifSandwich) {
        this.ifSandwich = ifSandwich;
    }
}

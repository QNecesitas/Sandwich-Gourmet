package com.example.elemperadorvedado;

import java.util.ArrayList;

public class Pila_Sandwiches {
    private int cantidad;
    private double precioProducto;
    private ArrayList<Pila_Sandwich_Subproductos> subproducto_sandwiches;

    public Pila_Sandwiches(int cantidad, double precioProducto, ArrayList<Pila_Sandwich_Subproductos> subproducto_sandwiches) {
        this.cantidad = cantidad;
        this.precioProducto = precioProducto;
        this.subproducto_sandwiches = subproducto_sandwiches;
    }

    public ArrayList<Pila_Sandwich_Subproductos> getSubproducto_sandwiches() {
        return subproducto_sandwiches;
    }
    public void setSubproducto_sandwiches(ArrayList<Pila_Sandwich_Subproductos> subproducto_sandwiches) {
        this.subproducto_sandwiches = subproducto_sandwiches;
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

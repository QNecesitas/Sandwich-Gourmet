package com.example.elemperadorvedado;

class Pila_Carrito_Sandwich {
    private String nombre;
    private double precioProducto;

    public Pila_Carrito_Sandwich(String nombre,double precioProducto) {
        this.nombre = nombre;
        this.precioProducto = precioProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }
}

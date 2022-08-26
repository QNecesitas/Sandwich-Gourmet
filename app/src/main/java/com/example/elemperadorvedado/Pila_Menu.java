package com.example.elemperadorvedado;

class Pila_Menu {
    private String nombre;
    private double precio;
    private int estado;

    public Pila_Menu(String nombre, double precio, int estado) {
        this.nombre= nombre;
        this.precio = precio;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre= nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}



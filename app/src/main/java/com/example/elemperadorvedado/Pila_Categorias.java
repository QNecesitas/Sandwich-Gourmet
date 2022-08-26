package com.example.elemperadorvedado;

class Pila_Categorias {
    private String nombre;
    private int cant_productos;

    public Pila_Categorias(String nombre, int cant_productos) {
        this.nombre = nombre;
        this.cant_productos = cant_productos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCant_productos() {
        return cant_productos;
    }

    public void setCant_productos(int cant_productos) {
        this.cant_productos = cant_productos;
    }

}

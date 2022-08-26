package com.example.elemperadorvedado;

import java.util.ArrayList;

public class Pila_Pedidos {
    private String id;
    private String direccion;
    private String hora;
    private String latitud;
    private String longitud;
    private String precio;
    private String estado;
    private String explicacion;
    private String formaPago;
    private String numCel;
    private String numWapp;
    private ArrayList<Pila_Elementos> array_varios_elementos;
    private ArrayList<Pila_Sandwiches> array_varios_sandwiches;

    public Pila_Pedidos(String id, String direccion, String hora, String latitud, String longitud, String precio, String estado, String explicacion, String formaPago, String numCel, String numWapp, ArrayList<Pila_Elementos> array_varios_elementos, ArrayList<Pila_Sandwiches> array_varios_sandwiches) {
        this.id = id;
        this.direccion = direccion;
        this.hora = hora;
        this.latitud = latitud;
        this.longitud = longitud;
        this.precio = precio;
        this.estado = estado;
        this.explicacion = explicacion;
        this.formaPago = formaPago;
        this.numCel = numCel;
        this.numWapp = numWapp;
        this.array_varios_elementos = array_varios_elementos;
        this.array_varios_sandwiches = array_varios_sandwiches;
    }

    public Pila_Pedidos(){
        array_varios_elementos=new ArrayList<>();
        array_varios_sandwiches=new ArrayList<>();
    }

    public String getFormaPago() {
        return formaPago;
    }
    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getNumCel() {
        return numCel;
    }
    public void setNumCel(String numCel) {
        this.numCel = numCel;
    }

    public String getNumWapp() {
        return numWapp;
    }
    public void setNumWapp(String numWapp) {
        this.numWapp = numWapp;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getLatitud() {
        return latitud;
    }
    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getPrecio() {
        return precio;
    }
    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getExplicacion() {
        return explicacion;
    }
    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public ArrayList<Pila_Elementos> getArray_varios_elementos() {
        return array_varios_elementos;
    }
    public void setArray_varios_elementos(ArrayList<Pila_Elementos> array_varios_elementos) {
        this.array_varios_elementos = array_varios_elementos;
    }

    public ArrayList<Pila_Sandwiches> getArray_varios_sandwiches() {
        return array_varios_sandwiches;
    }
    public void setArray_varios_sandwiches(ArrayList<Pila_Sandwiches> array_varios_sandwiches) {
        this.array_varios_sandwiches = array_varios_sandwiches;
    }
}

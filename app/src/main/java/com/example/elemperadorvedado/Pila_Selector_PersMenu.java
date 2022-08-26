package com.example.elemperadorvedado;

public class Pila_Selector_PersMenu {
    String nombreVisible;
    String valorEnBd;

    public Pila_Selector_PersMenu(String nombreVisible, String valorEnBd) {
        this.nombreVisible = nombreVisible;
        this.valorEnBd = valorEnBd;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }
    public void setNombreVisible(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getValorEnBd() {
        return valorEnBd;
    }
    public void setValorEnBd(String valorEnBd) {
        this.valorEnBd = valorEnBd;
    }
}

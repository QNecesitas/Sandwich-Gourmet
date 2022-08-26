package com.example.elemperadorvedado;

import java.util.ArrayList;

class Carrito_Elementos {
    private static ArrayList<Pila_Elementos> arrayList_elementos=new ArrayList<>();
    private static ArrayList<Pila_Sandwiches> arrayList_sandwiches =new ArrayList<>();
    private static int posicionesSandwichLlenas=0;
    private static int posicionesElementosLlenas=0;

    public static ArrayList<Pila_Elementos> getArrayList_elementos() {
        return arrayList_elementos;
    }
    public static ArrayList<Pila_Sandwiches> getArrayList_sandwiches() {
        return arrayList_sandwiches;
    }

    public static int getCantidadSandwich() {
        return posicionesSandwichLlenas;
    }
    public static int getCantidadElementos() {
        return posicionesElementosLlenas;
    }

    public static void vaciarCarrito(){
        arrayList_elementos.clear();
        arrayList_sandwiches.clear();
        posicionesElementosLlenas=0;
        posicionesSandwichLlenas=0;
    }

    public static void addSandwich(Pila_Sandwiches pila_sandwiches){
        arrayList_sandwiches.add(pila_sandwiches);
        posicionesSandwichLlenas++;
    }
    public static void addElemento(Pila_Elementos pila_elemnto){
        arrayList_elementos.add(pila_elemnto);
        posicionesElementosLlenas++;
    }

    public static void delSandwich(int position){
        arrayList_sandwiches.remove(position);
        posicionesSandwichLlenas--;
    }
    public static void delElemento(int position){
        arrayList_elementos.remove(position);
        posicionesElementosLlenas--;
    }

    public static boolean isEmpty(){
        return arrayList_elementos.isEmpty()&&arrayList_sandwiches.isEmpty();
    }

}

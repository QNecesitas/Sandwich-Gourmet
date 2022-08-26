package com.example.elemperadorvedado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorR_Carrito_Elementos extends RecyclerView.Adapter<AdaptadorR_Carrito_Elementos.CarritoViewHolder> {
    private Context context;
    private ArrayList<Pila_Elementos> arrayList_elementos;

    private RecyclerDeleteListener recyclerDeleteListener;

    public void setRecyclerClickListener(RecyclerDeleteListener recyclerDeleteListener) {
        this.recyclerDeleteListener = recyclerDeleteListener;
    }

    //interfaces
    public interface RecyclerDeleteListener {
        void onClick(View v, int position);
    }


    public AdaptadorR_Carrito_Elementos(Context context, ArrayList<Pila_Elementos> pila__elementos){
        this.context=context;
        this.arrayList_elementos = pila__elementos;
        }

    @Override
    public CarritoViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.recycler_carrito,null);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarritoViewHolder holder, int position){
        Pila_Elementos pila = arrayList_elementos.get(position);

        //Nombre y precio
        String texto_nombre_cant=pila.getNombre()+" x"+pila.getCantidad();
        holder.nombre_cant.setText(texto_nombre_cant);
        double precio= pila.getCantidad()* pila.getPrecioProducto();
        String textoPrecio=precio+" CUP";
        holder.precio.setText(textoPrecio);

    }

    public double getPrecioTotalDeLosElementos(){
        double precioTotalCarrito=0.0;

        for (int f=0;f<arrayList_elementos.size();f++){

            precioTotalCarrito+=arrayList_elementos.get(f).getPrecioProducto()*arrayList_elementos.get(f).getCantidad();

        }

        return precioTotalCarrito;
    }

    @Override
    public int getItemCount(){
        return arrayList_elementos.size();
    }

    class CarritoViewHolder extends RecyclerView.ViewHolder{
        TextView nombre_cant;
        TextView precio;
        TextView elementos;
        ImageView cancelar;

        public CarritoViewHolder(final View itemView){
            super(itemView);

            nombre_cant=(TextView)itemView.findViewById(R.id.recycler_carrito_nombre_prod);
            precio=(TextView)itemView.findViewById(R.id.recycler_carrito_precio);
            elementos=(TextView)itemView.findViewById(R.id.recycler_carrito_elementos);
            cancelar=(ImageView)itemView.findViewById(R.id.recycler_carrito_eliminar);

            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerDeleteListener !=null) recyclerDeleteListener.onClick(itemView,getAdapterPosition());
                }
            });

        }

    }

}

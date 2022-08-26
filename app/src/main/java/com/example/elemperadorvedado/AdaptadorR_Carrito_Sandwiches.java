package com.example.elemperadorvedado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorR_Carrito_Sandwiches extends RecyclerView.Adapter<AdaptadorR_Carrito_Sandwiches.CarritoViewHolder> {
    private Context context;
    private ArrayList<Pila_Sandwiches> arrayList_sandwiches;
    private RecyclerDeleteListener recyclerDeleteListener;

    public void setRecyclerClickListener(RecyclerDeleteListener recyclerDeleteListener) {
        this.recyclerDeleteListener = recyclerDeleteListener;
    }

    //interfaces
    public interface RecyclerDeleteListener {
        void onClick(View v, int position);
    }


    public AdaptadorR_Carrito_Sandwiches(Context context, ArrayList<Pila_Sandwiches> pila_sandwiches) {
        this.context = context;
        this.arrayList_sandwiches=pila_sandwiches;
    }


    @Override
    public CarritoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_carrito, null);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarritoViewHolder holder, int position) {
        Pila_Sandwiches pila = arrayList_sandwiches.get(position);

        //Nombre y precio
        String texto_nombre_cant = "Sandwich x" + pila.getCantidad();
        holder.nombre_cant.setText(texto_nombre_cant);
        double precio = pila.getCantidad() * pila.getPrecioProducto();
        String textoPrecio = precio + " CUP";
        holder.precio.setText(textoPrecio);

        holder.elementos.setVisibility(View.VISIBLE);
        StringBuilder stringBuilder = new StringBuilder();
        for (int f = 0; f < pila.getSubproducto_sandwiches().size(); f++) {
            String nombreSubproducto = pila.getSubproducto_sandwiches().get(f).getNombre();
            String precioSubproducto = String.valueOf(pila.getSubproducto_sandwiches().get(f).getPrecioProducto());
            stringBuilder.append(nombreSubproducto);
            stringBuilder.append(" -- ");
            stringBuilder.append(precioSubproducto);
            stringBuilder.append("CUP");
            stringBuilder.append("\n");
        }
        holder.elementos.setText(stringBuilder.toString());

    }

    public double getPrecioTotalDeLosSandwiches() {
        double precioTotalCarrito = 0.0;

        for (int f = 0; f < arrayList_sandwiches.size(); f++) {
            precioTotalCarrito += arrayList_sandwiches.get(f).getPrecioProducto() * arrayList_sandwiches.get(f).getCantidad();
        }

        return precioTotalCarrito;
    }
    public double getPrecioTotalDelSandwich(ArrayList<Pila_Sandwich_Subproductos> arrayList_de_subproductos){
        double precioSandwich=0.0;

        for (int f=0;f<arrayList_de_subproductos.size();f++){
            precioSandwich+=arrayList_de_subproductos.get(f).getPrecioProducto();
        }
        return precioSandwich;
    }

    @Override
    public int getItemCount() {
        return arrayList_sandwiches.size();
    }

    class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView nombre_cant;
        TextView precio;
        TextView elementos;
        ImageView cancelar;

        public CarritoViewHolder(final View itemView) {
            super(itemView);

            nombre_cant = (TextView) itemView.findViewById(R.id.recycler_carrito_nombre_prod);
            precio = (TextView) itemView.findViewById(R.id.recycler_carrito_precio);
            elementos = (TextView) itemView.findViewById(R.id.recycler_carrito_elementos);
            cancelar = (ImageView) itemView.findViewById(R.id.recycler_carrito_eliminar);

            cancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerDeleteListener != null)
                        recyclerDeleteListener.onClick(itemView, getAdapterPosition());
                }
            });

        }

    }

}

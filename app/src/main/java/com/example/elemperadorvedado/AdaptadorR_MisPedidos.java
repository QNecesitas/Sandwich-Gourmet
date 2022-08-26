package com.example.elemperadorvedado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorR_MisPedidos extends RecyclerView.Adapter<AdaptadorR_MisPedidos.CarritoViewHolder> {
    private Context context;
    private ArrayList<Pila_Pedidos> arrayList_misPedidos;
    private RecyclerUbicacionListener UbicListener;
    private String estado;

    //interfaces
    public interface RecyclerUbicacionListener {
        void onClickUbicacion(int position);
    }



    //Setters de clicks
    public void setUbicListener(RecyclerUbicacionListener ubicListener) {
        UbicListener = ubicListener;
    }


    public AdaptadorR_MisPedidos(Context context, ArrayList<Pila_Pedidos> pila_carrito_general) {
        this.context = context;
        this.arrayList_misPedidos = pila_carrito_general;
    }

    @Override
    public CarritoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_mis_pedidos, null);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CarritoViewHolder holder, int position) {
        Pila_Pedidos pila = arrayList_misPedidos.get(position);

        //Fase 1
        holder.tv_hora.setText(pila.getHora());
        holder.tv_precio.setText(pila.getPrecio());
        estado = pila.getEstado();
        switch (estado) {
            case "En espera":
                holder.tv_estado.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_redondeado_azul_chip));
                holder.tv_estado.setTextColor(ContextCompat.getColor(context, R.color.azul));
                holder.tv_estado.setText(context.getString(R.string.en_espera));
                break;
            case "Aceptado":
                holder.tv_estado.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_redondeado_verde_chip));
                holder.tv_estado.setTextColor(ContextCompat.getColor(context, R.color.verde_brillante));
                holder.tv_estado.setText(context.getString(R.string.aceptado));
                break;
            case "Cancelado":
                holder.tv_estado.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_redondeado_rojo_chip));
                holder.tv_estado.setTextColor(ContextCompat.getColor(context, R.color.rojo_logo));
                holder.tv_estado.setText(R.string.cancelado);
                holder.tvExplicacion.setText(pila.getExplicacion());
                holder.cardViewExpl.setVisibility(View.VISIBLE);
                break;
            case "Finalizado":
                holder.tv_estado.setBackground(ContextCompat.getDrawable(context, R.drawable.textview_redondeado_verde_chip));
                holder.tv_estado.setTextColor(ContextCompat.getColor(context, R.color.verde_brillante));
                holder.tv_estado.setText(R.string.finalizado);
                break;

        }
        //Fase 2
        holder.tv_formaPago.setText(pila.getFormaPago());
        holder.tv_numCel.setText(pila.getNumCel());
        holder.tv_numWapp.setText(pila.getNumWapp());
        //Fase 4
        holder.tv_direccion.setText(pila.getDireccion());

        //Fase 3
        ArrayList<Pila_Elementos> arrayList_varios_elementos = arrayList_misPedidos.get(position).getArray_varios_elementos();
        ArrayList<Pila_Sandwiches> arrayList_varios_sandwiches = arrayList_misPedidos.get(position).getArray_varios_sandwiches();
        StringBuilder textoAColocarBuilder = new StringBuilder();
        //Elementos
        if (arrayList_varios_elementos != null) {
            for (int posElm = 0; posElm < arrayList_varios_elementos.size(); posElm++) {
                textoAColocarBuilder
                        .append(arrayList_varios_elementos.get(posElm).getNombre())
                        .append(" x")
                        .append(arrayList_varios_elementos.get(posElm).getCantidad())
                        .append(" ----- ")
                        .append(arrayList_varios_elementos.get(posElm).getPrecioProducto())
                        .append(" CUP")
                        .append("\n");
            }
        }
        if (arrayList_varios_sandwiches != null) {
            for (int posSdw = 0; posSdw < arrayList_varios_sandwiches.size(); posSdw++) {
                //Sandwich x3 ----- 25 CUP
                textoAColocarBuilder
                        .append("Sandwich")
                        .append(" x")
                        .append(arrayList_varios_sandwiches.get(posSdw).getCantidad())
                        .append(" ----- ")
                        .append(arrayList_varios_sandwiches.get(posSdw).getPrecioProducto())
                        .append(" CUP")
                        .append("\n");

                //     Queso ----- 20 CUP
                //     Jamon _____ 60 CUP
                ArrayList<Pila_Sandwich_Subproductos> arrayList_subproductos = arrayList_varios_sandwiches.get(posSdw).getSubproducto_sandwiches();
                if (arrayList_subproductos != null) {
                    for (int y = 0; y < arrayList_subproductos.size(); y++) {
                        textoAColocarBuilder
                                .append("        ")
                                .append(arrayList_subproductos.get(y).getNombre())
                                .append(" ----- ")
                                .append(arrayList_subproductos.get(y).getPrecioProducto())
                                .append(" CUP")
                                .append("\n");
                    }
                }

            }
        }
        holder.tv_elementos.setText(textoAColocarBuilder);

    }


    @Override
    public int getItemCount() {
        return arrayList_misPedidos.size();
    }

    class CarritoViewHolder extends RecyclerView.ViewHolder {
        //Fase 1
        TextView tv_hora, tv_precio, tv_estado;
        //Fase 2
        TextView tv_formaPago, tv_numCel, tv_numWapp;
        //Fase3
        TextView tv_elementos;
        //Fase 4
        TextView tv_direccion;
        LinearLayout verEnMapa;
        //Fase 5
        CardView cardViewExpl;
        TextView tvExplicacion;

        public CarritoViewHolder(final View itemView) {
            super(itemView);

            //Fase 1
            tv_hora = (TextView) itemView.findViewById(R.id.recycler_misPedidos_hora);
            tv_precio = (TextView) itemView.findViewById(R.id.recycler_misPedidos_precio);
            tv_estado = (TextView) itemView.findViewById(R.id.recycler_misPedidos_TVestado);
            //Fase 2
            tv_formaPago = (TextView) itemView.findViewById(R.id.recycler_misPedidos_TVformaPago);
            tv_numCel = (TextView) itemView.findViewById(R.id.recycler_misPedidos_TVnumCel);
            tv_numWapp = (TextView) itemView.findViewById(R.id.recycler_misPedidos_TVnumWapp);
            //Fase3
            tv_elementos = (TextView) itemView.findViewById(R.id.recycler_misPedidos_TVelementos);
            //Fase 4
            tv_direccion = (TextView) itemView.findViewById(R.id.recycler_misPedidos_TVdireccion);
            verEnMapa=(LinearLayout)itemView.findViewById(R.id.recycler_misPedidos_ver_en_mapa);
            verEnMapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(UbicListener!=null)UbicListener.onClickUbicacion(getAdapterPosition());
                }
            });
            //Fase 5
            cardViewExpl=(CardView)itemView.findViewById(R.id.recycler_misPedidos_CVCancelado);
            tvExplicacion=(TextView)itemView.findViewById(R.id.recycler_misPedidos_TVCancelado);
        }

    }

}

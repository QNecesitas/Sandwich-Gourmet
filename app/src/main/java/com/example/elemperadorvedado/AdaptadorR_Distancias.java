package com.example.elemperadorvedado;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class AdaptadorR_Distancias extends RecyclerView.Adapter<AdaptadorR_Distancias.DistanciasViewHolder> {
    private Context context;
    private ArrayList<Pila_Distancia> pila_distancias;
    private RecyclerEditarListener editar_listener;
    private RecyclerEliminarListener eliminar_listener;

    //interfaces
    public interface RecyclerEditarListener{
        void onClickItem(View v, int position);
    }
    public interface RecyclerEliminarListener{
        void onClickItem(View v, int position);
    }

    public void setEditar_listener(RecyclerEditarListener editar_listener) {
        this.editar_listener = editar_listener;
    }
    public void setEliminar_listener(RecyclerEliminarListener eliminar_listener) {
        this.eliminar_listener = eliminar_listener;
    }


    public AdaptadorR_Distancias(Context context, ArrayList<Pila_Distancia> pila_distancias){
        this.context=context;
        this.pila_distancias = pila_distancias;
    }

    @Override
    public DistanciasViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.recycler_distancias,null);
        return new DistanciasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DistanciasViewHolder holder, int position){
        Pila_Distancia pila = pila_distancias.get(position);

        holder.distancias_in_fi.setText(pila.getDistancia_inicial()+"-"+pila.getDistancia_final()+" km");
        holder.precio.setText(pila.getPrecio()+" cup");

    }

    @Override
    public int getItemCount(){
        return pila_distancias.size();
    }

    class DistanciasViewHolder extends RecyclerView.ViewHolder{
        TextView distancias_in_fi;
        TextView precio;
        TextView editar;
        TextView eliminar;

        public DistanciasViewHolder(final View itemView){
            super(itemView);

            distancias_in_fi=(TextView)itemView.findViewById(R.id.recycler_distancia_TVKilometros);
            precio=(TextView)itemView.findViewById(R.id.recycler_distancias_TVCosto);
            editar=(TextView)itemView.findViewById(R.id.recyler_distancias_editar);
            eliminar=(TextView)itemView.findViewById(R.id.recyler_distancias_eliminar);

            editar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(editar_listener!=null) editar_listener.onClickItem(itemView,getAdapterPosition());
                }
            });

            eliminar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(eliminar_listener!=null) eliminar_listener.onClickItem(itemView,getAdapterPosition());
                }
            });
        }

    }
}

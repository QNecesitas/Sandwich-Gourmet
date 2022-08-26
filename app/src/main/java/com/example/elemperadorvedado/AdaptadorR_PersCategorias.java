package com.example.elemperadorvedado;

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


public class AdaptadorR_PersCategorias extends RecyclerView.Adapter<AdaptadorR_PersCategorias.CategoriasViewHolder> {
    private Context context;
    private ArrayList<Pila_Categorias> pila_categorias;
    private RecyclerEditarTouchListener listener_editar;
    private RecyclerEliminarTouchListener listener_eliminar;


    public interface RecyclerEditarTouchListener{
        void onClickItem(View v, int position, Drawable drawable);
    }

    public interface RecyclerEliminarTouchListener{
        void onClickItem(View v, int position);
    }

    public AdaptadorR_PersCategorias(Context context, ArrayList<Pila_Categorias> pila_categorias){
        this.context=context;
        this.pila_categorias = pila_categorias;
    }


    @Override
    public CategoriasViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.recycler_personalizar_categorias,null);
        return new CategoriasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriasViewHolder holder, int position){
        Pila_Categorias pila = pila_categorias.get(position);

        Glide.with(context)
                .load(Utiles.DIRECWEB_IMG+"Categorias_"+pila.getNombre()+".jpg")
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imagenview);
        
        holder.nombre_categoria.setText(pila.getNombre());
        holder.cant_productos.setText(pila.getCant_productos()+" "+context.getString(R.string.productos));

    }

    public void setListener_editar(RecyclerEditarTouchListener listener_editar) {
        this.listener_editar = listener_editar;
    }

    public void setListener_eliminar(RecyclerEliminarTouchListener listener_eliminar) {
        this.listener_eliminar = listener_eliminar;
    }

    @Override
    public int getItemCount(){
        return pila_categorias.size();
    }


    class CategoriasViewHolder extends RecyclerView.ViewHolder{
        TextView nombre_categoria;
        TextView cant_productos;
        ImageView imagenview, btnEditar, btnEliminar;

        public CategoriasViewHolder(final View itemView){
            super(itemView);

            nombre_categoria=(TextView)itemView.findViewById(R.id.recycler_persCategorias_TVNombre);
            cant_productos=(TextView)itemView.findViewById(R.id.recycler_persCategorias_TVCtdElementos);
            imagenview=(ImageView)itemView.findViewById(R.id.recycler_persCategorias_IV);
            btnEditar=(ImageView)itemView.findViewById(R.id.recycler_persCategorias_IVEditar);
            btnEliminar=(ImageView)itemView.findViewById(R.id.recycler_persCategorias_IVEliminar);

            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener_editar!=null)listener_editar.onClickItem(v,getAdapterPosition(),imagenview.getDrawable());
                }
            });
            btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener_eliminar!=null)listener_eliminar.onClickItem(v,getAdapterPosition());
                }
            });

        }

    }
}

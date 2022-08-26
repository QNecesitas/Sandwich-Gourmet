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

class AdaptadorR_Menu_Selection extends RecyclerView.Adapter<AdaptadorR_Menu_Selection.MenuViewHolder> {
    private Context context;
    private ArrayList<Pila_Menu> pila_menu;
    private ArrayList<Integer> array_color;
    private int positionColor=0;
    private int positionSeleccionada=-1;
    private RecyclerTouchListener listener;

    public void setListener(RecyclerTouchListener listener) {
        this.listener = listener;
    }

    public interface RecyclerTouchListener{
        void onClick(View v, int position);
    }

    public AdaptadorR_Menu_Selection(Context context, ArrayList<Pila_Menu> pila_menu){
        this.context=context;
        this.pila_menu = pila_menu;
        array_color=new ArrayList<>();
    }


    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.recycler_activity_menu,null);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position){
        Pila_Menu pila = pila_menu.get(position);

        switch (positionColor){
            case 0:
                holder.imagenview_back.setBackgroundColor(context.getColor(R.color.rosado_brillante));
                array_color.add(context.getColor(R.color.rosado_brillante));
                positionColor++;
                break;
            case 1:
                holder.imagenview_back.setBackgroundColor(context.getColor(R.color.azul_brillante));
                array_color.add(context.getColor(R.color.azul_brillante));
                positionColor++;
                break;
            case 2:
                holder.imagenview_back.setBackgroundColor(context.getColor(R.color.verde_brillante));
                array_color.add(context.getColor(R.color.verde_brillante));
                positionColor++;
                break;
            case 3:
                holder.imagenview_back.setBackgroundColor(context.getColor(R.color.naranja_brillante));
                array_color.add(context.getColor(R.color.naranja_brillante));
                positionColor++;
                break;
            case 4:
                holder.imagenview_back.setBackgroundColor(context.getColor(R.color.rojo_logo));
                array_color.add(context.getColor(R.color.rojo_logo));
                positionColor=0;
                break;
        }


        Glide.with(context)
                .load(Utiles.DIRECWEB_IMG+"Menu_"+pila.getNombre()+".jpg")
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imagenview);



        holder.nombre_producto.setText(pila.getNombre());
        holder.precio_producto.setText(String.valueOf(pila.getPrecio()));

        //Seleccinador
        if(position==positionSeleccionada){
            holder.imagenview_select.setVisibility(View.VISIBLE);
        }else{
            holder.imagenview_select.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount(){
        return pila_menu.size();
    }

    public int getPositionSeleccionada() {
        return positionSeleccionada;
    }

    class MenuViewHolder extends RecyclerView.ViewHolder{
        TextView nombre_producto;
        TextView precio_producto;
        ImageView imagenview;
        ImageView imagenview_back;
        ImageView imagenview_select;
        public MenuViewHolder(final View itemView){
            super(itemView);

            nombre_producto=(TextView)itemView.findViewById(R.id.recycler_menu_nombre_producto);
            precio_producto=(TextView)itemView.findViewById(R.id.recycler_menu_precio);
            imagenview=(ImageView)itemView.findViewById(R.id.imagen_recycler_menu);
            imagenview_back=(ImageView)itemView.findViewById(R.id.imagen_recycler_menu_bacgr);
            imagenview_select=(ImageView)itemView.findViewById(R.id.selector_recycler_menu);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    int posicionAnterior=positionSeleccionada;
                    if(positionSeleccionada==getAdapterPosition()){
                        positionSeleccionada=-1;
                    }else{
                        positionSeleccionada=getAdapterPosition();
                    }
                    notifyItemChanged(getAdapterPosition());
                    notifyItemChanged(posicionAnterior);


                    if(listener!=null)listener.onClick(itemView,getAdapterPosition());


                }
            });


        }

    }
}


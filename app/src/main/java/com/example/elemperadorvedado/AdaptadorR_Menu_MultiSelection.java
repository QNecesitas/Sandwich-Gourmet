package com.example.elemperadorvedado;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

class AdaptadorR_Menu_MultiSelection extends RecyclerView.Adapter<AdaptadorR_Menu_MultiSelection.MenuViewHolder> {
    private Context context;
    private ArrayList<Pila_Menu> pila_menu;
    private ArrayList<Integer> array_color;
    private RecyclerTouchListener listener;
    private int positionColor=0;
    private ArrayList<String> array_posiciones_seleccionadas;


    public interface RecyclerTouchListener{
        void onClickItem(View v, int position);
    }

    public AdaptadorR_Menu_MultiSelection(Context context, ArrayList<Pila_Menu> pila_menu){
        this.context=context;
        this.pila_menu = pila_menu;
        array_color=new ArrayList<>();
        array_posiciones_seleccionadas =new ArrayList<>();
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
        if(array_posiciones_seleccionadas.contains(String.valueOf(position))){
            holder.imagenview_select.setVisibility(View.VISIBLE);
        }else{
            holder.imagenview_select.setVisibility(View.GONE);
        }


    }

    public void setClickListener(RecyclerTouchListener listener){
        this.listener=listener;
    }

    public ArrayList<String> getPosiciones_seleccionadas() {
        return array_posiciones_seleccionadas;
    }

    @Override
    public int getItemCount(){
        return pila_menu.size();
    }

    public double getPrecioTotal(){
        double total=0;

        for (int f=0;f<array_posiciones_seleccionadas.size();f++){

            int posicion_del_array_seleccionada=Integer.parseInt(array_posiciones_seleccionadas.get(f));
            total+=pila_menu.get(posicion_del_array_seleccionada).getPrecio();

        }

        return total;
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


                    if(array_posiciones_seleccionadas.contains(String.valueOf(getAdapterPosition()))){
                        array_posiciones_seleccionadas.remove(String.valueOf(getAdapterPosition()));
                    }else{
                        array_posiciones_seleccionadas.add(String.valueOf(getAdapterPosition()));
                    }
                    notifyItemChanged(getAdapterPosition());

                    if(listener!=null)listener.onClickItem(itemView,getAdapterPosition());


                }
            });


        }

    }
}


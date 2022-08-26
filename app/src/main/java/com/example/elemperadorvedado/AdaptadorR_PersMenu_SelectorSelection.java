package com.example.elemperadorvedado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

class AdaptadorR_PersMenu_SelectorSelection extends RecyclerView.Adapter<AdaptadorR_PersMenu_SelectorSelection.MenuViewHolder> {
    private Context context;
    private ArrayList<Pila_Selector_PersMenu> pila_menu;
    private int positionSeleccionada=0;
    private RecyclerTouchListener listener;

    public void setListener(RecyclerTouchListener listener) {
        this.listener = listener;
    }

    public interface RecyclerTouchListener{
        void onClick(View v, int position);
    }

    public AdaptadorR_PersMenu_SelectorSelection(Context context, ArrayList<Pila_Selector_PersMenu> pila_menu){
        this.context=context;
        this.pila_menu = pila_menu;
    }


    @Override
    public AdaptadorR_PersMenu_SelectorSelection.MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater= LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.recycler_persmenu_selector,null);
        return new AdaptadorR_PersMenu_SelectorSelection.MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position){
        Pila_Selector_PersMenu pila = pila_menu.get(position);


        holder.nombre.setText(pila.getNombreVisible());

        //Seleccinador
        if(position==positionSeleccionada){
            holder.selector.setVisibility(View.VISIBLE);
        }else{
            holder.selector.setVisibility(View.GONE);
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
        TextView nombre;
        View selector;
        public MenuViewHolder(final View itemView){
            super(itemView);

            nombre=(TextView)itemView.findViewById(R.id.recycler_persMenu_selector_tv);
            selector=(View)itemView.findViewById(R.id.recycler_persMenu_selector_v);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    int posicionAnterior=positionSeleccionada;
                    positionSeleccionada=getAdapterPosition();
                    notifyItemChanged(getAdapterPosition());
                    notifyItemChanged(posicionAnterior);


                    if(listener!=null)listener.onClick(itemView,getAdapterPosition());


                }
            });


        }

    }
}


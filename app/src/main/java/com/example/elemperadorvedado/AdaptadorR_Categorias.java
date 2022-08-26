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


    public class AdaptadorR_Categorias extends RecyclerView.Adapter<AdaptadorR_Categorias.CategoriasViewHolder> {
        private Context context;
        private ArrayList<Pila_Categorias> pila_categorias;
        private RecyclerTouchListener listener;


        public interface RecyclerTouchListener{
            void onClickItem(View v, int position);
        }

        public AdaptadorR_Categorias(Context context, ArrayList<Pila_Categorias> pila_categorias){
            this.context=context;
            this.pila_categorias = pila_categorias;
        }


        @Override
        public CategoriasViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater inflater= LayoutInflater.from(context);
            View view=inflater.inflate(R.layout.recycler_activity_categorias,null);
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

        public void setClickListener(RecyclerTouchListener listener){
            this.listener=listener;
        }

        @Override
        public int getItemCount(){
            return pila_categorias.size();
        }


        class CategoriasViewHolder extends RecyclerView.ViewHolder{
            TextView nombre_categoria;
            TextView cant_productos;
            ImageView imagenview;

            public CategoriasViewHolder(final View itemView){
                super(itemView);

                nombre_categoria=(TextView)itemView.findViewById(R.id.recycler_categoria_nombre);
                cant_productos=(TextView)itemView.findViewById(R.id.recycler_categoria_cant_productos);
                imagenview=(ImageView)itemView.findViewById(R.id.imagen_recycler_categorias);


                itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(listener!=null) listener.onClickItem(itemView,getAdapterPosition());
                    }
                });


            }

        }
    }

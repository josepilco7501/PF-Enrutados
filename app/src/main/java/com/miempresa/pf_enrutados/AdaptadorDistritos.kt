package com.miempresa.pf_enrutados

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.io.PipedOutputStream

class AdaptadorDistritos(val ListaDistritos:ArrayList<Distritos>):
    RecyclerView.Adapter<AdaptadorDistritos.ViewHolder>(){

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val fImagen = itemView.findViewById<ImageView>(R.id.elemento_foto)
        val fDescripcion = itemView.findViewById<TextView>(R.id.elemento_descripcion)
        val fNombre = itemView.findViewById<TextView>(R.id.elemento_nombre)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.fImagen?.load(ListaDistritos[position].foto)
        holder?.fNombre?.text=ListaDistritos[position].nombre
        holder?.fDescripcion?.text=ListaDistritos[position].descripcion
        var id = ListaDistritos[position].id
        var nombre = ListaDistritos[position].nombre
        var latitud = ListaDistritos[position].latitud
        var longitud = ListaDistritos[position].longitud

        holder.itemView.setOnClickListener(){
            val actividad_mapa = Intent(holder.itemView.context, MapaDistrito::class.java)
            actividad_mapa.putExtra("id", id.toString())
            actividad_mapa.putExtra("nombre", nombre)
            actividad_mapa.putExtra("latitud", latitud)
            actividad_mapa.putExtra("longitud", longitud)
            holder.itemView.context.startActivity(actividad_mapa)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.elementos_distritos, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ListaDistritos.size
    }
}
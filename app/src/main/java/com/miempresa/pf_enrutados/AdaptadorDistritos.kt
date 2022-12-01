package com.miempresa.pf_enrutados

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.io.PipedOutputStream
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList


class AdaptadorDistritos(val ListaDistritos:ArrayList<Distritos>):
    RecyclerView.Adapter<AdaptadorDistritos.ViewHolder>(){

    var ListaOriginal: ArrayList<Distritos>

    init {
        ListaOriginal = ArrayList()
        ListaOriginal.addAll(ListaDistritos)
    }

    fun filtrado(txtBuscar:String) {
        var longitud:Int = txtBuscar.length
        if(longitud==0){
            ListaDistritos.clear()
            ListaDistritos.addAll(ListaOriginal)
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                var coleccion =
                    ListaDistritos.stream().filter{ i:Distritos -> i.nombre.lowercase(Locale.getDefault()).contains(txtBuscar.lowercase(
                        Locale.getDefault()))}.collect(
                        Collectors.toList())
                ListaDistritos.clear()
                ListaDistritos.addAll(coleccion)
            } else {
                for (c in ListaOriginal) {
                    if (c.nombre.lowercase(Locale.getDefault()).contains(txtBuscar.lowercase(Locale.getDefault()))){
                        ListaDistritos.add(c)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

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
        var latitud_inicial = ListaDistritos[position].latitud_inicial
        var longitud_inical = ListaDistritos[position].longitud_inicial
        var latitud_final = ListaDistritos[position].latitud_final
        var longitud_final = ListaDistritos[position].longitud_final

        holder.itemView.setOnClickListener(){

            val actividad_mapa = Intent(holder.itemView.context, MapaDistrito::class.java)
            actividad_mapa.putExtra("id", id.toString())
            actividad_mapa.putExtra("nombre", nombre)
            actividad_mapa.putExtra("latitud_incial", latitud_inicial)
            actividad_mapa.putExtra("longitud_inicial", longitud_inical)
            actividad_mapa.putExtra("latitud_final", latitud_final)
            actividad_mapa.putExtra("longitud_final", longitud_final)


            /*if (Utilidades.routes.size != 0) {
                holder.itemView.context.startActivity(actividad_mapa)
            }*/
            //println(Utilidades.routes)
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
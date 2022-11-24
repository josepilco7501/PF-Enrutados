package com.miempresa.pf_enrutados

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_listado_distritos.*
import org.json.JSONException

class listadoDistritos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_distritos)

        cargarDistritos()
    }

    fun cargarDistritos() {
        listaDistritos.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        listaDistritos.layoutManager = LinearLayoutManager(this)
        var llenarLista = ArrayList<Distritos>()
        AsyncTask.execute {
            val queue = Volley.newRequestQueue(applicationContext)
            val url = getString(R.string.urlAPI) + "/distritos"
            val stringRequest = JsonArrayRequest(url,
                Response.Listener { response ->
                    try {
                        for (i in 0 until response.length()) {
                            val id =
                                response.getJSONObject(i).getString("id")
                            val nombre =
                                response.getJSONObject(i).getString("nombre")
                            val descripcion =
                                response.getJSONObject(i).getString("descripcion")
                            val foto =
                                response.getJSONObject(i).getString("imagen")
                            val latitud =
                                response.getJSONObject(i).getString("latitud")
                            val longitud =
                                response.getJSONObject(i).getString("longitud")
                            llenarLista.add(
                                Distritos(
                                    id.toInt(),
                                    nombre,
                                    latitud,
                                    longitud,
                                    descripcion,
                                    foto
                                )
                            )
                        }
                        val adapter = AdaptadorDistritos(llenarLista)
                        listaDistritos.adapter = adapter
                    } catch (e: JSONException) {
                        Toast.makeText(
                            applicationContext,
                            "Error al obtener los datos",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        applicationContext,
                        "Verifique que esta conectado a internet",
                        Toast.LENGTH_LONG
                    ).show()
                })
            queue.add(stringRequest)
        }
    }

}
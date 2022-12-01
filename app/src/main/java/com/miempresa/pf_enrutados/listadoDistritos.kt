package com.miempresa.pf_enrutados
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_listado_distritos.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Double
import java.lang.Exception
import java.util.HashMap

public class listadoDistritos : AppCompatActivity(), SearchView.OnQueryTextListener {
    var jsonObjectRequest: JsonObjectRequest? = null
    var request: RequestQueue? = null
    var adapter: AdaptadorDistritos? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_distritos)

        txtBuscar.setOnQueryTextListener(this)
        cargarDistritos()
        request = Volley.newRequestQueue(applicationContext)
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
                            val latitud_inicial =
                                response.getJSONObject(i).getString("latitud_inicial")
                            val longitud_inicial =
                                response.getJSONObject(i).getString("longitud_inicial")
                            val latitud_final =
                                response.getJSONObject(i).getString("latitud_final")
                            val longitud_final =
                                response.getJSONObject(i).getString("longitud_final")
                            llenarLista.add(
                                Distritos(
                                    id.toInt(),
                                    nombre,
                                    latitud_inicial,
                                    longitud_inicial,
                                    latitud_final,
                                    longitud_final,
                                    descripcion,
                                    foto
                                )
                            )
                        }
                        adapter = AdaptadorDistritos(llenarLista)
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

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(s: String?): Boolean {
        if (s != null) {
            adapter!!.filtrado(s)
        }
        return false
    }


}
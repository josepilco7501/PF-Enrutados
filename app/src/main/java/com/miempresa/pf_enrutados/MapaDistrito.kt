package com.miempresa.pf_enrutados

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_mapa_distrito.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class MapaDistrito : AppCompatActivity(), OnMapReadyCallback,
    AdapterView.OnItemSelectedListener, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap:GoogleMap
    var id:String? = null
    var latitud_inicial:Double? = null
    var longitud_inicial:Double? = null
    var latitud_final:Double? = null
    var longitud_final:Double? = null
    var arraySpinner = mutableListOf<String?>()
    var nombreDistrito:String? = null
    var Marcado_inicio = LatLng(0.0,0.0)
    var Marcado_final = LatLng(0.0,0.0)
    var jRuta: JSONArray? = null
    var jParaderos: JSONArray? = null
    var listaParaderos: MutableList<String>? = mutableListOf()
    var listaNombresParaderos: MutableList<String>? = mutableListOf()
    var jNombreruta: String? = null
    var jLatitudInicial: Double? = null
    var jLongitudInicial: Double? = null
    var jLatitudFinal: Double? = null
    var jLongitudFinal: Double? = null



    var jsonObjectRequest: JsonObjectRequest? = null
    var request: RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_distrito)

        request = Volley.newRequestQueue(applicationContext)

        val bundle :Bundle? = intent.extras
        id = bundle!!.getString("id")
        latitud_inicial = bundle!!.getString("latitud_incial")?.toDouble()
        longitud_inicial = bundle!!.getString("longitud_inicial")?.toDouble()
        latitud_final = bundle!!.getString("latitud_final")?.toDouble()
        longitud_final = bundle!!.getString("longitud_final")?.toDouble()

        nombreDistrito = bundle!!.getString("nombre").toString()
        titulo_mapa_distrito.setText("DISTRITO\n${nombreDistrito}")

        //dibujarRuta(latitud_inicial.toString(),longitud_inicial.toString(),latitud_final.toString(),longitud_final.toString() )
        //obtenerDatos(latitud_inicial.toString(),longitud_inicial.toString(),latitud_final.toString(),longitud_final.toString() )
        llenarSpinner(id!!.toInt())

        spncambioruta.onItemSelectedListener = this

    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(mapa: GoogleMap) {
        mMap = mapa
        mMap.clear()
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.uiSettings.setZoomControlsEnabled(true)
        mMap.uiSettings.setCompassEnabled(true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 123)
            };
        }

        mMap.addMarker(MarkerOptions().position(Marcado_inicio).title("Punto de partida"))
        mMap.addMarker(MarkerOptions().position(Marcado_final).title("Punto final"))

        var coordinatesParadero :LatLng? = null
        var coordinatesSiguienteParadero :LatLng? = null
        var par = 0
        var impar = 1
        var par_siguiente =0
        var impar_siguiente =1
        for (i in 0 until listaParaderos!!.size / 2){
            coordinatesParadero = LatLng(listaParaderos!!.get(par).toDouble(), listaParaderos!!.get(impar).toDouble())
            //mMap.addMarker(MarkerOptions().position(coordinates))
            mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource
               (R.drawable.bus_stop)).position(coordinatesParadero).title(listaNombresParaderos!!.get(i)))
            println(par)
            println(impar)
            par = par +2
            impar = impar +2
            println(i)
        }

        btnSiguienteParadero.setOnClickListener(){
            if(impar_siguiente+1 <= listaParaderos!!.size){
                coordinatesSiguienteParadero = LatLng(listaParaderos!!.get(par_siguiente).toDouble(), listaParaderos!!.get(impar_siguiente).toDouble())
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(coordinatesSiguienteParadero!!,16f),
                    1500,
                    null)
                par_siguiente = par_siguiente +2
                impar_siguiente = impar_siguiente +2
            }else{
                par_siguiente =0
                impar_siguiente =1
                coordinatesSiguienteParadero = LatLng(listaParaderos!!.get(par_siguiente).toDouble(), listaParaderos!!.get(impar_siguiente).toDouble())
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(coordinatesSiguienteParadero!!,16f),
                    1500,
                    null)
                par_siguiente = par_siguiente +2
                impar_siguiente = impar_siguiente +2
            }

        }

        btnInicio.setOnClickListener(){
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(Marcado_inicio,16f),
                1500,
                null)
        }
        btnFinal.setOnClickListener(){
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(Marcado_final,16f),
                1500,
                null)
        }

        var center: LatLng? = null
        var points: ArrayList<LatLng?>? = null
        var lineOptions: PolylineOptions? = null

        for (i in Utilidades.routes.indices) {
            points = ArrayList()
            lineOptions = PolylineOptions()

            // Obteniendo el detalle de la ruta
            val path = Utilidades.routes[i]

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for (j in path.indices) {
                val point = path[j]
                val lat = point["lat"]!!.toDouble()
                val lng = point["lng"]!!.toDouble()
                val position = LatLng(lat, lng)


                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = LatLng(lat, lng)
                }
                points.add(position)
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points)
            //Definimos el grosor de las Polilíneas
            lineOptions.width(5f)
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE)
        }
        mMap.addPolyline(lineOptions!!)

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(Marcado_inicio,15f),
            1500,
            null)

        mMap.setOnMarkerClickListener(this)
    }

    fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = java.util.ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = (if ((result and 1) != 0) (result shr 1).inv() else (result shr 1))
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = (if ((result and 1) != 0) (result shr 1).inv() else (result shr 1))
            lng += dlng
            val p = LatLng(
                ((lat.toDouble() / 1E5)),
                ((lng.toDouble() / 1E5))
            )
            poly.add(p)
        }
        return poly
    }


    public fun trazarLinea(
        latitudInicial: String,
        longitudInicial: String,
        latitudFinal: String,
        longitudFinal: String,
    ) {

        //String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
        //+"&destination="+latitudFinal+","+longitudFinal+"&key=AIzaSyCAWMVhUmj07yGYFzmD5WPAcmdNvRsSXJo";
        val url =
            ("https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
        +"&destination="+latitudFinal+","+longitudFinal+"&key=AIzaSyCAWMVhUmj07yGYFzmD5WPAcmdNvRsSXJo")
        idLoadingPB.setVisibility(View.VISIBLE);
        jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response -> //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                var jRoutes: JSONArray? = null
                var jLegs: JSONArray? = null
                var jSteps: JSONArray? = null
                try {
                    jRoutes = response.getJSONArray("routes")
                    /** Traversing all routes  */
                    for (i in 0 until jRoutes.length()) {
                        jLegs = (jRoutes.get(i) as JSONObject).getJSONArray("legs")
                        val path: MutableList<HashMap<String, String>> = ArrayList()
                        /** Traversing all legs  */
                        for (j in 0 until jLegs.length()) {
                            jSteps = (jLegs.get(j) as JSONObject).getJSONArray("steps")
                            /** Traversing all steps  */
                            for (k in 0 until jSteps.length()) {
                                var polyline = ""
                                polyline =
                                    ((jSteps.get(k) as JSONObject)["polyline"] as JSONObject)["points"] as String
                                val list = decodePoly(polyline)
                                /** Traversing all points  */
                                for (l in list.indices) {
                                    val hm = HashMap<String, String>()
                                    hm["lat"] = java.lang.Double.toString(list.get(l).latitude)
                                    hm["lng"] = java.lang.Double.toString(list.get(l).longitude)
                                    path.add(hm)
                                }
                            }
                            Utilidades.routes.add(path)
                        }
                    }
                } catch (e: JSONException) {
                    //e.printStackTrace()
                } catch (e: Exception) {
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(
                        applicationContext,
                        "No se puede conectar $error",
                        Toast.LENGTH_LONG
                    ).show()
                    println()
                    Log.d("ERROR: ", error.toString())
                }
            }
        )
        request!!.add(jsonObjectRequest)
    }

    fun llenarSpinner(id:Int){
        val url =
            (getString(R.string.urlAPI)+"/distritos/${id}")
        jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response -> //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                var jRuta: JSONArray? = null

                try {

                    //jLegs = (jRoutes.get(0) as JSONObject).getJSONArray("legs")
                    jRuta = response.getJSONArray("rutas")


                    //jLegs = (jRoutes.get(0) as JSONObject).getJSONArray("legs")

                    //jDistance = jLegs.getJSONObject(0).getJSONObject("distance").getString("text")

                    //Duracion = (jLegs.get(0) as JSONObject).getJSONArray("duration")

                    for (j in 0 until jRuta.length()){
                        jNombreruta = (jRuta.get(j) as JSONObject).getString("nombre")
                        arraySpinner.add(jNombreruta)
                    }
                    var courses = arrayOf<String?>(arraySpinner[0],arraySpinner[1],arraySpinner[2],arraySpinner[3])


                    val ad : ArrayAdapter<*> = ArrayAdapter<String?>(
                        this,
                        android.R.layout.simple_spinner_item,
                        courses)

                    ad.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item)

                    spncambioruta.adapter = ad
                    println(jRuta)
                    println(jNombreruta)

                    println("hola llenar spinner")

                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(
                        applicationContext,
                        "No se puede conectar $error",
                        Toast.LENGTH_LONG
                    ).show()
                    println()
                    Log.d("ERROR: ", error.toString())
                }
            }
        )
        request!!.add(jsonObjectRequest)
    }

    fun obtenerDatos(
        latitudInicial: String,
        longitudInicial: String,
        latitudFinal: String,
        longitudFinal: String,
    ){
        val url =
            ("https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                    +"&destination="+latitudFinal+","+longitudFinal+"&key=AIzaSyCAWMVhUmj07yGYFzmD5WPAcmdNvRsSXJo")
        jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response -> //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                var jRoutes: JSONArray? = null
                var jLegs: JSONArray? = null
                var jDistance: String? = null
                var jDuration: String? = null
                try {
                    jRoutes = response.getJSONArray("routes")

                    jLegs = (jRoutes.get(0) as JSONObject).getJSONArray("legs")

                    jDistance = jLegs.getJSONObject(0).getJSONObject("distance").getString("text")

                    jDuration = jLegs.getJSONObject(0).getJSONObject("duration").getString("text")

                    //Duracion = (jLegs.get(0) as JSONObject).getJSONArray("duration")
                    println(jRoutes)
                    println(jLegs)
                    println(jDistance)
                    println(jDuration)
                    //println(jRoutes)
                    et_informacionruta.setText("${jDistance}\n${jDuration}")



                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(
                        applicationContext,
                        "No se puede conectar $error",
                        Toast.LENGTH_LONG
                    ).show()
                    println()
                    Log.d("ERROR: ", error.toString())
                }
            }
        )
        request!!.add(jsonObjectRequest)
    }



    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val url =
            (getString(R.string.urlAPI)+"/distritos/${id}")
        jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener { response -> //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.

                try {
                    listaParaderos?.clear()

                    //jLegs = (jRoutes.get(0) as JSONObject).getJSONArray("legs")
                    jRuta = response.getJSONArray("rutas")
                    jParaderos = (jRuta!!.get(p2) as JSONObject).getJSONArray("paraderos")
                    println(jParaderos)

                    for (i in 0 until jParaderos!!.length()) {
                        println("paradero${i}")
                        println((jParaderos!!.get(i) as JSONObject).getString("nombre"))
                        listaParaderos?.add((jParaderos!!.get(i) as JSONObject).getString("latitud"))
                        listaParaderos?.add((jParaderos!!.get(i) as JSONObject).getString("longitud"))
                        listaNombresParaderos?.add((jParaderos!!.get(i) as JSONObject).getString("nombre"))
                    }
                    println(listaParaderos)
                    //println(paradero)

                    jLatitudInicial = (jRuta?.get(p2) as JSONObject).getString("latitud_inicial").toDouble()
                    jLongitudInicial = (jRuta?.get(p2) as JSONObject).getString("longitud_inicial").toDouble()
                    jLatitudFinal = (jRuta?.get(p2) as JSONObject).getString("latitud_final").toDouble()
                    jLongitudFinal = (jRuta?.get(p2) as JSONObject).getString("longitud_final").toDouble()


                    Marcado_inicio = LatLng(jLatitudInicial!!,jLongitudInicial!!)
                    Marcado_final = LatLng(jLatitudFinal!!,jLongitudFinal!!)

                    trazarLinea(jLatitudInicial.toString(),jLongitudInicial.toString(),jLatitudFinal.toString(),jLongitudFinal.toString())
                    //obtenerDatos(jLatitudInicial.toString(),jLongitudInicial.toString(),jLatitudFinal.toString(),jLongitudFinal.toString() )

                    idLoadingPB.setVisibility(View.VISIBLE)

                       Handler().postDelayed({
                            createFragment()
                            idLoadingPB.setVisibility(View.GONE)
                            obtenerDatos(jLatitudInicial.toString(),jLongitudInicial.toString(),jLatitudFinal.toString(),jLongitudFinal.toString() )

                        }, 11000)

                    println("se ejecuto on item selected")

                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(
                        applicationContext,
                        "No se puede conectar $error",
                        Toast.LENGTH_LONG
                    ).show()
                    println()
                    Log.d("ERROR: ", error.toString())
                }
            }
        )
        request!!.add(jsonObjectRequest)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_mapadistrito, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if ( id == R.id.agregarruta){
            val intent = Intent(this, ActivityAgregarRuta::class.java)
            intent.putExtra("nombreDistrito",nombreDistrito)
            startActivity(intent)
        }
        println("se selecciono un item de menu ")

        return super.onOptionsItemSelected(item)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
       println("dasd")
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val intent = Intent(this, Activity_Paradero3D::class.java)

        intent.putExtra("latitud", p0.getPosition().latitude.toString())
        intent.putExtra("longitud", p0.getPosition().longitude.toString())
        intent.putExtra("tituloParadero", p0.title.toString())
        //intent.putExtra("nombreRuta", nombreDistrito)
        startActivity(intent)

        return true
    }


}

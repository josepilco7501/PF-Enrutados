package com.miempresa.pf_enrutados

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_mapa_distrito.*

class MapaDistrito : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map:GoogleMap
    var latitud:Double? = null
    var longitud:Double? = null
    var nombre:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_distrito)

        val bundle :Bundle? = intent.extras
        latitud = bundle!!.getString("latitud")?.toDouble()
        longitud = bundle!!.getString("longitud")?.toDouble()

        nombre = bundle!!.getString("nombre").toString()
        titulo_mapa_distrito.setText("DISTRITO\n${nombre}")

        createFragment()


    }

    private fun createFragment() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(mapa: GoogleMap) {
        map = mapa
        createMarker()
    }

    private fun createMarker() {
        val coordinates = LatLng(latitud!!, longitud!!)
        val marker = MarkerOptions().position(coordinates).title(nombre)
        map.addMarker(marker)
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates,14.5f),
            4000,
            null
        )
    }
}
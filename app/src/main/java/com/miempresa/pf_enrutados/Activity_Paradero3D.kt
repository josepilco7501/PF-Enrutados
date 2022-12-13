package com.miempresa.pf_enrutados

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.StreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_paradero3_d.*

class Activity_Paradero3D : AppCompatActivity(), OnStreetViewPanoramaReadyCallback {

    private var bundle: Bundle? = null
    private var latitud: String? = null
    private var longitud: String? = null
    private var tituloParadero: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paradero3_d)

        val streetViewPanoramaFragment = fragmentManager
            .findFragmentById(R.id.streetviewpanorama) as StreetViewPanoramaFragment
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this)
        bundle = intent.extras
        latitud = bundle!!.getString("latitud")
        longitud = bundle!!.getString("longitud")
        tituloParadero = bundle!!.getString("tituloParadero")
        println(latitud)
        println(longitud)

        titulo_paradero.setText("PARADERO\n${tituloParadero}")

    }

    override fun onStreetViewPanoramaReady(p0: StreetViewPanorama) {
        p0.setPosition(LatLng(latitud!!.toDouble(), longitud!!.toDouble()))

    }
}
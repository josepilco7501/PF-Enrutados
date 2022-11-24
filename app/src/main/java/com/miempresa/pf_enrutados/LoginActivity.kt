package com.miempresa.pf_enrutados

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener(){
            val listadodistritos = Intent(applicationContext, listadoDistritos::class.java)
            startActivity(listadodistritos)
        }
    }
}
package com.miempresa.pf_enrutados

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLogin.setOnClickListener(){



            val activit_login = Intent(applicationContext, LoginActivity::class.java)
            startActivity(activit_login)
        }

        btnRegistrar.setOnClickListener(){
            val activit_registrar = Intent(applicationContext,RegisterActivity::class.java)
            startActivity(activit_registrar)
        }
    }
}
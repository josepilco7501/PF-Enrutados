package com.miempresa.pf_enrutados

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener(){
            val correo = et_email.text.toString()
            val clave = et_contraseña.text.toString()
            val queue = Volley.newRequestQueue(this)
            var url = getString(R.string.urlAPI)+"/usuarios?"
            url = url + "correo=" + correo + "&contraseña=${clave}"

            println(url)


            val stringRequest = JsonArrayRequest(url,
                Response.Listener { response ->
                    try {
                        val valor = response.getJSONObject(0)
                        this.finish()
                        val llamaractividad= Intent(applicationContext, listadoDistritos::class.java)
                        startActivity(llamaractividad)
                    }catch (e: JSONException){
                        Toast.makeText(
                            applicationContext,
                            "Error en las credenciales",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        applicationContext,
                        "Compruebe que tiene acceso a internet",
                        Toast.LENGTH_LONG
                    ).show()
                })
            queue.add(stringRequest)
        }
    }
}
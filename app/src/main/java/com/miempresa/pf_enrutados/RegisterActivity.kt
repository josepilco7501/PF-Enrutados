package com.miempresa.pf_enrutados

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_registrar.setOnClickListener() {
            if (et_email.getText().toString().isEmpty() || et_fullname.getText().toString()
                    .isEmpty() || et_password.getText().toString().isEmpty()
                || et_username.toString().isEmpty() || et_confirm_password.toString().isEmpty()
            ) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Por favor completa todos los campos :)")
                    .setNegativeButton("Ok") { dialogInterface, i -> dialogInterface.dismiss() }
                builder.show()
            }else {
                if (et_password.text.toString() == et_confirm_password.text.toString()) {
                    cargarDatos(
                        et_fullname.text.toString(),
                        et_email.text.toString(),
                        et_username.text.toString(),
                        et_password.text.toString()
                    )
                }else{
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Las contraseñas no coinciden :(")
                        .setNegativeButton("Ok") { dialogInterface, i -> dialogInterface.dismiss() }
                    builder.show()
                }
            }

        }
    }

    private fun cargarDatos(nombre: String, correo: String, usuario:String,contraseña:String) {
        //url donde esta almacenada nuestra apirest
        val url = "http://192.168.1.3:3000/usuarios?"

        //Creacion de la variable para la cola de solicitudes
        val queue = Volley.newRequestQueue(this@RegisterActivity)

        //Creando el metodo de solicitud para subir nuestros datos - POST
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                //Vaciamos los ediText para agregar otro usuario
                et_email.setText("")
                et_username.setText("")
                et_password.setText("")
                et_fullname.setText("")

                try {
                    //Analizamos los datos del objeto JSON y lo guardamos en la variable
                    val objJSON = JSONObject(response)

                    //Extraemos los datos del objeto JSON

                    val nombre = objJSON.getString("nombre")
                    val correo = objJSON.getString("correo")
                    val usuario  = objJSON.getString("usuario")
                    val contraseña  = objJSON.getString("contraseña")

                    //Mostramos todos los datos que obtenemos en un TextView

                    Toast.makeText(this@RegisterActivity, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> // method to handle errors.
                Toast.makeText(
                    this@RegisterActivity,
                    "Fail to get response = $error",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getParams(): Map<String, String>? {
                //Estamos creando un objeto HashMap donde se almacenará nuestros datos
                //usando una clave y valor,
                val params: MutableMap<String,String> = HashMap()

                //Estamos pasando la clave y el valor para el nuevo usuario
                params["nombre"] = nombre
                params["correo"] = correo
                params["usuario"] = usuario
                params["contraseña"] = contraseña


                //Estamos devolviendo los valores
                return params
            }
        }
        //Estamos haciendo la solicitud al objeto JSON.
        queue.add(request)
    }

}
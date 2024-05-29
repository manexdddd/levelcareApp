package mx.tecnm.cdhidalgo.iotapp

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity4 : AppCompatActivity(),itemListener {
    private lateinit var rvList: RecyclerView
    private lateinit var btnRefresh:ImageView
    private lateinit var back:ImageView
    lateinit var sesion: SharedPreferences //guarda o lee datos
    lateinit var  lista:Array<Array<String?>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        rvList = findViewById(R.id.rvList)
        btnRefresh = findViewById(R.id.btnRefresh)
        back = findViewById(R.id.btnBack)
        sesion = getSharedPreferences("sesion", 0)
        //3 minimos para que funcione el recycler
        rvList.setHasFixedSize(true) //bloquea el tamaño de ventana
        rvList.itemAnimator = DefaultItemAnimator() //animacion al mover los items
        rvList.layoutManager = LinearLayoutManager(this)





        fill()
  back.setOnClickListener {
      finish()
  }

        btnRefresh.setOnClickListener{
            fill()

        }
    }
    private fun fill() {
        val url = Uri.parse(Config.URL + "notifications")
            .buildUpon()
            .build().toString()

        val peticion = object : JsonArrayRequest(Request.Method.GET, url, null, { response ->
            // Handle the JSON array response
            val jsonArray = response
            var tempNotifications = mutableListOf<Map<String, Any>>()


            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val notification = mapOf(
                    "id" to jsonObject.getString("id"),
                    "title" to jsonObject.getString("title"),
                    "subtitle" to jsonObject.getString("subtitle"),
                    "date" to jsonObject.getString("date"),
                    "updated_at" to jsonObject.getString("updated_at") // Assuming updated_at is a long
                )
                tempNotifications.add(notification)
            }



// Assuming tempNotifications is a MutableList<Map<String, Any>> and contains 'updated_at' keys
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT)

// Correctly sort the temporary list by 'updated_at' in descending order
            tempNotifications.sortByDescending { entry ->
                val date = dateFormat.parse(entry["updated_at"] as String)
                date.time
            }



            // Now, populate 'lista' with the sorted notifications
            lista = Array(tempNotifications.size) { i ->
                arrayOfNulls<String?>(5)
            }.also { array ->
                for ((index, notification) in tempNotifications.withIndex()) {
                    array[index][0] = notification["id"] as String?
                    array[index][1] = notification["title"] as String?
                    array[index][2] = notification["subtitle"] as String?
                    array[index][3] = notification["date"] as String?
                    array[index][4] = notification["updated_at"] as String?
                }
            }

            rvList.adapter = MyAdapter(lista, this)

        }, { error ->
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
        }) {
            override fun getHeaders(): Map<String, String> {
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString() // Obtienes el token guardado
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }







    override fun onClick(v: View?, position: Int) {
        Toast.makeText(this,"Click en $position",Toast.LENGTH_SHORT).show()
    }

    override fun onEdit(v: View?, position: Int) {
        val i  = Intent(this,MainActivity3::class.java)
        val intent = Intent(this, MainActivity3::class.java)
        intent.putExtra("id", lista[position][0])
        intent.putExtra("name", lista[position][1])
        intent.putExtra("type", lista[position][2])
        intent.putExtra("value", lista[position][3])
        startActivity(intent)
    }

    override fun onDel(v: View?, position: Int) {
        AlertDialog.Builder(this)
            .setTitle(("Eliminar"))
            .setMessage("seguro de eliminar ${lista[position][1]}?")
            .setPositiveButton("SI"){dialog,which->
                val url = Uri.parse(Config.URL +"notifications/"+ lista[position][0])
                    .buildUpon()
                    .build().toString()
                val peticion = object: StringRequest(Request.Method.DELETE, url, {
                        response ->fill()
                }, {
                        error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                    fill()
                }) {
                    override fun getHeaders(): Map<String, String>{
                        val body: MutableMap<String, String> = HashMap()
                        body["Authorization"] = sesion.getString("jwt", "").toString() //obtiene el token guardado
                        return body
                    }
                }
                MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)


            }
            .setNegativeButton("NO",null)
            .show()

    }
}
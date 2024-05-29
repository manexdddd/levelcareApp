package mx.tecnm.cdhidalgo.iotapp

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class
MainActivity : AppCompatActivity() {
    lateinit var etLoginUser: TextInputLayout
    lateinit var etLoginPass: TextInputLayout
    lateinit var btnLoginStart: MaterialButton
    lateinit var sesion: SharedPreferences //guarda o lee datos
    private var hasNotificationPermissionGranted = false
    private var isLoggingIn = false
    lateinit var  lista:Array<Array<String?>>

    private val notificationPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasNotificationPermissionGranted = isGranted
            if (isGranted) {
                // Permission granted, you can now send notifications
                //Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied, handle accordingly
                //Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this);






        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&!hasNotificationPermissionGranted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        val token = FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }


            val token = task.result
            fill2(token)
           //

            val msg = "FCM Token: $token"
            Log.d("FCM_TOKEN", msg)
            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }

        // Rest of your

        etLoginUser = findViewById(R.id.etLoginUser)
        etLoginPass = findViewById(R.id.etLoginPassword)
        btnLoginStart = findViewById(R.id.btnLoginStart)
        sesion = getSharedPreferences("sesion", 0)
        btnLoginStart.setOnClickListener { login() }



    }

    private fun login() {
  if(!isLoggingIn){

      isLoggingIn = true
      btnLoginStart.isEnabled = false
      btnLoginStart.setTextColor(Color.WHITE) // Assuming you have imported android.graphics.Color
      btnLoginStart.text = "Logging In..."

     etLoginUser.isEnabled = false
      etLoginPass.isEnabled = false


      val url = Uri.parse(Config.URL + "login")
          .buildUpon()
          .build().toString()

      val peticion = object: StringRequest(Request.Method.POST, url, {
              response -> with(sesion.edit()) {
          putString("jwt", response)
          putString("username", etLoginUser.editText?.text.toString())
          apply()
      }
          startActivity(Intent(this, MainActivity2::class.java))
          isLoggingIn = false
          btnLoginStart.isEnabled = true
          btnLoginStart.text = "Login"


          etLoginUser.isEnabled = true
          etLoginPass.isEnabled = true

      }, {
              error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
          isLoggingIn = false
          btnLoginStart.isEnabled = true
          btnLoginStart.text = "Login"
          etLoginUser.isEnabled = true
          etLoginPass.isEnabled = true
          Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
      }) {
          override fun getParams(): Map<String, String>{
              val body: MutableMap<String, String> = HashMap()
              body["username"] = etLoginUser.editText?.text.toString()
              body.put("password", etLoginPass.editText?.text.toString())
              return body
          }
      }
      MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
  }

    }




    private fun saveNew(token:String) {
        val url = Uri.parse(Config.URL + "tokens")//revisa que la url esta bien escrita.
            .buildUpon()
            .build().toString()

        val body  = JSONObject()
        body.put("token",token)
        val peticion = object: JsonObjectRequest(Request.Method.POST, url, body, {
                response -> Log.e(TAG,"Guardado")



        }, {
                error -> Log.e(TAG, error.toString())
        }) {
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)
    }



    private fun fill2(token: String) {
        val url = Uri.parse(Config.URL + "tokens")
            .buildUpon()
            .build().toString()

        val peticion = object : JsonArrayRequest(Request.Method.GET, url, null, { response ->
            // Handle the JSON array response
            val jsonArray = response
            var tokens = mutableListOf<Map<String, Any>>()

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val tk = mapOf(
                    "token" to jsonObject.getString("token")
                )
                tokens.add(tk)
            }

            // Check if the specific token exists in the list
            val targetTokenExists = tokens.any { it["token"] == token }
            if (targetTokenExists) {
                // Token exists, perform your action here
                Toast.makeText(this, "existe", Toast.LENGTH_SHORT).show()
            } else {
                // Token does not exist, perform your alternative action here
                Toast.makeText(this, "no existe", Toast.LENGTH_SHORT).show()
                saveNew(token)
            }

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





}





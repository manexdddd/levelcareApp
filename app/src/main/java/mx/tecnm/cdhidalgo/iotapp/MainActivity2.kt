package mx.tecnm.cdhidalgo.iotapp


import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity2 : AppCompatActivity() {
    lateinit var sesion: SharedPreferences //guarda o lee datos
    lateinit var imageAgua: ImageView //Imagen que va mostrar el cambio del nivel del Agua
    lateinit var imagetinaco: ImageView //Imagen que va mostrar el cambio del nivel del Agua
    lateinit var nivelAgua: TextView //text que muestra el % del nivel del Agua
    lateinit var btnRefresh: ImageButton//boton que actualiza los datos de los sensores
    lateinit var btninfo: ImageButton //boton que lleva a la sig ventana de info
    lateinit var btninfoTDS: ImageButton //boton que lleva a la sig ventana de info
    lateinit var TDSDatos: TextView //textView que muestra dato del TDS
    lateinit var nivelph: TextView //text que muestra el dato del PH
    lateinit var tdsCalidad: TextView //text que muestra el dato del PH
    lateinit var nivv: TextView //text que muestra el dato del PH
     lateinit var alerta:TextView
    lateinit var alerta2:ImageView
    lateinit var imgph: ImageView //text que muestra el dato del PH
    lateinit var pausa:ImageView

    //preferencias
    private val PREFS_NAME = "prefs"
    private val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private var notificationsEnabled: Boolean = true
    private var isPlayingFirstAudio: Boolean = false
    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        imageAgua = findViewById(R.id.imageAgua)
        imagetinaco = findViewById(R.id.imageTinaco)

        nivelAgua = findViewById(R.id.nivelAgua)
        btnRefresh = findViewById(R.id.btnRefresh)
        alerta = findViewById(R.id.AlertaFuga)
        btninfo = findViewById(R.id.btninfo)
        btninfoTDS = findViewById(R.id.btninfoTDS)
        sesion = getSharedPreferences("sesion", 0)
        TDSDatos = findViewById(R.id.TDSDatos)
        nivelph = findViewById(R.id.nivelph)
        tdsCalidad = findViewById(R.id.tdsCalidad)
        nivv = findViewById(R.id.nivagua)
        imgph = findViewById(R.id.nivelph2)
        alerta2 = findViewById(R.id.alerta2)
        pausa = findViewById(R.id.btn_bano)

        fill()
        mediaPlayer = MediaPlayer()
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        updateNotificationIcon()



        btnRefresh.setOnClickListener { fill()

        }

        pausa.setOnClickListener {
            notificationsEnabled = !notificationsEnabled
            updateNotificationIcon()
            updateNotificationPreference(notificationsEnabled)
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.reset()
            }

            if (notificationsEnabled) {
                mediaPlayer = MediaPlayer.create(this, R.raw.door)

            } else {
                mediaPlayer = MediaPlayer.create(this, R.raw.shower)
            }
            mediaPlayer.start()
        }

        alerta.setOnClickListener {
            startActivity(Intent(this, MainActivity4::class.java))

        }
        alerta2.setOnClickListener {
            startActivity(Intent(this, MainActivity4::class.java))

        }

        btninfo.setOnClickListener { startActivity(Intent(this, MainActivity3::class.java)) }
        btninfoTDS.setOnClickListener { startActivity(Intent(this, MainActivity3::class.java)) }




    }



    private fun fill(){
        val url = Uri.parse(Config.URL + "sensors")//revisa que la url esta bien escrita.
            .buildUpon()
            .build().toString()

        val peticion = object: JsonArrayRequest(Request.Method.GET, url, null, { response ->
            Log.e("Lista Sensors", response.toString())
            //aqui leemos los valores (0, agua, 1, tds y 2 del ph)
            //NivelPH = response.getJSONArray(0).getString("value").toDouble()
            val ph = response.getJSONObject(2).getString("value").toDouble() //Valor del PH
            val tds = response.getJSONObject(1).getString("value").toDouble() //Valor del TDS
            val nivel =
                response.getJSONObject(0).getString("value").toDouble() //Valor del nivel de agua
            nivelph.text = ph.toString()
            TDSDatos.text = tds.toString()
            Log.e("Titulo,", (100 + 650 * nivel / 100).toInt().toString())
            // val nuevoLayoutParams = LinearLayout.LayoutParams(199, 250) // Aquí defines el nuevo layout_height
            //imageAgua.layoutParams = nuevoLayoutParams

            nivelAgua.text = nivel.toString()+"%"
            // Set up the wave animation
            // Define the gradient colors


            // Set the gradient shader for the ShapeDrawable
            val gradientColors = intArrayOf(
                Color.parseColor("#00ADD8"), // Light Blue
                Color.parseColor("#007FFF") // Strong Blue
            )

            var gradientDrawable = GradientDrawable().apply {
                gradientType = GradientDrawable.LINEAR_GRADIENT
                colors = gradientColors
            }

            imageAgua.background = gradientDrawable

            val rippleTexture = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            for (x in 0 until 1) {
                for (y in 0 until 1) {
                    val color = Color.argb((255 * Math.random()).toInt(), 0, 0, 0)
                    rippleTexture.setPixel(x, y, color)
                }
            }

            val bitmapDrawable = BitmapDrawable(resources, rippleTexture)
            imageAgua.setImageDrawable(bitmapDrawable)
            val waveAnimator = ObjectAnimator.ofFloat(imageAgua, "translationY", -30f, 0f)
            waveAnimator.duration = 3000 // Duration of the animation in milliseconds
            waveAnimator.repeatCount = ObjectAnimator.INFINITE // Repeat indefinitely
            waveAnimator.repeatMode = ObjectAnimator.REVERSE // Reverse the animation direction
            waveAnimator.start()

            val rotationAnimator2 = ObjectAnimator.ofFloat(imageAgua, "rotation", 2f, 0f)
            rotationAnimator2.duration = 3000 // Match the duration with the translation animation
            rotationAnimator2.repeatCount = ObjectAnimator.INFINITE // Repeat indefinitely
            rotationAnimator2.repeatMode = ObjectAnimator.REVERSE // Reverse the animation direction
            rotationAnimator2.start()

// Optionally, adjust the wave height based on the level (nivel)
            val waveHeight = (20 + 20 * nivel / 100).toFloat()
            imageAgua.translationY = waveHeight



            // Configura los parámetros de diseño para 'imageAgua' usando MATCH_CONSTRAINT para el ancho
            val layoutParams = ConstraintLayout.LayoutParams(
                600, // Ancho responsivo
                (100 + 620 * nivel / 100).toInt()
            )
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID


            imageAgua.layoutParams = layoutParams


            if(tds<100 && tds<0){
                tdsCalidad.text = "Muy bueno!"
                tdsCalidad.setTextColor(Color.BLACK)
                tdsCalidad.setBackgroundResource(R.drawable.fondo_excelente);
            }
            if(tds>100 && tds<=300){
                tdsCalidad.setTextColor(Color.BLACK)
                tdsCalidad.text = "Excelente"
                tdsCalidad.setBackgroundResource(R.drawable.fondo_excelente);
            }
            if(tds>300 && tds <=600){
                tdsCalidad.setTextColor(Color.BLACK)
                tdsCalidad.text = "Bueno"
                tdsCalidad.setBackgroundResource(R.drawable.fondo_bueno);
            }
            if(tds>600 && tds <=900){
                tdsCalidad.setTextColor(Color.BLACK)
                tdsCalidad.text = "Regular"
                tdsCalidad.setBackgroundResource(R.drawable.fondo_regular);
            }
            if(tds>900 && tds<=1200){
                tdsCalidad.setTextColor(Color.WHITE)
                tdsCalidad.text = "Pobre"
                tdsCalidad.setBackgroundResource(R.drawable.fondo_pobre);
            }
            if(tds>1200){
                tdsCalidad.setTextColor(Color.WHITE)
                tdsCalidad.text = "Inaceptable"
                tdsCalidad.setBackgroundResource(R.drawable.fondo_inaceptable);

            }
            if(ph>6.5 && ph<=7){
                nivelph.setTextColor(Color.rgb(0,192,75))
                imgph.setImageResource(R.drawable.icon_check)

            }else{
                nivelph.setTextColor(Color.RED)
                imgph.setImageResource(R.drawable.remove)
        }

            if(nivel>0 && nivel<=25){
                nivv.setTextColor(Color.WHITE)
                nivv.text = "bajo"
                nivv.setBackgroundResource(R.drawable.fondo_inaceptable);
            }

            if(nivel>25 && nivel<=50){
                nivv.setTextColor(Color.BLACK)
                nivv.text = "Regular"
                nivv.setBackgroundResource(R.drawable.fondo_regular);
            }

            if(nivel>50 && nivel<=75){
                nivv.setTextColor(Color.BLACK)
                nivv.text = "Bueno"
                nivv.setBackgroundResource(R.drawable.fondo_bueno);
            }

            if(nivel>75){
                nivv.setTextColor(Color.BLACK)
                nivv.text = "Excelente"
                nivv.setBackgroundResource(R.drawable.fondo_excelente);
            }

            if(nivel<=0){imageAgua.visibility = View.INVISIBLE
            }else{
                imageAgua.visibility = View.VISIBLE
            }

                                                                               }, {
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
        }) {
            override fun getHeaders(): Map<String, String>{
                val body: MutableMap<String, String> = HashMap()
                body["Authorization"] = sesion.getString("jwt", "").toString() //obtiene el token guardado
                return body
            }
        }
        MySingleton.getInstance(applicationContext).addToRequestQueue(peticion)

    }


    private fun updateNotificationIcon() {
        if (notificationsEnabled) {
           pausa.setImageResource(R.drawable.pl)

        } else {
            pausa.setImageResource(R.drawable.pause)

        }
    }

    private fun updateNotificationPreference(enabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
        editor.apply()

        if (enabled) {
            enableNotifications()
        } else {
            disableNotifications()
        }
    }

    private fun enableNotifications() {
        // Suscribirse a un tema o habilitar la recepción de notificaciones
        FirebaseMessaging.getInstance().subscribeToTopic("all")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Suscripción exitosa al tema
                    Toast.makeText(this, "Modo baño desactivado", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun disableNotifications() {
        // Attempt to unsubscribe from the topic
        val unsubscribeTask = FirebaseMessaging.getInstance().unsubscribeFromTopic("all")

        unsubscribeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Successful unsubscription
                Log.d("NotificationService", "Successfully unsubscribed from topic 'all'")
                Toast.makeText(this, "Modo baño activado", Toast.LENGTH_SHORT).show()
            } else {
                // Handle failure
                Log.e("NotificationService", "Failed to unsubscribe from topic 'all'", task.exception)
                Toast.makeText(this, "Error al intentar desactivar notificaciones", Toast.LENGTH_LONG).show()
            }
        }
    }


    }


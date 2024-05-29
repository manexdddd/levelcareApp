package mx.tecnm.cdhidalgo.iotapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        lateinit var btnBack: Button //boton que regresa a la ventana de datos

        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener { startActivity(Intent(this, MainActivity2::class.java)) }
    }
}
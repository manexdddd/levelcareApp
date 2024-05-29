package mx.tecnm.cdhidalgo.iotapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logo_splash)
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        logo.startAnimation(bounceAnimation)

        // Schedule the navigation to the Login activity after the animation ends
        bounceAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                // Use a Handler to post a Runnable to navigate to the Login activity
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@Splash, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000) // Adjust the delay as needed
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }
}

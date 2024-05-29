package mx.tecnm.cdhidalgo.iotapp

import BackgroundNotificationService.Companion.CHANNEL_ID
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.time.LocalDateTime
import javax.annotation.Nullable


class FirebaseMessageReceiver : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FCM_TOKEN", "Refreshed token: $token")
        Log.e(TAG,"hello")
        super.onNewToken(token)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Check if the notification payload is not null




        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", false)

        if (notificationsEnabled) {
            // Process the message if notifications are enabled
            if (remoteMessage.getNotification()!= null) {
                val notification = remoteMessage.getNotification()
                // 'notification'
                if (notification != null) {
                    showNotification(notification.getTitle()!!, notification.getBody()!!)
                    //saveNew(notification.getTitle()!!, notification.getBody()!!)
                }
            }
        } else {
            // Optionally, log or ignore the message if notifications are disabled
        }
    }


    @SuppressLint("MissingPermission")
    fun showNotification(
        title: String,
        message: String
    ) {
        val intent = Intent(this, MainActivity4::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this.applicationContext,
            CHANNEL_ID
        )



            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo4)
            .setBadgeIconType(R.drawable.logo4)
        with(NotificationManagerCompat.from(this)) {
            notify(0, builder.build())
        }
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as? NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Web App Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
        notificationManager?.notify(0, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveNew(title2:String, body2:String) {
        val url = Uri.parse(Config.URL + "notifications")//revisa que la url esta bien escrita.
            .buildUpon()
            .build().toString()

        val currentTime = LocalDateTime.now()
        val body  = JSONObject()
        body.put("title",title2)
        body.put("subtitle",body2)
        body.put("date",currentTime)
        body.put("updated_at",currentTime)
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


}

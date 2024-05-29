import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import mx.tecnm.cdhidalgo.iotapp.R
import java.util.concurrent.TimeUnit

class BackgroundNotificationService : Service() {

    private val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        Log.d("BackgroundNotificationService", "Service started")
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Background Task")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.logo2)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)



        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Background Task Channel"
            val description = "Channel for background task notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
                this.setImportance(NotificationManager.IMPORTANCE_HIGH)
                this.enableLights(true)
                this.lightColor = Color.BLUE
                this.enableVibration(true)
                this.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val CHANNEL_ID = "background_task_channel"
        const val NOTIFICATION_ID = 1

        fun startService(context: Context) {
            val intent = Intent(context, BackgroundNotificationService::class.java)
            context.startService(intent)
        }
    }
}

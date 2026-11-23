package locutor.cristopher.sic.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import locutor.cristopher.sic.MainActivity

class RadioAnnouncerForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startInForeground()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_TRIGGER_ANNOUNCEMENT -> {
                RadioEngineCoordinator.getInstance(this).triggerSimulationNow()
            }
            ACTION_TOGGLE_ANNOUNCER -> {
                RadioEngineCoordinator.getInstance(this).toggleAnnouncer()
                updateNotification()
            }
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "I AM-FM Locutor Radio",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitoreo en segundo plano y locución horaria"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun startInForeground() {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingOpenApp = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerIntent = Intent(this, RadioAnnouncerForegroundService::class.java).apply {
            action = ACTION_TRIGGER_ANNOUNCEMENT
        }
        val pendingTrigger = PendingIntent.getService(
            this,
            1,
            triggerIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val isEnabled = RadioEngineCoordinator.getInstance(this).preferences.config.value.isEnabled
        val statusText = if (isEnabled) "Locutor ACTIVO • Monitoreando hora en punto" else "Locutor MUTED"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("📻 I AM-FM Engine")
            .setContentText(statusText)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingOpenApp)
            .addAction(android.R.drawable.ic_btn_speak_now, "Hablar Ya", pendingTrigger)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "iamfm_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_TRIGGER_ANNOUNCEMENT = "locutor.cristopher.sic.ACTION_TRIGGER_ANNOUNCEMENT"
        const val ACTION_TOGGLE_ANNOUNCER = "locutor.cristopher.sic.ACTION_TOGGLE_ANNOUNCER"

        fun start(context: Context) {
            val intent = Intent(context, RadioAnnouncerForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}

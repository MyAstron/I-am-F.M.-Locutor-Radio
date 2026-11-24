package locutor.cristopher.sic.service

import android.app.Notification
import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MediaMonitorNotificationListener : NotificationListenerService() {

    private var sessionManager: MediaSessionManager? = null

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        controllers?.let {
            RadioEngineCoordinator.getInstance(this).mediaRepository.updateActiveSessions(it)
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d("MediaMonitorListener", "NotificationListenerService conectado con éxito.")
        try {
            sessionManager = getSystemService(MEDIA_SESSION_SERVICE) as? MediaSessionManager
            val component = ComponentName(this, MediaMonitorNotificationListener::class.java)

            sessionManager?.let { mgr ->
                mgr.addOnActiveSessionsChangedListener(sessionListener, component)
                val initialControllers = mgr.getActiveSessions(component)
                RadioEngineCoordinator.getInstance(this).mediaRepository.updateActiveSessions(initialControllers)
            }
        } catch (e: SecurityException) {
            Log.e("MediaMonitorListener", "Falta permiso de acceso a notificaciones: ${e.message}")
        } catch (e: Exception) {
            Log.e("MediaMonitorListener", "Error al registrar session listener", e)
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        sessionManager?.removeOnActiveSessionsChangedListener(sessionListener)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val notification = sbn.notification ?: return
        val extras = notification.extras ?: return

        val pkg = sbn.packageName ?: return
        if (pkg == packageName) return // Ignorar nuestras propias notificaciones

        // 1. Extraer token de MediaSession si es una notificación multimedia
        val token = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable(Notification.EXTRA_MEDIA_SESSION, MediaSession.Token::class.java)
            } else {
                @Suppress("DEPRECATION")
                extras.getParcelable(Notification.EXTRA_MEDIA_SESSION)
            }
        } catch (e: Exception) {
            null
        }

        var controller: MediaController? = null
        if (token != null) {
            try {
                controller = MediaController(this, token)
            } catch (e: Exception) {
                Log.w("MediaMonitorListener", "No se pudo instanciar MediaController desde token de $pkg", e)
            }
        }

        // 2. Extraer metadatos visibles desde los extras de la notificación
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()

        val isMediaNotification = token != null ||
                notification.category == Notification.CATEGORY_TRANSPORT ||
                extras.containsKey(Notification.EXTRA_MEDIA_SESSION)

        if (isMediaNotification && !title.isNullOrBlank()) {
            val largeIconBitmap: Bitmap? = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable(Notification.EXTRA_LARGE_ICON, Bitmap::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    extras.getParcelable(Notification.EXTRA_LARGE_ICON) as? Bitmap
                }
            } catch (_: Exception) {
                try {
                    val icon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras.getParcelable(Notification.EXTRA_LARGE_ICON, Icon::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        extras.getParcelable(Notification.EXTRA_LARGE_ICON) as? Icon
                    }
                    val drawable = icon?.loadDrawable(this)
                    (drawable as? BitmapDrawable)?.bitmap
                } catch (_: Exception) {
                    null
                }
            }

            RadioEngineCoordinator.getInstance(this).mediaRepository.updateFromNotification(
                packageName = pkg,
                title = title,
                artist = text ?: "",
                album = subText ?: "",
                artworkBitmap = largeIconBitmap,
                controller = controller
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (sbn == null) return
        RadioEngineCoordinator.getInstance(this).mediaRepository.onNotificationRemoved(sbn.packageName)
    }
}

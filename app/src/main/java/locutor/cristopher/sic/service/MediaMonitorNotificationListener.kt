package locutor.cristopher.sic.service

import android.content.ComponentName
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
        Log.d("MediaMonitorListener", "NotificationListenerService conectado.")
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
        // Monitoreo pasivo
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Monitoreo pasivo
    }
}

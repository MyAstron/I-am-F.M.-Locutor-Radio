package locutor.cristopher.sic

import android.app.Application
import locutor.cristopher.sic.service.RadioAnnouncerForegroundService
import locutor.cristopher.sic.service.RadioEngineCoordinator

class LocutorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializar el coordinador central del motor de radio
        RadioEngineCoordinator.getInstance(this)
        // Iniciar el servicio en primer plano persistente
        RadioAnnouncerForegroundService.start(this)
    }
}

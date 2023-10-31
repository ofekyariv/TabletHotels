package ofek.yariv.tablethotels

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import ofek.yariv.tablethotels.di.modules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TabletHotelsApp : Application() {
    //private val billingManager: BillingManager by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TabletHotelsApp)
            modules(modules)
        }

        FirebaseAuth.getInstance().signInAnonymously()

        FirebaseRemoteConfig.getInstance().apply {
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate()
        }

        //billingManager.setupBillingClient()

    }
}
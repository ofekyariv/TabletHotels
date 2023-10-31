package ofek.yariv.tablethotels.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import ofek.yariv.tablethotels.utils.SharedPreferencesKeys.SHARED_PREFERENCES_KEY
import ofek.yariv.tablethotels.R
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        get<Context>().getSharedPreferences(
            SHARED_PREFERENCES_KEY,
            0
        )
    }

    factory {
        FirebaseRemoteConfig.getInstance().apply {
            setDefaultsAsync(R.xml.remote_config_defaults)
            fetchAndActivate()
        }
    }
}
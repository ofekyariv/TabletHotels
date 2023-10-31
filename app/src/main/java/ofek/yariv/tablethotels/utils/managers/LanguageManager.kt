package ofek.yariv.tablethotels.utils.managers

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.ActivityCompat.recreate
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.utils.Constants.DEFAULT_LANGUAGE
import ofek.yariv.tablethotels.utils.Constants.ENGLISH
import ofek.yariv.tablethotels.utils.Constants.SPANISH
import ofek.yariv.tablethotels.utils.ReportConstants.CLICKED
import ofek.yariv.tablethotels.utils.ReportConstants.LANGUAGE_CHANGE_TO
import ofek.yariv.tablethotels.utils.SharedPreferencesKeys.LANGUAGE
import java.util.*

class LanguageManager(
    private val context: Context,
    private val activity: Activity,
    private val analyticsManager: AnalyticsManager,
    private val sharedPreferences: SharedPreferences,
) {

    private var currentLanguage: String = DEFAULT_LANGUAGE
    private val editor = sharedPreferences.edit()

    fun changeLanguage() {
        val newLanguage =
            when (currentLanguage) {
                SPANISH -> ENGLISH
                ENGLISH -> SPANISH
                else -> DEFAULT_LANGUAGE
            }
        editor.putString(LANGUAGE, newLanguage)
        editor.apply()
        setLanguage(newLanguage)
        analyticsManager.report(
            "$LANGUAGE_CHANGE_TO ${context.getString(R.string.changeLanguage)}",
            CLICKED
        )
        recreate(activity)
    }

    fun setSavedLanguageOrDefault() {
        currentLanguage = sharedPreferences.getString(
            LANGUAGE,
            DEFAULT_LANGUAGE
        ) ?: DEFAULT_LANGUAGE
        setLanguage(currentLanguage)
    }

    @Suppress("DEPRECATION")
    private fun setLanguage(language: String) {
        val config = context.resources.configuration
        config.setLocale(Locale(language))
        activity.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    fun getCurrentLanguage(): String {
        return currentLanguage
    }
}
package ofek.yariv.tablethotels.utils.managers

import android.content.SharedPreferences
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.utils.SharedPreferencesKeys.THEME

class ThemeManager(
    private val activity: AppCompatActivity,
    private val sharedPreferences: SharedPreferences,
) {

    fun showThemeDialog() {
        val themes = arrayOf(
            activity.getString(R.string.light),
            activity.getString(R.string.dark),
            activity.getString(R.string.system_default)
        )

        val checkedItem = when (getSavedTheme()) {
            AppCompatDelegate.MODE_NIGHT_NO -> 0
            AppCompatDelegate.MODE_NIGHT_YES -> 1
            else -> 2
        }

        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.choose_theme))
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                val selectedTheme = when (which) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                saveTheme(selectedTheme)
                AppCompatDelegate.setDefaultNightMode(selectedTheme)
                activity.recreate()
                dialog.dismiss()
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun saveTheme(theme: Int) = sharedPreferences.edit().putInt(THEME, theme).apply()

    fun getSavedTheme() =
        sharedPreferences.getInt(THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}

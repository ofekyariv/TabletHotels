package ofek.yariv.tablethotels.view.activities.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.utils.Constants
import ofek.yariv.tablethotels.utils.ReportConstants.CHANGE_THEME
import ofek.yariv.tablethotels.utils.ReportConstants.CLICKED
import ofek.yariv.tablethotels.utils.ReportConstants.CONTACT_US
import ofek.yariv.tablethotels.utils.ReportConstants.RATE_THE_APP
import ofek.yariv.tablethotels.utils.ReportConstants.SHARE_THE_APP
import ofek.yariv.tablethotels.utils.managers.AnalyticsManager
import ofek.yariv.tablethotels.utils.managers.LanguageManager
import ofek.yariv.tablethotels.utils.managers.ThemeManager
import ofek.yariv.tablethotels.view.activities.termsAndPrivacy.TermsAndPrivacy
import ofek.yariv.tablethotels.view.activities.termsAndPrivacy.TermsAndPrivacyActivity

class MenuItemClickHelper(
    private val context: Context,
    private val activity: AppCompatActivity,
    private val languageManager: LanguageManager,
    private val analyticsManager: AnalyticsManager,
    private val themeManager: ThemeManager,
) {

    private fun shareTheApp() {
        analyticsManager.report(SHARE_THE_APP, CLICKED)
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=ofek.yariv.tablethotels"
        )
        sendIntent.type = "text/plain"
        activity.startActivity(Intent.createChooser(sendIntent, "Share Using"))
    }

    private fun rateTheApp() {
        analyticsManager.report(RATE_THE_APP, CLICKED)
        try {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=ofek.yariv.tablethotels")
                )
            )
        } catch (_: Exception) {
        }
    }

    private fun contactUs() {
        analyticsManager.report(CONTACT_US, CLICKED)
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:Ofek@gmail.com")
        }
        activity.startActivity(
            Intent.createChooser(
                emailIntent,
                context.getString(R.string.contact_us)
            )
        )
    }

    private fun changeTheme() {
        analyticsManager.report(CHANGE_THEME, CLICKED)
        themeManager.showThemeDialog()
    }

    fun menuItemClickById(id: Int): Boolean {
        when (id) {
            R.id.shareItem -> {
                shareTheApp()
            }

            R.id.rateItem -> {
                rateTheApp()
            }

            R.id.changeLanguageItem -> {
                languageManager.changeLanguage()
            }

            R.id.contactUsItem -> {
                contactUs()
            }

            R.id.termsItem -> {
                activity.startActivity(
                    Intent(
                        activity,
                        TermsAndPrivacyActivity::class.java
                    ).putExtra(Constants.TERMS_OR_PRIVACY, TermsAndPrivacy.TERMS.value)
                )
            }

            R.id.privacyItem -> {
                activity.startActivity(
                    Intent(
                        activity,
                        TermsAndPrivacyActivity::class.java
                    ).putExtra(Constants.TERMS_OR_PRIVACY, TermsAndPrivacy.PRIVACY.value)
                )
            }

            R.id.changeThemeItem -> {
                changeTheme()
            }
        }
        return true
    }
}
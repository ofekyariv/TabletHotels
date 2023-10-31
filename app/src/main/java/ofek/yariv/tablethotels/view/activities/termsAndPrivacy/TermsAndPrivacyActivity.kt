package ofek.yariv.tablethotels.view.activities.termsAndPrivacy

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.databinding.ActivityTermsAndPrivacyBinding
import ofek.yariv.tablethotels.utils.Constants.PRIVACY_POLICY_PATH
import ofek.yariv.tablethotels.utils.Constants.TERMS_AND_CONDITIONS_PATH
import ofek.yariv.tablethotels.utils.Constants.TERMS_OR_PRIVACY
import ofek.yariv.tablethotels.utils.ReportConstants
import ofek.yariv.tablethotels.utils.SharedPreferencesKeys.DO_NOT_SHOW_AGAIN_TERMS
import ofek.yariv.tablethotels.utils.managers.AnalyticsManager
import org.koin.android.ext.android.inject

class TermsAndPrivacyActivity : AppCompatActivity() {
    private val analyticsManager: AnalyticsManager by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private val editor = sharedPreferences.edit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityTermsAndPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val termsOrPrivacy = intent.getStringExtra(TERMS_OR_PRIVACY)

        if (termsOrPrivacy == TermsAndPrivacy.TERMS.value) {
            binding.webView.loadUrl(TERMS_AND_CONDITIONS_PATH)
            analyticsManager.report(
                "${ReportConstants.TERMS_AND_CONDITIONS} ${ReportConstants.SHOWN}",
                ReportConstants.SHOWN
            )
        } else {
            binding.webView.loadUrl(PRIVACY_POLICY_PATH)
            analyticsManager.report(
                "${ReportConstants.PRIVACY_POLICY} ${ReportConstants.SHOWN}",
                ReportConstants.SHOWN
            )
        }

        binding.btnClose.setOnClickListener {
            if (!sharedPreferences.getBoolean(
                    DO_NOT_SHOW_AGAIN_TERMS,
                    false
                ) && (termsOrPrivacy == TermsAndPrivacy.TERMS.value)
            ) {
                MaterialAlertDialogBuilder(this).setTitle(R.string.terms_and_conditions)
                    .setMessage(R.string.terms_and_condition_read_needed)
                    .setPositiveButton(R.string.i_have_read_and_accept_terms) { dialog, _ ->
                        editor.putBoolean(DO_NOT_SHOW_AGAIN_TERMS, true)
                        editor.apply()
                        dialog.dismiss()
                        finish()
                    }.show()
            } else {
                finish()
            }
        }
    }
}

enum class TermsAndPrivacy(val value: String) {
    TERMS("terms"), PRIVACY("privacy")
}
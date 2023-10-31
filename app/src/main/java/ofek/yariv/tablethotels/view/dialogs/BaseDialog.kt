package ofek.yariv.tablethotels.view.dialogs

import android.app.Dialog
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.utils.ReportConstants
import ofek.yariv.tablethotels.utils.managers.AnalyticsManager

abstract class BaseDialog(
    activity: AppCompatActivity,
    private val analyticsManager: AnalyticsManager,
) : Dialog(activity) {

    override fun show() {
        super.show()
        reportDialogShown()
    }

    override fun onStart() {
        super.onStart()
        window?.attributes?.windowAnimations = R.style.DialogAnimation
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    abstract fun getDialogName(): String

    private fun reportDialogShown() {
        analyticsManager.report(
            "${getDialogName()} ${ReportConstants.DIALOG_SHOWN}",
            ReportConstants.SHOWN
        )
    }
}
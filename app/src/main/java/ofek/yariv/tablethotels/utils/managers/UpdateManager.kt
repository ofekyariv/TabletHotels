package ofek.yariv.tablethotels.utils.managers

import android.app.Activity
import android.content.IntentSender
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import ofek.yariv.tablethotels.utils.Constants
import ofek.yariv.tablethotels.utils.ReportConstants.UPDATE
import ofek.yariv.tablethotels.utils.ReportConstants.UPDATE_STARTED

@Suppress("DEPRECATION")
class UpdateManager(
    private val activity: Activity,
    private val analyticsManager: AnalyticsManager,
) {
    fun checkForUpdates() {
        val appUpdateManager = AppUpdateManagerFactory.create(activity)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    analyticsManager.report(UPDATE_STARTED, UPDATE)
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        activity,
                        Constants.START_UPDATE_FLOW_RESULT_CODE
                    )
                } catch (_: IntentSender.SendIntentException) {
                }
            }
        }
    }
}
package ofek.yariv.tablethotels.view.fragments

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import ofek.yariv.tablethotels.utils.ReportConstants
import ofek.yariv.tablethotels.utils.managers.AnalyticsManager
import ofek.yariv.tablethotels.view.activities.main.MainActivity
import org.koin.android.ext.android.inject

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    val analyticsManager: AnalyticsManager by inject()

    override fun onResume() {
        super.onResume()
        reportScreenShown()
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setBottomNavigationItemChecked(getFragmentItemId())
    }

    abstract fun getFragmentName(): String

    abstract fun getFragmentItemId(): Int

    private fun reportScreenShown() {
        analyticsManager.report(
            "${getFragmentName()} ${ReportConstants.FRAGMENT_SHOWN}",
            ReportConstants.SHOWN
        )
    }
}
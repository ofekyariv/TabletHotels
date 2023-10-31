package ofek.yariv.tablethotels.utils.managers

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import ofek.yariv.tablethotels.utils.Constants.TAG

class AnalyticsManager(context: Context) {
    private val mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    private fun replacerForAnalytics(string: String): String {
        return string
            .replace(Regex("[^A-Za-z0-9 ]"), "")
            .replace(" ", "_")
            .take(38)
    }

    fun report(eventName: String, eventCategory: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_NAME, replacerForAnalytics(eventName))
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, replacerForAnalytics(eventCategory))
        }
        mFirebaseAnalytics.logEvent(replacerForAnalytics(eventName), bundle)
        Log.d(TAG, "EventName: $eventName")
    }
}
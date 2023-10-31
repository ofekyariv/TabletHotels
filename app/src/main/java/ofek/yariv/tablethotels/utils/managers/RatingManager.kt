package ofek.yariv.tablethotels.utils.managers

import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager

class RatingManager(
    private val reviewManager: ReviewManager,
    private val activity: AppCompatActivity,
) {

    fun requestInAppReview() {
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                launchReviewFlow(reviewInfo)
            } else {
                // Problem in receiving the ReviewInfo object
                // You can fallback to other methods to collect feedback, e.g., show a dialog, etc.
            }
        }
    }

    private fun launchReviewFlow(reviewInfo: ReviewInfo) {
        val reviewFlow = reviewManager.launchReviewFlow(activity, reviewInfo)
        reviewFlow.addOnCompleteListener {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown.
            // Thus, no matter the result, continue with the app flow.
        }
    }
}
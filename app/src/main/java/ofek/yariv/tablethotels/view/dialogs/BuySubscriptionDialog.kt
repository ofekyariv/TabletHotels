package ofek.yariv.tablethotels.view.dialogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ofek.yariv.tablethotels.R
import ofek.yariv.tablethotels.databinding.DialogBuySubscriptionBinding
import ofek.yariv.tablethotels.utils.ReportConstants
import ofek.yariv.tablethotels.utils.managers.AnalyticsManager
import ofek.yariv.tablethotels.utils.managers.BillingManager

class BuySubscriptionDialog(
    private val activity: AppCompatActivity,
    private val analyticsManager: AnalyticsManager,
    private val billingManager: BillingManager,
) : BaseDialog(activity, analyticsManager) {

    private lateinit var binding: DialogBuySubscriptionBinding
    override fun getDialogName() = ReportConstants.BUY_SUBSCRIPTION_DIALOG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBuySubscriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnSend.setOnClickListener {
//            val startedBillingFlow = billingManager.startBillingFlow(activity)
//            if (startedBillingFlow) {
//                analyticsManager.report(
//                    ReportConstants.STARTED_BILLING_FLOW,
//                    ReportConstants.SUBSCRIPTION
//                )
//            } else {
//                analyticsManager.report(
//                    ReportConstants.FAILED_TO_START_BILLING_FLOW,
//                    ReportConstants.SUBSCRIPTION
//                )
//            }
            dismiss()
        }
    }
}
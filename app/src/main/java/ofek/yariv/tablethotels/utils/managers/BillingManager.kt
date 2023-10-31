package ofek.yariv.tablethotels.utils.managers

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import ofek.yariv.tablethotels.utils.ReportConstants.PURCHASED_A_SUBSCRIPTION_SUCCESSFULLY
import ofek.yariv.tablethotels.utils.ReportConstants.SUBSCRIPTION
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingManager(
    private val context: Context,
    private val analyticsManager: AnalyticsManager,
) {
    private lateinit var monthlySubscriptionProductDetails: ProductDetails
    private var billingClient: BillingClient? = null

    private val _isSubscribed = MutableStateFlow(false)
    val isSubscribed = _isSubscribed.asStateFlow()

    fun setupBillingClient() {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                handlePurchases(purchases)
            }
        }
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryMonthlySubscriptionProductDetails()
                    checkSubscription()
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun queryMonthlySubscriptionProductDetails() {
        val queryProductDetailsParams = QueryProductDetailsParams
            .newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID_MONTHLY_SUBSCRIPTION)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient?.queryProductDetailsAsync(
            queryProductDetailsParams
        ) { _, productDetailsList ->
            monthlySubscriptionProductDetails = productDetailsList[0]
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                    analyticsManager.report(
                        PURCHASED_A_SUBSCRIPTION_SUCCESSFULLY,
                        SUBSCRIPTION
                    )
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(
            acknowledgePurchaseParams
        ) {
            _isSubscribed.value = true
        }
    }

    private fun checkSubscription() {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { _: BillingResult?, purchases: List<Purchase?> ->
            _isSubscribed.value = purchases.isNotEmpty()
        }
    }

    fun startBillingFlow(activity: Activity): Boolean {
        val offerToken =
            monthlySubscriptionProductDetails.subscriptionOfferDetails?.get(0)?.offerToken

        val productDetailsParams = offerToken?.let {
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(monthlySubscriptionProductDetails)
                .setOfferToken(it)
                .build()
        }

        val productDetailsParamsList = productDetailsParams?.let { ImmutableList.of(it) }

        val billingFlowParams = productDetailsParamsList?.let {
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(it)
                .build()
        }

        return if (billingFlowParams != null) {
            billingClient?.launchBillingFlow(activity, billingFlowParams)
            true
        } else {
            false
        }
    }

    companion object {
        const val PRODUCT_ID_MONTHLY_SUBSCRIPTION = "1"
    }

    fun getPrice(): Int {
        val price =
            monthlySubscriptionProductDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                0
            )?.priceAmountMicros
        return (price?.toInt()?.div(1000000)) ?: 15
    }

}
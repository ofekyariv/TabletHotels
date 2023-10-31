package ofek.yariv.tablethotels.view.dialogs.di

import androidx.appcompat.app.AppCompatActivity
import ofek.yariv.tablethotels.view.dialogs.BuySubscriptionDialog
import org.koin.dsl.module

val dialogsModule = module {
    factory { (activity: AppCompatActivity) ->
        BuySubscriptionDialog(
            activity = activity,
            analyticsManager = get(),
            billingManager = get()
        )
    }
}
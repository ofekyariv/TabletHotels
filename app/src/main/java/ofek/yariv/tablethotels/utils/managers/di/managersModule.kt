package ofek.yariv.tablethotels.utils.managers.di

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewManagerFactory
import ofek.yariv.tablethotels.utils.managers.*
import ofek.yariv.tablethotels.view.adapters.SuggestionAdapter
import org.koin.dsl.module

val managersModule = module {
    single { AnalyticsManager(context = get()) }
    single { BillingManager(context = get(), analyticsManager = get()) }
    factory { (activity: AppCompatActivity) ->
        SuggestionAdapter(
            activity = activity,
            autoCompleteManager = get()
        )
    }
    single {
        AutoCompleteManager(
            repository = get(),
            internetManager = get(),
        )
    }

    single { InternetManager(context = get()) }
    single { (activity: Activity) -> UpdateManager(activity = activity, analyticsManager = get()) }
    single { (activity: Activity) -> SpeechToTextManager(activity = activity) }
    single { (activity: AppCompatActivity) ->
        LanguageManager(
            context = get(),
            activity = activity,
            analyticsManager = get(),
            sharedPreferences = get()
        )
    }
    single { LocationManager(context = get()) }
    single { (activity: AppCompatActivity) -> PermissionsManager(activity = activity) }
    single { (activity: AppCompatActivity) ->
        RatingManager(
            reviewManager = ReviewManagerFactory.create(
                get()
            ), activity = activity
        )
    }
    single { (activity: AppCompatActivity) ->
        ThemeManager(
            activity = activity,
            sharedPreferences = get(),
        )
    }
}
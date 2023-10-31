package ofek.yariv.tablethotels.view.activities.main.di

import androidx.appcompat.app.AppCompatActivity
import ofek.yariv.tablethotels.view.activities.main.MainActivityViewModel
import ofek.yariv.tablethotels.view.activities.main.MenuItemClickHelper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val mainActivityModule = module {
    viewModel { MainActivityViewModel() }
    single { (activity: AppCompatActivity) ->
        MenuItemClickHelper(
            context = get(),
            activity = activity,
            languageManager = get { parametersOf(activity) },
            analyticsManager = get(),
            themeManager = get(),
        )
    }
}
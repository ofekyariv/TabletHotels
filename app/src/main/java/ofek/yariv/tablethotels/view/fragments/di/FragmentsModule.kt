package ofek.yariv.tablethotels.view.fragments.di

import ofek.yariv.tablethotels.view.fragments.care_plan.CarePlanViewModel
import ofek.yariv.tablethotels.view.fragments.hotels.HotelsViewModel
import ofek.yariv.tablethotels.view.fragments.home.HomeFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val fragmentModule = module {
    viewModel { CarePlanViewModel() }

    viewModel { HotelsViewModel() }

    viewModel { HomeFragmentViewModel() }
}
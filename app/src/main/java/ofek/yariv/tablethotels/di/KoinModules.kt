package ofek.yariv.tablethotels.di

import ofek.yariv.tablethotels.model.network.di.networkModule
import ofek.yariv.tablethotels.utils.managers.di.managersModule
import ofek.yariv.tablethotels.view.activities.main.di.mainActivityModule
import ofek.yariv.tablethotels.view.dialogs.di.dialogsModule
import ofek.yariv.tablethotels.view.fragments.di.fragmentModule
import ofek.yariv.tablethotels.di.appModule
import org.koin.dsl.module

val modules = module {
    includes(
        appModule,
        mainActivityModule,
        fragmentModule,
        networkModule,
        managersModule,
        dialogsModule,
    )
}
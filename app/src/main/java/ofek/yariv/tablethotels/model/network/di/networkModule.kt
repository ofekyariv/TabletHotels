package ofek.yariv.tablethotels.model.network.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ofek.yariv.tablethotels.model.network.AutoCompleteService
import ofek.yariv.tablethotels.model.repository.AutoCompleteAPI
import ofek.yariv.tablethotels.model.repository.Repository
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val RETROFIT = "RETROFIT"

private const val BASE_URL = "https://www.tablethotels.com/"

val networkModule = module {
    single { OkHttpClient.Builder().build() }
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    single(named(RETROFIT)) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .build()
    }
    single<AutoCompleteService> {
        get<Retrofit>(named(RETROFIT)).create(AutoCompleteService::class.java)
    }
    single { AutoCompleteAPI(autoCompleteService = get()) }
    single {
        Repository(
            autoCompleteAPI = get(),
        )
    }
}
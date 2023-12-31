package com.drahovac.weatherstationdisplay.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.platformLogWriter
import com.drahovac.weatherstationdisplay.data.CurrentWeatherDataRepositoryImpl
import com.drahovac.weatherstationdisplay.data.Database
import com.drahovac.weatherstationdisplay.data.DeviceCredentialsRepositoryImpl
import com.drahovac.weatherstationdisplay.data.ForecastRepositoryImpl
import com.drahovac.weatherstationdisplay.data.HistoryWeatherDataRepositoryImpl
import com.drahovac.weatherstationdisplay.data.NetworkClient
import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.ForecastRepository
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
import com.drahovac.weatherstationdisplay.usecase.HistoryUseCase
import com.drahovac.weatherstationdisplay.viewmodel.CurrentWeatherViewModel
import com.drahovac.weatherstationdisplay.viewmodel.ForecastViewModel
import com.drahovac.weatherstationdisplay.viewmodel.HistoryDataViewModel
import com.drahovac.weatherstationdisplay.viewmodel.HistoryInitViewModel
import com.drahovac.weatherstationdisplay.viewmodel.InitialDestinationViewModel
import com.drahovac.weatherstationdisplay.viewmodel.SetupAipKeyViewModel
import com.drahovac.weatherstationdisplay.viewmodel.SetupDeviceIdViewModel
import com.rickclephas.kmm.viewmodel.KMMViewModel
import org.koin.core.context.startKoin
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

internal expect val platformModule: Module

internal val sharedModule = module {
    factory { HistoryUseCase(get(), get()) }

    single<DeviceCredentialsRepository> { DeviceCredentialsRepositoryImpl(get()) }

    factory { InitialDestinationViewModel(get()) }

    single { NetworkClient(get()) }

    single<CurrentWeatherDataRepository> { CurrentWeatherDataRepositoryImpl(get()) }

    single<HistoryWeatherDataRepository> { HistoryWeatherDataRepositoryImpl(get()) }

    single<ForecastRepository> { ForecastRepositoryImpl(get()) }

    single { Database(get()) }

    kmmViewModel { SetupDeviceIdViewModel(get()) }

    kmmViewModel { SetupAipKeyViewModel(get()) }

    kmmViewModel { CurrentWeatherViewModel(get(), get()) }

    kmmViewModel { HistoryInitViewModel(get(), get()) }

    kmmViewModel { HistoryDataViewModel(get()) }

    kmmViewModel { ForecastViewModel(get(), get(), get()) }
}

internal expect inline fun <reified T : KMMViewModel> Module.kmmViewModel(
    noinline definition: Definition<T>
): KoinDefinition<T>

fun initApplication(
    isProd: Boolean,
    appDeclaration: KoinAppDeclaration = {}
) {
    Logger.setLogWriters(platformLogWriter())
    Logger.setTag("Weather")
    Logger.setMinSeverity(if (isProd) Severity.Error else Severity.Verbose)
    startKoin {
        appDeclaration()
        modules(platformModule, sharedModule)
    }
}

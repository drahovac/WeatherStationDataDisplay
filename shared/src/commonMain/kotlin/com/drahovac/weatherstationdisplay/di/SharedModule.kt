package com.drahovac.weatherstationdisplay.di

import com.drahovac.weatherstationdisplay.data.DeviceCredentialsRepositoryImpl
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
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
    kmmViewModel { SetupDeviceIdViewModel(get()) }

    kmmViewModel { SetupAipKeyViewModel(get()) }

    single<DeviceCredentialsRepository> { DeviceCredentialsRepositoryImpl(get()) }

    factory { InitialDestinationViewModel(get()) }
}

internal expect inline fun <reified T : KMMViewModel> Module.kmmViewModel(
    noinline definition: Definition<T>
): KoinDefinition<T>

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(platformModule, sharedModule)
    }
}

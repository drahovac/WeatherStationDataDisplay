package com.drahovac.weatherstationdisplay.di

import com.drahovac.weatherstationdisplay.data.DatabaseDriver
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.Settings
import com.russhwolf.settings.KeychainSettings
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.module

actual inline fun <reified T : KMMViewModel> Module.kmmViewModel(
    noinline definition: Definition<T>
): KoinDefinition<T> {
    return factory(definition = definition)
}

@OptIn(ExperimentalSettingsImplementation::class)
actual val platformModule: Module = module {
    single<Settings> {
        KeychainSettings(
            service = "MyEncryptedSettings"
        )
    }

    single { DatabaseDriver() }
}

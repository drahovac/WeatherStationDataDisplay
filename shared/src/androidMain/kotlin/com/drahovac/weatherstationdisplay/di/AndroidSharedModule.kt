package com.drahovac.weatherstationdisplay.di

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKey.Builder
import com.drahovac.weatherstationdisplay.data.DatabaseDriver
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.module

actual inline fun <reified T : KMMViewModel> Module.kmmViewModel(
    noinline definition: Definition<T>
): KoinDefinition<T> {
    return viewModel(definition = definition)
}

actual val platformModule: Module = module {
    single<Settings> {
        val masterKey: MasterKey = Builder(androidContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        SharedPreferencesSettings(
            EncryptedSharedPreferences.create(
                androidContext(),
                "MyEncryptedSettings",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            ),
            true
        )
    }

    single {
        DatabaseDriver(androidContext())
    }
}

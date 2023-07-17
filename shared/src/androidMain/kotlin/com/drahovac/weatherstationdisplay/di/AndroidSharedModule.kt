package com.drahovac.weatherstationdisplay.di

import com.rickclephas.kmm.viewmodel.KMMViewModel
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
    single {
        //  DatabaseDriver(androidContext())
    }
}

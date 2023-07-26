package com.drahovac.weatherstationdisplay.viewmodel

import dev.icerock.moko.resources.StringResource

data class SetupState(
    val value: String? = null,
    val error: StringResource? = null,
)

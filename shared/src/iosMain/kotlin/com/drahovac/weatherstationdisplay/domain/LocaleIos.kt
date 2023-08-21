package com.drahovac.weatherstationdisplay.domain

import platform.Foundation.NSLocale
import platform.Foundation.languageCode
import platform.Foundation.systemLocale

actual fun Locale.language(): String {
    return NSLocale.systemLocale.languageCode
}
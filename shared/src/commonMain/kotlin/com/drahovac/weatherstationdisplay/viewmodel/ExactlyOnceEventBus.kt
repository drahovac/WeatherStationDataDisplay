package com.drahovac.weatherstationdisplay.viewmodel

class ExactlyOnceEventBus<T>(event: T? = null) {
    private val buffer = event?.let { ArrayDeque<T>(listOf(it)) } ?: ArrayDeque()

    fun receive(): T? = buffer.removeFirstOrNull()
}

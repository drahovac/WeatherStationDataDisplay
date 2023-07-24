package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DeviceCredentialsRepositoryImpl(
    private val settings: Settings
) : DeviceCredentialsRepository {


    override suspend fun getDeviceId(): String? {
        return withContext(Dispatchers.IO) {
            settings.getStringOrNull(DEVICE_ID_KEY)
        }
    }

    override suspend fun getApiKey(): String? {
        return withContext(Dispatchers.IO) {
            settings.getStringOrNull(API_KEY)
        }
    }

    override suspend fun saveDeviceId(id: String) {
        withContext(Dispatchers.IO) {
            settings[DEVICE_ID_KEY] = id
        }
    }

    override suspend fun saveApiKey(key: String) {
        withContext(Dispatchers.IO) {
            settings[API_KEY] = key
        }
    }

    override suspend fun removeApiKey() {
        withContext(Dispatchers.IO) {
            settings[API_KEY] = null
        }
    }

    override suspend fun removeDeviceId() {
        withContext(Dispatchers.IO) {
            settings[DEVICE_ID_KEY] = null
        }
    }

    private companion object {
        const val API_KEY = "API_KEY"
        const val DEVICE_ID_KEY = "DEVICE_ID_KEY"
    }
}

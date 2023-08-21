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
        return getValue(DEVICE_ID_KEY)
    }

    override suspend fun getApiKey(): String? {
        return getValue(API_KEY)
    }

    override suspend fun saveDeviceId(id: String) {
        saveValue(DEVICE_ID_KEY, id)
    }

    override suspend fun saveApiKey(key: String) {
        saveValue(API_KEY, key)
    }

    override suspend fun removeApiKey() {
        saveValue(API_KEY, null)
    }

    override suspend fun removeDeviceId() {
        saveValue(DEVICE_ID_KEY, null)
        saveValue(DEVICE_CODE, null) // delete code as well
    }

    override suspend fun saveStationCode(code: String) {
        saveValue(getDevicCodeKey(), code)
    }

    private suspend fun DeviceCredentialsRepositoryImpl.getDevicCodeKey(): String {
        return getDeviceId() + DEVICE_CODE
    }

    override suspend fun getStationCode(): String? {
        return getValue(getDevicCodeKey())
    }

    private suspend fun saveValue(key: String, value: String?) {
        withContext(Dispatchers.IO) {
            settings[key] = value
        }
    }

    private suspend fun getValue(key: String): String? {
        return withContext(Dispatchers.IO) {
            settings.getStringOrNull(key)
        }
    }

    private companion object {
        const val API_KEY = "API_KEY"
        const val DEVICE_ID_KEY = "DEVICE_ID_KEY"
        const val DEVICE_CODE = "DEVICE_CODE"
    }
}

package com.drahovac.weatherstationdisplay.domain

interface DeviceCredentialsRepository {
    suspend fun getDeviceId(): String?
    suspend fun getApiKey(): String?
    suspend fun saveDeviceId(id: String)
    suspend fun saveApiKey(key: String)
}

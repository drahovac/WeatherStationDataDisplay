package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.NetworkError
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val metadata: Metadata,
    val success: Boolean,
    val errors: List<Error>
) {
    fun getNetworkError(): NetworkError {
        val message = errors.joinToString("\n") { it.error.message }

        return errors.firstOrNull()?.let {
            when (it.error.code) {
                "CDN-0001" -> NetworkError.InvalidApiKey
                "TODO" -> NetworkError.TooManyRequests
                else -> NetworkError.General(Throwable(message))
            }
        } ?: NetworkError.General(Throwable(message))
    }
}

@Serializable
data class Metadata(
    val transactionId: String? = null
)

@Serializable
data class Error(
    val error: ErrorDetails
)

@Serializable
data class ErrorDetails(
    val code: String,
    override val message: String
) : Throwable()

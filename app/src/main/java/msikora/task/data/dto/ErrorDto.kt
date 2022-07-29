package msikora.task.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val field: String? = null,
    val message: String? = null,
)
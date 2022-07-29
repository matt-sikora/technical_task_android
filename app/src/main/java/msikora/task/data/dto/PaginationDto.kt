package msikora.task.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginationDto(
    val page: Int?,
    val pages: Int?,
)
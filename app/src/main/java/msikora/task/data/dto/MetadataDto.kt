package msikora.task.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MetadataDto(
    val pagination: PaginationDto?
)
package msikora.task.core

import msikora.task.data.dto.ErrorDto

data class ApiError(
    val errors: List<ErrorDto>
) : Throwable()
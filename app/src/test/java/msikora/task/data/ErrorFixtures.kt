package msikora.task.data

import msikora.task.data.dto.ErrorDto

object ErrorFixtures {

    val emailError = ErrorDto(
        field = "email",
        message = "is invalid"
    )
}
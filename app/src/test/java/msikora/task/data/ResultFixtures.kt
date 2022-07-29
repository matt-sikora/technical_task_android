package msikora.task.data

import msikora.task.core.ApiError
import msikora.task.core.CallState
import msikora.task.core.NetworkError
import msikora.task.data.dto.ErrorDto

object ResultFixtures {

    val networkError = listOf(CallState.Loading, CallState.Error(NetworkError()))
    val successList = listOf(CallState.Loading, CallState.Success(Unit))
    val authenticationFailed = CallState.Error(
        ApiError(
            listOf(
                ErrorDto(message = "Authentication failed")
            )
        )
    )

    fun error(errors: List<ErrorDto>) = listOf(CallState.Loading, CallState.Error(ApiError(errors)))
}
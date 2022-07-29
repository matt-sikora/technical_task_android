package msikora.task.data

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import msikora.task.core.ApiError
import msikora.task.core.CallState
import msikora.task.core.NetworkError
import msikora.task.data.dto.ApiErrorResponse

fun <ApiModel, EmittedModel> networkCallAsResultFlow(
    networkCall: suspend () -> NetworkResponse<ApiModel, ApiErrorResponse>,
    mapping: suspend (ApiModel) -> EmittedModel,
): Flow<CallState<EmittedModel>> {
    return flow {
        emit(CallState.Loading)
        val response = networkCall()
        emit(
            when (response) {
                is NetworkResponse.Success -> CallState.Success(mapping(response.body))
                is NetworkResponse.ServerError -> {
                    val errors = response.body?.data
                    if (errors.isNullOrEmpty()) {
                        CallState.Error()
                    } else {
                        CallState.Error(ApiError(errors))
                    }
                }
                is NetworkResponse.NetworkError -> CallState.Error(NetworkError())
                is NetworkResponse.UnknownError -> CallState.Error(response.error)
            }
        )
    }
}

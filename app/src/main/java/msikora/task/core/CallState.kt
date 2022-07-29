package msikora.task.core

sealed interface CallState<out T> {
    data class Success<T>(val data: T) : CallState<T>
    data class Error(val exception: Throwable? = null) : CallState<Nothing>
    object Loading : CallState<Nothing>
}

fun CallState.Error.extractSingleMessage(): String {
    return when (exception) {
        is ApiError -> {
            exception.message ?: "Something went wrong"
        }
        is NetworkError -> {
            "Check your network connection and try again"
        }
        else -> {
            "Something went wrong"
        }
    }
}
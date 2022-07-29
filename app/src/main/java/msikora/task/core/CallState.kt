package msikora.task.core

sealed interface CallState<out T> {
    data class Success<T>(val data: T) : CallState<T>
    data class Error(val exception: Throwable? = null) : CallState<Nothing>
    object Loading : CallState<Nothing>
}

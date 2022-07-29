@file:OptIn(ExperimentalCoroutinesApi::class)

package msikora.task.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import msikora.task.core.CallState
import msikora.task.data.UsersRepository
import msikora.task.domain.User
import javax.inject.Inject

@HiltViewModel
class UserListViewModel
@Inject constructor(
    private val repo: UsersRepository
) : ViewModel() {

    val users = repo.users

    private val fetchingInput = MutableStateFlow(0)
    val fetchingState: Flow<CallState<Unit>> = fetchingInput.flatMapLatest {
        repo.fetchUsers()
    }.shareIn(
        viewModelScope,
        replay = 1,
        started = SharingStarted.Lazily,
    )

    private val _messages = Channel<String>(Channel.CONFLATED)
    val messages = _messages.receiveAsFlow()

    fun delete(user: User) {
        viewModelScope.launch {
            repo.deleteUser(user.id).collect { callState ->
                if (callState is CallState.Error) {
                    _messages.send("Something went wrong :(")
                } else if (callState is CallState.Success) {
                    _messages.send("${user.name} deleted")
                }
            }
        }
    }

    fun retryFetching() {
        fetchingInput.update { it + 1 }
    }
}
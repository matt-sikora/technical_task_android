package msikora.task.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import msikora.task.core.CallState
import msikora.task.domain.NewUser
import msikora.task.domain.User

interface UsersRepository {
    val users: StateFlow<List<User>>
    fun createUser(user: NewUser): Flow<CallState<Unit>>
    fun deleteUser(userId: Int): Flow<CallState<Unit>>
    fun fetchUsers(): Flow<CallState<Unit>>
}
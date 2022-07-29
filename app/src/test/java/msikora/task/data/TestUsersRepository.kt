package msikora.task.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import msikora.task.NewUser
import msikora.task.domain.User
import msikora.task.core.CallState

class TestUsersRepository(override val users: MutableStateFlow<List<User>>) : UsersRepository {

    var fetchUsersCalled = 0

    override fun createUser(user: NewUser): Flow<CallState<Unit>> {
        return flow { ResultFixtures.successList }
    }

    override fun deleteUser(userId: Int): Flow<CallState<Unit>> {
        return flow { ResultFixtures.successList }
    }

    override fun fetchUsers(): Flow<CallState<Unit>> {
        fetchUsersCalled++
        return flow { ResultFixtures.successList }
    }
}
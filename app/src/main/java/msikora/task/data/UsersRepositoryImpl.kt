@file:OptIn(ExperimentalCoroutinesApi::class)

package msikora.task.data

import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import msikora.task.NewUser
import msikora.task.domain.User
import msikora.task.core.ApiError
import msikora.task.core.CallState
import msikora.task.data.dto.ErrorDto
import msikora.task.data.dto.NewUserDto
import msikora.task.data.dto.UserListResponse
import msikora.task.data.service.UsersService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersRepositoryImpl
@Inject constructor(
    private val usersService: UsersService
) : UsersRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users

    override fun createUser(user: NewUser): Flow<CallState<Unit>> {
        return networkCallAsResultFlow(
            networkCall = { usersService.createUser(NewUserDto.fromDomain(user)) },
            mapping = { newUserInAList ->
                _users.update { it + newUserInAList.toDomainUsers() }
            }
        )
    }

    override fun deleteUser(userId: Int): Flow<CallState<Unit>> {
        return networkCallAsResultFlow(
            networkCall = { usersService.deleteUser(userId) },
            mapping = {
                removeFromState(userId)
            }
        ).mapLatest { latest ->
            if (latest == CallState.Error(ApiError(listOf(ErrorDto(message = "Resource not found"))))) {
                removeFromState(userId)
                CallState.Success(Unit)
            } else {
                latest
            }
        }
    }

    override fun fetchUsers(): Flow<CallState<Unit>> {
        return networkCallAsResultFlow(
            networkCall = {
                val justForPageNumber = usersService.getUsers(Int.MAX_VALUE)
                if (justForPageNumber is NetworkResponse.Success) {
                    val pages = justForPageNumber.body.meta?.pagination?.pages
                    if (pages == null) {
                        usersService.getUsers()
                    } else {
                        usersService.getUsers(pages - 1)
                    }
                } else {
                    justForPageNumber
                }
            },
            mapping = { userListResponse ->
                _users.update { userListResponse.toDomainUsers() }
            }
        )
    }

    private fun removeFromState(userId: Int) {
        _users.update { it.filter { user -> user.id != userId } }
    }

    private fun UserListResponse.toDomainUsers() = data?.mapNotNull { it.toDomain() } ?: emptyList()
}
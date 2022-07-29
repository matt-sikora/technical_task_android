@file:OptIn(ExperimentalCoroutinesApi::class)

package msikora.task.ui.list

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import msikora.task.common.MainDispatcherRule
import msikora.task.core.CallState
import msikora.task.data.ResultFixtures
import msikora.task.data.UserFixtures
import msikora.task.data.UsersRepository
import msikora.task.domain.User
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UserListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun fetching() = runTest {
        val repo = mockk<UsersRepository>()
        val users = MutableStateFlow<List<User>>(emptyList())
        every { repo.users } returns users
        every { repo.fetchUsers() } returnsMany listOf(
            ResultFixtures.networkError,
            ResultFixtures.successList,
        ).map { it.asFlow() }

        val viewModel = UserListViewModel(repo)
        val fetchingStates = mutableListOf<CallState<Unit>>()
        val collectFetchingState = launch(UnconfinedTestDispatcher()) {
            viewModel.fetchingState.toList(fetchingStates)
        }
        val collectedUsers = mutableListOf<List<User>>()
        val collectUsers = launch(UnconfinedTestDispatcher()) {
            viewModel.users.toList(collectedUsers)
        }

        assertEquals(ResultFixtures.networkError, fetchingStates)
        viewModel.retryFetching()
        users.update { listOf(UserFixtures.alice()) }
        assertEquals(ResultFixtures.networkError + ResultFixtures.successList, fetchingStates)
        assertEquals(listOf(emptyList(), listOf(UserFixtures.alice())), collectedUsers)

        collectFetchingState.cancel()
        collectUsers.cancel()
    }
}
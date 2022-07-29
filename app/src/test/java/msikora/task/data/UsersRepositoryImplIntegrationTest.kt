@file:OptIn(ExperimentalCoroutinesApi::class)

package msikora.task.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import msikora.task.BuildConfig
import msikora.task.core.CallState
import msikora.task.data.service.UsersService
import msikora.task.di.NetworkModule.okHttp
import msikora.task.di.NetworkModule.retrofit
import msikora.task.di.NetworkModule.usersService
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// in an ideal world it would be run in separate test suite
// in an ideal world it would be unit test working against fixtures that are validated by integration tests
class UsersRepositoryImplIntegrationTest {

    @Test
    fun `create valid user`() = runTest {
        val repo = UsersRepositoryImpl(buildUserService())

        val newUser = UserFixtures.newUser()
        val createUserResult = repo.createUser(newUser).toList()
        assertEquals(
            ResultFixtures.successList,
            createUserResult
        )
        val users = repo.users.value
        assertEquals(1, users.size)
        with(users[0]) {
            assertEquals(newUser.email, email)
            assertEquals(newUser.gender, gender)
            assertEquals(newUser.name, name)
            assertTrue(isActive)
            assertTrue(id > 0)
        }
    }

    @Test
    fun `create invalid user`() = runTest {
        val repo = UsersRepositoryImpl(buildUserService())
        val createUserResult = repo.createUser(UserFixtures.invalidEmail()).toList()
        assertEquals(
            ResultFixtures.error(listOf(ErrorFixtures.emailError)),
            createUserResult,
        )
    }

    @Test
    fun `delete without valid token`() = runTest {
        val repo = UsersRepositoryImpl(buildUserService(""))

        val fetchResult = repo.fetchUsers().toList()
        assertEquals(ResultFixtures.successList, fetchResult)
        val deleteMe = repo.users.first().first().id

        val deleteResult = repo.deleteUser(deleteMe).toList()
        assertEquals(
            listOf(CallState.Loading, ResultFixtures.authenticationFailed),
            deleteResult,
        )
    }

    @Test
    fun `404 treated as success when deleting`() = runTest {
        val repo = UsersRepositoryImpl(buildUserService())

        val newUser = UserFixtures.newUser()
        val createUserResult = repo.createUser(newUser).toList()
        assertEquals(
            ResultFixtures.successList,
            createUserResult
        )
        val userIdToDelete = repo.users.value[0].id
        assertEquals(ResultFixtures.successList, repo.deleteUser(userIdToDelete).toList())
        assertEquals(ResultFixtures.successList, repo.deleteUser(userIdToDelete).toList())
    }

    private fun buildUserService(token: String = BuildConfig.TOKEN): UsersService =
        usersService(retrofit(okHttp(
            Token(token),
            HttpLoggingInterceptor {
                println(it)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )))
}
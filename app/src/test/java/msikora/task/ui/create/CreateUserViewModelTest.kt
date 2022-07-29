@file:OptIn(ExperimentalCoroutinesApi::class)

package msikora.task.ui.create

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import msikora.task.common.MainDispatcherRule
import msikora.task.data.ErrorFixtures
import msikora.task.data.ResultFixtures
import msikora.task.data.UserFixtures
import msikora.task.data.UsersRepository
import msikora.task.ui.common.validator.NonBlankValidator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateUserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CreateUserViewModel
    private lateinit var repo: UsersRepository

    @Before
    fun setup() {
        repo = mockk()
        viewModel = CreateUserViewModel(
            repo = repo,
            nonEmptyValidator = NonBlankValidator()
        )
    }

    @Test
    fun `test validation`() {
        viewModel.submit()
        assertEquals("can't be blank", viewModel.emailError.value)
        assertEquals("can't be blank", viewModel.nameError.value)
        assertEquals("", viewModel.genderError.value)
    }

    @Test
    fun `test updating value resets error`() {
        viewModel.submit()
        assertEquals("can't be blank", viewModel.emailError.value)
        assertEquals("can't be blank", viewModel.nameError.value)

        viewModel.updateEmail("alice")
        assertEquals("", viewModel.emailError.value)
        assertEquals("can't be blank", viewModel.nameError.value)

        viewModel.updateName("alice")
        assertEquals("", viewModel.emailError.value)
        assertEquals("", viewModel.nameError.value)
    }

    @Test
    fun `error when submitting`() = runTest {
        val validEmail = "alice@wonderland.com"
        val newUser = UserFixtures.invalidEmail()
        val validUser = newUser.copy(email = "alice@wonderland.com")

        every { repo.createUser(newUser) } returns
                ResultFixtures.error(listOf(ErrorFixtures.emailError)).asFlow()

        every { repo.createUser(validUser) } returns
                ResultFixtures.successList.asFlow()

        viewModel.updateEmail(newUser.email)
        viewModel.updateName(newUser.name)
        viewModel.updateGender(newUser.gender)

        viewModel.submit()

        assertEquals("is invalid", viewModel.emailError.value)
        assertEquals("", viewModel.genderError.value)
        assertEquals("", viewModel.nameError.value)

        viewModel.updateEmail(validEmail)

        var receivedFinishSignal = false
        val collectFinishSignals = launch(UnconfinedTestDispatcher()) {
            viewModel.finishSignal.collect { receivedFinishSignal = true }
        }

        viewModel.submit()
        assertTrue(receivedFinishSignal)
        collectFinishSignals.cancel()
    }

}
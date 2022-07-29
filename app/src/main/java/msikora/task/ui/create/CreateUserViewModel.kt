package msikora.task.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import msikora.task.NewUser
import msikora.task.core.ApiError
import msikora.task.core.CallState
import msikora.task.core.NetworkError
import msikora.task.data.UsersRepository
import msikora.task.domain.Gender
import msikora.task.ui.common.validator.NonBlankValidator
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel
@Inject constructor(
    private val repo: UsersRepository,
    private val nonEmptyValidator: NonBlankValidator,
) : ViewModel() {

    private val _finishSignal = Channel<Any>(Channel.CONFLATED)
    val finishSignal = _finishSignal.receiveAsFlow()

    private val _callInProgress = MutableStateFlow(false)
    val callInProgress: StateFlow<Boolean> = _callInProgress

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name
    private val _nameError = MutableStateFlow("")
    val nameError: StateFlow<String> = _nameError

    private val _gender = MutableStateFlow(DEFAULT_GENDER)
    val gender: StateFlow<Gender> = _gender
    private val _genderError = MutableStateFlow("")
    val genderError: StateFlow<String> = _genderError

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    private val _emailError = MutableStateFlow("")
    val emailError: StateFlow<String> = _emailError

    private val _generalError = MutableStateFlow("")
    val generalError: StateFlow<String> = _generalError

    fun updateName(name: String) {
        _name.value = name
        _nameError.value = ""
    }

    fun updateGender(gender: Gender) {
        _gender.value = gender
        _genderError.value = ""
    }

    fun updateEmail(email: String) {
        _email.value = email
        _emailError.value = ""
    }

    fun submit() {
        val name = getAndValidateField(_name, _nameError)
        val email = getAndValidateField(_email, _emailError)
        val newUser = NewUser(
            email = email ?: return,
            gender = gender.value,
            name = name ?: return,
        )
        viewModelScope.launch {
            repo.createUser(newUser).collect { callState ->
                _callInProgress.value = callState is CallState.Loading
                when (callState) {
                    is CallState.Error -> {
                        when (callState.exception) {
                            is ApiError -> {
                                callState.exception.errors.iterator().forEach { errorDto ->
                                    when (errorDto.field) {
                                        "email" -> _emailError.value = errorDto.message ?: ""
                                        "name" -> _nameError.value = errorDto.message ?: ""
                                        "gender" -> _genderError.value = errorDto.message ?: ""
                                        else -> _generalError.value = errorDto.message ?: ""
                                    }
                                }
                            }
                            is NetworkError -> {
                                _generalError.value = "Check your network connection and try again"
                            }
                            else -> {
                                _generalError.value = "Something went wrong"
                            }
                        }
                    }
                    CallState.Loading -> {
                        // no-op
                    }
                    is CallState.Success -> {
                        _finishSignal.send(Any())
                    }
                }
            }
        }
    }

    private fun getAndValidateField(
        field: MutableStateFlow<String>,
        error: MutableStateFlow<String>
    ): String? {
        val value = field.value
        val errorMessage = nonEmptyValidator.validate(value)
        error.value = errorMessage
        return if (errorMessage.isNotBlank()) {
            null
        } else {
            value
        }
    }

    companion object {
        val DEFAULT_GENDER = Gender.Female
    }
}
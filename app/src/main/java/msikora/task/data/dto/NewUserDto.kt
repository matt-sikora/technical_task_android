package msikora.task.data.dto

import kotlinx.serialization.Serializable
import msikora.task.domain.Gender
import msikora.task.domain.NewUser

@Serializable
data class NewUserDto(
    val email: String?,
    val gender: String?,
    val name: String?,
    val status: String?,
) {

    companion object {

        fun fromDomain(user: NewUser) = with(user) {
            NewUserDto(
                email = email,
                gender = gender.asApiString(),
                name = name,
                status = UserDto.STATUS_ACTIVE,
            )
        }
    }
}

fun Gender.asApiString() = when (this) {
    Gender.Male -> "male"
    Gender.Female -> "female"
}

fun String.asGender() = when (this) {
    "male" -> Gender.Male
    "female" -> Gender.Female
    else -> null
}
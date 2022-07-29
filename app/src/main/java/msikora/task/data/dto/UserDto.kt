package msikora.task.data.dto

import kotlinx.serialization.Serializable
import msikora.task.domain.Gender
import msikora.task.domain.User

@Serializable
data class UserDto(
    val id: Int?,
    val email: String?,
    val gender: String?,
    val name: String?,
    val status: String?
) {
    fun toDomain(): User? {
        return User(
            id = id ?: return null,
            email = email ?: return null,
            gender = gender?.asGender() ?: return null,
            name = name ?: return null,
            isActive = status == STATUS_ACTIVE,
        )
    }

    companion object {

        const val STATUS_ACTIVE = "active"
    }
}
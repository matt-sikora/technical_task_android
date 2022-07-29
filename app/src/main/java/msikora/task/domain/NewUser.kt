package msikora.task.domain

data class NewUser(
    val email: String,
    val gender: Gender,
    val name: String,
)
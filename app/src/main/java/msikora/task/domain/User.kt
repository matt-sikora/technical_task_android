package msikora.task.domain

data class User(
    val id: Int,
    val email: String,
    val gender: Gender,
    val name: String,
    val isActive: Boolean,
)

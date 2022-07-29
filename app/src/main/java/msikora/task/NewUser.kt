package msikora.task

import msikora.task.domain.Gender

data class NewUser(
    val email: String,
    val gender: Gender,
    val name: String,
)
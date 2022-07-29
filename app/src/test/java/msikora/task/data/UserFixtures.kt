package msikora.task.data

import msikora.task.domain.Gender
import msikora.task.domain.NewUser
import msikora.task.domain.User
import java.util.*

object UserFixtures {

    fun newUser(): NewUser {
        val uuid = UUID.randomUUID()
        return NewUser(
            email = "$uuid@gmail.com",
            gender = Gender.Male,
            name = uuid.toString(),
        )
    }

    fun alice() = User(
        id = 1,
        email = "alice@wonderland.com",
        gender = Gender.Female,
        name = "Alice",
        isActive = true,
    )

    fun invalidEmail() = NewUser("foo", Gender.Male, "baz")
}
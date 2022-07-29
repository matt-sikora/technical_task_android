package msikora.task.ui.common.validator

import dagger.Reusable
import javax.inject.Inject

@Reusable
class NonBlankValidator
@Inject constructor() {

    fun validate(candidate: String): String = if (candidate.isBlank()) {
        "can't be blank"
    } else {
        ""
    }
}
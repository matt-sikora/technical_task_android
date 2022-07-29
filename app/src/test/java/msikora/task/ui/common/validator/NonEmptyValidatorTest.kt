package msikora.task.ui.common.validator

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NonEmptyValidatorTest {

    private lateinit var validator: NonBlankValidator

    @Before
    fun setup() {
        validator = NonBlankValidator()
    }

    @Test
    fun `test validating non blank`() {
        assertEquals("", validator.validate("valid"))
    }

    @Test
    fun `test validating empty`() {
        assertEquals("can't be blank", validator.validate(""))
    }

    @Test
    fun `test validating blank`() {
        assertEquals("can't be blank", validator.validate(""   ))
    }
}
package ch.benlu.composeform.validators

import ch.benlu.composeform.Validator

class IsEqualValidator<T>(expectedValue: () -> T, errorText: String? = null) : Validator<T>(
    validate = {
        it == expectedValue()
    },
    errorText = errorText ?: "This field's value is not as expected."
)
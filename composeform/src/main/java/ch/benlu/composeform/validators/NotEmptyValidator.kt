package ch.benlu.composeform.validators

import ch.benlu.composeform.Validator

class NotEmptyValidator<T>(errorText: String? = null) : Validator<T>(
    validate = {
        it != null
    },
    errorText = errorText ?: "This field should not be empty"
)
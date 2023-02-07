package ch.benlu.composeform.validators

import ch.benlu.composeform.Validator
import java.util.*

class DateValidator(minDateTime: () -> Long, errorText: String? = null) : Validator<Date?>(
    validate = {
        (it?.time ?: -1) >= minDateTime()
    },
    errorText = errorText ?: "This field is not valid."
)
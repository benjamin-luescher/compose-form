package ch.benlu.composeform

import android.util.Log
import androidx.compose.runtime.*

abstract class Form {
    var isValid by mutableStateOf(true)

    abstract fun self(): Form

    private fun getFormFields(): List<Pair<String, FormField?>> {
        return this::class.java.declaredFields.map {
            Pair(it.name, it.getAnnotation(FormField::class.java))
        }.filter { it.second != null }
    }

    fun logRawValue() {
        getFormFields().forEach { pair ->
            val name = pair.first
            val f = this::class.java.getDeclaredField(name)
            f.isAccessible = true

            try {
                val fieldState = (f.get(this) as FieldState<Any>)
                val value = fieldState.state?.value
                Log.d("Form", "$name:${value}")
            } catch (e: Exception) {
                Log.e("Form", e.toString())
            }
        }
    }

    /**
     * Triggers validation for all fields in the form.
     */
    fun validate(ignoreUntouched: Boolean = false) {
        var isValid = true
        val formFields = getFormFields()

        formFields.forEach { pair ->
            val name = pair.first
            val f = this::class.java.getDeclaredField(name)
            f.isAccessible = true

            try {
                val fieldState = (f.get(this) as FieldState<Any>)
                val value = fieldState.state?.value
                val validators = fieldState.validators

                println("$name:${value}")
                var isFieldValid = true

                // first clear all error text before validation
                fieldState.errorText.clear()

                validators.forEach {
                    if (!it.validate(value)) {
                        isValid = false
                        isFieldValid = false
                        // add error text to fieldState
                        fieldState.errorText.add(it.errorText!!)
                    }
                }
                Log.d("Form", "Field Validation ($name): $isFieldValid")
                fieldState.isValid.value = isFieldValid

                // if we should ignore untouched fields, every field should be marked as changed
                if (ignoreUntouched) {
                    fieldState.hasChanges.value = true
                }

            } catch (e: Exception) {
                Log.e("Form", e.toString())
            }
        }

        Log.d("Form", "Form Validation: $isValid")

        this.isValid = isValid
    }
}

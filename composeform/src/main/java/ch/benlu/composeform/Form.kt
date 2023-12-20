package ch.benlu.composeform

import android.util.Log
import androidx.compose.runtime.*

abstract class Form {
    var isValid by mutableStateOf(true)

    abstract fun self(): Form

    /**
     * Returns a list of all fields in the form.
     */
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
                val fieldState = (f.get(this) as FieldState<*>)
                val value = fieldState.state.value
                val isVisible = fieldState.isVisible()
                Log.d("Form", "$name:${value} (isVisible: $isVisible)")
            } catch (e: Exception) {
                Log.e("Form", e.toString())
            }
        }
    }

    /**
     * Triggers validation for all fields in the form.
     * @param markAsChanged If true, all fields will be marked as changed.
     * @param ignoreInvisible If true, invisible fields will be ignored during validation.
     */
    fun validate(markAsChanged: Boolean = false, ignoreInvisible: Boolean = true) {
        var isValid = true
        val formFields = getFormFields()

        formFields.forEach { pair ->
            val name = pair.first
            val f = this::class.java.getDeclaredField(name)
            f.isAccessible = true


            try {
                val fieldState = (f.get(this) as FieldState<Any>)

                // if we should ignore invisible fields, skip validation
                if (ignoreInvisible && !fieldState.isVisible()) {
                    return@forEach
                }

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
                        fieldState.errorText.add(it.errorText)
                    }
                }
                Log.d("Form", "Field Validation ($name): $isFieldValid")
                fieldState.isValid.value = isFieldValid

                // if we should ignore untouched fields, every field should be marked as changed
                if (markAsChanged) {
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

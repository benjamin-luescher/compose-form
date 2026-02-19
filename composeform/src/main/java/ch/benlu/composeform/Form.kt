package ch.benlu.composeform

import android.util.Log
import androidx.compose.runtime.*
import java.lang.reflect.Field

/**
 * Data class representing a cached form field with all necessary metadata.
 */
private data class CachedFormField(
    val name: String,
    val field: Field,
    val annotation: FormField
)

/**
 * Exception thrown when form field operations fail.
 */
class FormFieldException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

abstract class Form {
    var isValid by mutableStateOf(true)

    /**
     * Lazy-initialized cache of form fields discovered via reflection.
     * This cache is computed once per form instance and reused for all operations.
     */
    private val cachedFormFields: List<CachedFormField> by lazy {
        discoverFormFields()
    }

    /**
     * Discovers and caches all @FormField annotated fields in the form.
     * Performs validation and error handling during discovery.
     * 
     * @return List of cached form field metadata
     * @throws FormFieldException if field discovery fails
     */
    private fun discoverFormFields(): List<CachedFormField> {
        return try {
            val formClass = this::class.java
            val discoveredFields = mutableListOf<CachedFormField>()
            
            formClass.declaredFields.forEach { field ->
                val annotation = field.getAnnotation(FormField::class.java)
                if (annotation != null) {
                    try {
                        // Make field accessible for future operations
                        field.isAccessible = true
                        
                        // Validate that the field is actually a FieldState
                        validateFieldType(field)
                        
                        discoveredFields.add(
                            CachedFormField(
                                name = field.name,
                                field = field,
                                annotation = annotation
                            )
                        )
                    } catch (e: Exception) {
                        val errorMsg = "Failed to process @FormField annotated field '${field.name}': ${e.message}"
                        Log.e("Form", errorMsg, e)
                        throw FormFieldException(errorMsg, e)
                    }
                }
            }
            
            discoveredFields
        } catch (e: FormFieldException) {
            throw e
        } catch (e: Exception) {
            val errorMsg = "Critical error during form field discovery: ${e.message}"
            Log.e("Form", errorMsg, e)
            throw FormFieldException(errorMsg, e)
        }
    }

    /**
     * Validates that a field is of the correct FieldState type.
     * 
     * @param field The field to validate
     * @throws FormFieldException if the field is not a valid FieldState
     */
    private fun validateFieldType(field: Field) {
        try {
            val fieldValue = field.get(this)
            if (fieldValue !is FieldState<*>) {
                throw FormFieldException(
                    "Field '${field.name}' annotated with @FormField must be of type FieldState<*>, " +
                    "but found: ${fieldValue?.javaClass?.simpleName ?: "null"}"
                )
            }
        } catch (e: IllegalAccessException) {
            throw FormFieldException(
                "Cannot access field '${field.name}'. Ensure it's properly initialized.", e
            )
        }
    }

    /**
     * Safely gets a FieldState from a cached form field with proper type checking.
     * 
     * @param cachedField The cached field metadata
     * @return The FieldState instance
     * @throws FormFieldException if the field cannot be accessed or cast safely
     */
    private fun getFieldStateSafely(cachedField: CachedFormField): FieldState<*> {
        return try {
            val fieldValue = cachedField.field.get(this)
            
            when (fieldValue) {
                is FieldState<*> -> fieldValue
                null -> throw FormFieldException(
                    "Field '${cachedField.name}' is null. Ensure it's properly initialized."
                )
                else -> throw FormFieldException(
                    "Field '${cachedField.name}' is not a FieldState. Expected FieldState<*>, " +
                    "but found: ${fieldValue.javaClass.simpleName}"
                )
            }
        } catch (e: IllegalAccessException) {
            throw FormFieldException(
                "Cannot access field '${cachedField.name}': ${e.message}", e
            )
        } catch (e: FormFieldException) {
            throw e
        } catch (e: Exception) {
            throw FormFieldException(
                "Unexpected error accessing field '${cachedField.name}': ${e.message}", e
            )
        }
    }

    /**
     * Returns a list of all cached form fields.
     * This method uses the lazy-initialized cache for optimal performance.
     * 
     * @return List of cached form field metadata
     */
    private fun getFormFields(): List<CachedFormField> {
        return cachedFormFields
    }

    /**
     * Returns a map of field names to their current raw values.
     */
    fun getRawValues(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        for (cachedField in getFormFields()) {
            try {
                val fieldState = getFieldStateSafely(cachedField)
                result[cachedField.name] = fieldState.state.value
            } catch (e: Exception) {
                Log.e("Form", "Error reading field '${cachedField.name}': ${e.message}")
            }
        }
        return result
    }

    /**
     * Triggers validation for all fields in the form.
     * Uses cached field discovery and safe casting for optimal performance and reliability.
     * 
     * @param markAsChanged If true, all fields will be marked as changed.
     * @param ignoreInvisible If true, invisible fields will be ignored during validation.
     */
    fun validate(markAsChanged: Boolean = false, ignoreInvisible: Boolean = true) {
        var formIsValid = true
        
        try {
            val formFields = getFormFields()

            formFields.forEach { cachedField ->
                try {
                    val fieldState = getFieldStateSafely(cachedField)
                    
                    // Skip invisible fields if requested
                    if (ignoreInvisible && !fieldState.isVisible()) {
                        return@forEach
                    }

                    // Perform field validation with safe operations
                    val fieldIsValid = validateSingleField(cachedField, fieldState, markAsChanged)
                    
                    if (!fieldIsValid) {
                        formIsValid = false
                    }
                    
                } catch (e: FormFieldException) {
                    Log.e("Form", "Validation error for field '${cachedField.name}': ${e.message}")
                    formIsValid = false
                } catch (e: Exception) {
                    Log.e("Form", "Unexpected validation error for field '${cachedField.name}': ${e.message}")
                    formIsValid = false
                }
            }
            
        } catch (e: Exception) {
            Log.e("Form", "Critical error during form validation: ${e.message}", e)
            formIsValid = false
        }

        this.isValid = formIsValid
    }

    /**
     * Validates only the given field and recomputes form-level validity
     * by reading each field's cached isValid state (without re-running their validators).
     * Use this for per-keystroke validation to avoid O(fields * validators) cost.
     *
     * @param fieldState The field state that changed and needs validation
     */
    fun validateField(fieldState: FieldState<*>) {
        try {
            val formFields = getFormFields()
            // Find the cached field matching this fieldState and validate it
            for (cachedField in formFields) {
                try {
                    val fs = getFieldStateSafely(cachedField)
                    if (fs === fieldState) {
                        validateSingleField(cachedField, fs, markAsChanged = false)
                        break
                    }
                } catch (e: Exception) {
                    // Continue searching
                }
            }
        } catch (e: Exception) {
            Log.e("Form", "Error during single-field validation: ${e.message}", e)
        }
        recomputeFormValidity()
    }

    /**
     * Recomputes form-level isValid by reading each field's cached isValid state
     * without re-running any validators.
     */
    private fun recomputeFormValidity() {
        var formIsValid = true
        try {
            val formFields = getFormFields()
            for (cachedField in formFields) {
                try {
                    val fs = getFieldStateSafely(cachedField)
                    if (!fs.isVisible()) continue
                    if (!fs.isValid.value) {
                        formIsValid = false
                        break
                    }
                } catch (e: Exception) {
                    formIsValid = false
                }
            }
        } catch (e: Exception) {
            Log.e("Form", "Error recomputing form validity: ${e.message}", e)
            formIsValid = false
        }
        this.isValid = formIsValid
    }

    /**
     * Validates a single field with comprehensive error handling.
     * 
     * @param cachedField The cached field metadata
     * @param fieldState The field state to validate
     * @param markAsChanged Whether to mark the field as changed
     * @return True if the field is valid, false otherwise
     */
    private fun validateSingleField(
        cachedField: CachedFormField,
        fieldState: FieldState<*>,
        markAsChanged: Boolean
    ): Boolean {
        return try {
            // Safely cast the FieldState to handle Any type
            @Suppress("UNCHECKED_CAST")
            val typedFieldState = fieldState as FieldState<Any>
            
            val fieldName = cachedField.name
            val value = typedFieldState.state.value
            val validators = typedFieldState.validators
            
            var isFieldValid = true

            // Clear previous error messages
            typedFieldState.errorText.clear()

            // Run all validators
            validators.forEach { validator ->
                try {
                    if (!validator.validate(value)) {
                        isFieldValid = false
                        typedFieldState.errorText.add(validator.errorText)
                    }
                } catch (e: Exception) {
                    Log.e("Form", "Validator error for field '$fieldName': ${e.message}")
                    isFieldValid = false
                    typedFieldState.errorText.add("Validation error: ${e.message}")
                }
            }

            // Update field state
            typedFieldState.isValid.value = isFieldValid

            // Mark as changed if requested
            if (markAsChanged) {
                typedFieldState.hasChanges.value = true
            }

            isFieldValid
            
        } catch (e: ClassCastException) {
            Log.e("Form", "Type casting error for field '${cachedField.name}': ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("Form", "Error validating field '${cachedField.name}': ${e.message}")
            false
        }
    }
}

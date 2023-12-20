package ch.benlu.composeform

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class FieldState<T>(
    val state: MutableState<T>,
    val validators: MutableList<Validator<T>> = mutableListOf(),
    val errorText: MutableList<String> = mutableListOf(),
    val isValid: MutableState<Boolean?> = mutableStateOf(false),
    val isVisible: () -> Boolean = { true },
    val hasChanges: MutableState<Boolean?> = mutableStateOf(false),
    var options: MutableList<T> = mutableListOf(),
    val optionItemFormatter: ((T?) -> String)? = null,
) {
    fun hasError(): Boolean {
        return isVisible() && isValid.value == false && hasChanges.value == true
    }

    fun selectedOption(): T? {
        return options.firstOrNull { it == state.value }
    }

    fun selectedOptionText(): String? {
        val selectedOption = selectedOption() ?: return null
        return optionItemFormatter?.invoke(selectedOption) ?: selectedOption.toString()
    }
}
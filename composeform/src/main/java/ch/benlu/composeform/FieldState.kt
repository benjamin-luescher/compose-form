package ch.benlu.composeform

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class FieldState<T>(
    val state: MutableState<T>,
    val validators: MutableList<Validator<T>> = mutableListOf(),
    val errorText: SnapshotStateList<String> = mutableStateListOf(),
    val isValid: MutableState<Boolean> = mutableStateOf(false),
    val isVisible: () -> Boolean = { true },
    val hasChanges: MutableState<Boolean> = mutableStateOf(false),
    var options: MutableList<T> = mutableListOf(),
    val optionItemFormatter: ((T?) -> String)? = null,
) {
    fun hasError(): Boolean {
        return isVisible() && !isValid.value && hasChanges.value
    }

    fun selectedOption(): T? {
        return options.firstOrNull { it == state.value }
    }

    fun selectedOptionText(): String? {
        val selectedOption = selectedOption() ?: return null
        return optionItemFormatter?.invoke(selectedOption) ?: selectedOption.toString()
    }
}
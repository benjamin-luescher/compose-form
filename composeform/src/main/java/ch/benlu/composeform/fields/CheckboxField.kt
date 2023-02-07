package ch.benlu.composeform.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import ch.benlu.composeform.Field
import ch.benlu.composeform.FieldState
import ch.benlu.composeform.Form
import ch.benlu.composeform.components.CheckboxComponent

class CheckboxField(
    label: String,
    form: Form,
    modifier: Modifier? = Modifier,
    fieldState: FieldState<Boolean?>,
    isVisible: Boolean = true,
    isEnabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    formatter: ((raw: Boolean?) -> String)? = null
) : Field<Boolean>(
    label = label,
    form = form,
    fieldState = fieldState,
    isVisible = isVisible,
    isEnabled = isEnabled,
    modifier = modifier,
    imeAction = imeAction,
    formatter = formatter
) {

    /**
     * Returns a composable representing the DateField / Picker for this field
     */
    @Composable
    override fun Field() {
        this.updateComposableValue()
        if (!isVisible) {
            return
        }
        CheckboxComponent(
            modifier = modifier ?: Modifier,
            checked = fieldState.state.value == true,
            onCheckedChange = {
                this.onChange(it, form)
            },
            label = label,
            hasError = fieldState.hasError(),
            errorText = fieldState.errorText
        )
    }
}

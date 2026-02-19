package ch.benlu.composeform.fields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import ch.benlu.composeform.Field
import ch.benlu.composeform.FieldState
import ch.benlu.composeform.Form
import ch.benlu.composeform.components.SwitchComponent

class SwitchField(
    label: String,
    form: Form,
    modifier: Modifier? = Modifier,
    fieldState: FieldState<Boolean?>,
    isEnabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    formatter: ((raw: Boolean?) -> String)? = null,
    changed: ((v: Boolean?) -> Unit)? = null
) : Field<Boolean>(
    label = label,
    form = form,
    fieldState = fieldState,
    isEnabled = isEnabled,
    modifier = modifier,
    imeAction = imeAction,
    formatter = formatter,
    changed = changed
) {

    @Composable
    override fun Field() {
        this.updateComposableValue()
        if (!fieldState.isVisible()) {
            return
        }
        SwitchComponent(
            modifier = modifier ?: Modifier,
            checked = fieldState.state.value == true,
            onCheckedChange = {
                this.onChange(it, form)
            },
            label = label,
            isEnabled = isEnabled,
            hasError = fieldState.hasError(),
            errorText = fieldState.errorText
        )
    }
}

package ch.benlu.composeform.fields

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import ch.benlu.composeform.Field
import ch.benlu.composeform.FieldState
import ch.benlu.composeform.Form
import ch.benlu.composeform.components.TextFieldComponent
import java.util.*

class DateField(
    label: String,
    form: Form,
    modifier: Modifier? = Modifier,
    fieldState: FieldState<Date?>,
    isEnabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    formatter: ((raw: Date?) -> String)? = null,
    private val themeResId: Int = 0,
    changed: ((v: Date?) -> Unit)? = null
) : Field<Date>(
    label = label,
    form = form,
    fieldState = fieldState,
    isEnabled = isEnabled,
    modifier = modifier,
    imeAction = imeAction,
    formatter = formatter,
    changed = changed
) {

    /**
     * Returns a composable representing the DateField / Picker for this field
     */
    @Composable
    override fun Field() {
        this.updateComposableValue()
        if (!fieldState.isVisible()) {
            return
        }

        val focusRequester = FocusRequester()
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current
        val showDialog = remember { mutableStateOf(false) }

        if (showDialog.value) {
            val calendar = Calendar.getInstance()
            calendar.time = value.value ?: Date()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = remember {
                DatePickerDialog(
                    context,
                    themeResId,
                    { _: DatePicker, yyyy: Int, mm: Int, dd: Int ->
                        val c = Calendar.getInstance()
                        c.set(yyyy, mm, dd, 0, 0)
                        val d = c.time
                        value.value = d
                        this.onChange(d, form)
                        showDialog.value = false
                    },
                    year,
                    month,
                    day
                ).apply {
                    setOnDismissListener {
                        showDialog.value = false
                        focusManager.clearFocus()
                    }
                }
            }

            DisposableEffect(Unit) {
                datePickerDialog.show()
                onDispose {
                    datePickerDialog.dismiss()
                }
            }
        }

        TextFieldComponent(
            modifier = modifier ?: Modifier,
            isEnabled = isEnabled,
            label = label,
            text = formatter?.invoke(value.value) ?: value.value.toString(),
            hasError = fieldState.hasError(),
            errorText = fieldState.errorText,
            isReadOnly = true,
            focusRequester = focusRequester,
            focusChanged = {
                if (it.isFocused) {
                    showDialog.value = true
                }
            }
        )
    }
}

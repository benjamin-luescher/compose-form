package com.edorex.mobile.composeForm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ch.benlu.composeform.Field
import ch.benlu.composeform.FieldState
import ch.benlu.composeform.Form

class OtpField(
    label: String,
    form: Form,
    fieldState: FieldState<String?>,
    modifier: Modifier? = Modifier,
    isEnabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    private val length: Int = 5,
    changed: ((v: String?) -> Unit)? = null
) : Field<String>(
    label = label,
    form = form,
    fieldState = fieldState,
    isEnabled = isEnabled,
    modifier = modifier,
    imeAction = imeAction,
    changed = changed
) {
    @Composable
    override fun Field() {
        updateComposableValue()
        if (!fieldState.isVisible()) return

        val code = value.value ?: ""
        val focusRequesters = remember { List(length) { FocusRequester() } }
        val focusManager = LocalFocusManager.current

        Column(modifier = (modifier ?: Modifier).padding(top = 8.dp)) {
            if (label.isNotEmpty()) {
                Text(text = label, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 0 until length) {
                    val char = code.getOrNull(i)?.toString() ?: ""

                    OutlinedTextField(
                        value = char,
                        onValueChange = { input ->
                            val digit = input.filter { it.isDigit() }.takeLast(1)
                            val updated = buildString {
                                append(code.take(i))
                                append(digit)
                                append(code.drop(i + 1))
                            }.take(length)
                            onChange(updated.ifEmpty { null }, form)

                            if (digit.isNotEmpty() && i < length - 1) {
                                focusRequesters[i + 1].requestFocus()
                            } else if (digit.isEmpty() && i > 0) {
                                focusRequesters[i - 1].requestFocus()
                            } else if (updated.length == length) {
                                focusManager.clearFocus()
                            }
                        },
                        enabled = isEnabled,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        isError = fieldState.hasError(),
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .focusRequester(focusRequesters[i])
                    )
                }
            }

            if (fieldState.hasError()) {
                Text(
                    text = fieldState.errorText.joinToString("\n"),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }
    }
}

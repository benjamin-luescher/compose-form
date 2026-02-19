package ch.benlu.composeform.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun SliderComponent(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: ((Float) -> Unit),
    label: String,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    isEnabled: Boolean = true,
    hasError: Boolean = false,
    errorText: List<String>? = null
) {
    Column(modifier = modifier.padding(top = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "%.0f".format(value),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth()
        )
        if (hasError && errorText != null) {
            Text(
                text = errorText.joinToString("\n"),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle.Default.copy(color = MaterialTheme.colorScheme.error)
            )
        }
    }
}

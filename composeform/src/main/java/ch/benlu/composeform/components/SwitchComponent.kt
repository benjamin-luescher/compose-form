package ch.benlu.composeform.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun SwitchComponent(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    label: String,
    isEnabled: Boolean = true,
    hasError: Boolean = false,
    errorText: List<String>? = null
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Column(modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(
                enabled = isEnabled,
                onClick = { onCheckedChange(!checked) }
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = null,
                    enabled = isEnabled
                )

                Spacer(Modifier.size(6.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (hasError && errorText != null) {
            Text(
                text = errorText.joinToString("\n"),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = TextStyle.Default.copy(color = MaterialTheme.colorScheme.error)
            )
        }
    }
}

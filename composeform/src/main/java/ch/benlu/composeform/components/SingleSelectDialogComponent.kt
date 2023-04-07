package ch.benlu.composeform.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> SingleSelectDialogComponent(
    title: String,
    text: String? = null,
    optionsList: MutableList<T?>,
    defaultSelected: T?,
    submitButtonText: String,
    onSubmitButtonClick: (T) -> Unit,
    onDismissRequest: () -> Unit,
    optionItemFormatter: ((T?) -> String)? = null,
    search: ((options: MutableList<T?>, query: String) -> List<T?>)? = null
) {

    val selectedOption =
        remember { mutableStateOf(optionsList.indexOfFirst { it == defaultSelected }) }
    val query = remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Surface(
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = title, style = MaterialTheme.typography.h4)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (text != null) {
                        Text(text = text)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (search != null) {
                        TextFieldComponent(
                            leadingIcon = { Icon(Icons.Filled.Search, null) },
                            onChange = { query.value = it },
                            label = stringResource(id = android.R.string.search_go),
                            isEnabled = true,
                            text = query.value
                        )
                    }
                }

                LazyColumn(
                    modifier = if (search != null) Modifier.height(240.dp) else Modifier.wrapContentHeight()
                ) {
                    item {
                    }
                    items(
                        items = search?.invoke(optionsList, query.value) ?: optionsList,
                        key = { i ->
                            i.toString()
                        }
                    ) { item ->
                        RadioButtonComponent(
                            label = optionItemFormatter?.invoke(item) ?: item.toString(),
                            value = item,
                            selectedValue = optionsList.getOrNull(selectedOption.value),
                        ) { selectedValue ->
                            selectedOption.value =
                                optionsList.indexOfFirst { o -> o == selectedValue }
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                onDismissRequest.invoke()
                            },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = stringResource(id = android.R.string.cancel))
                        }
                        Spacer(
                            modifier = Modifier.width(16.dp)
                        )
                        Button(
                            onClick = {
                                if (selectedOption.value >= 0 && optionsList.size > selectedOption.value) {
                                    onSubmitButtonClick.invoke(optionsList[selectedOption.value]!!)
                                }
                                onDismissRequest.invoke()
                            },
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = submitButtonText)
                        }
                    }
                }
            }
        }
    }
}
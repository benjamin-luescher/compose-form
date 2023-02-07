package com.edorex.mobile.composeForm

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.benlu.composeform.fields.*
import ch.benlu.composeform.formatters.dateLong
import ch.benlu.composeform.formatters.dateShort
import com.edorex.mobile.composeForm.ui.theme.ComposeFormTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeFormTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    FormPage()
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FormPage() {
    val viewModel = hiltViewModel<MainViewModel>()

    Scaffold(
        content = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())) {

                    Column {
                        TextField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Name",
                            form = viewModel.form,
                            fieldState = viewModel.form.name,
                        ).Field()

                        TextField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Last Name",
                            form = viewModel.form,
                            fieldState = viewModel.form.lastName,
                            isEnabled = false,
                        ).Field()

                        TextField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "E-Mail",
                            form = viewModel.form,
                            fieldState = viewModel.form.email,
                            keyboardType = KeyboardType.Email
                        ).Field()

                        PasswordField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Password",
                            form = viewModel.form,
                            fieldState = viewModel.form.password
                        ).Field()

                        PasswordField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Password Confirm",
                            form = viewModel.form,
                            fieldState = viewModel.form.passwordConfirm
                        ).Field()

                        PickerField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Country",
                            form = viewModel.form,
                            fieldState = viewModel.form.country
                        ).Field()

                        DateField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Start Date",
                            form = viewModel.form,
                            fieldState = viewModel.form.startDate,
                            formatter = ::dateShort
                        ).Field()

                        DateField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "End Date",
                            form = viewModel.form,
                            fieldState = viewModel.form.endDate,
                            formatter = ::dateLong
                        ).Field()

                        CheckboxField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            fieldState = viewModel.form.agreeWithTerms,
                            label = "I agree to Terms & Conditions",
                            form = viewModel.form
                        ).Field()
                    }
                }

                ButtonRow(nextClicked = {
                    viewModel.validate()
                })

            }
        }
    )
}

@Composable
fun ButtonRow(nextClicked: () -> Unit) {
    Row {
        Button(
            enabled = false,
            modifier = Modifier.weight(1f),
            onClick = {
                // nothing
            }
        ) {
            Text("Back")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                nextClicked()
            }
        ) {
            Text("Validate")
        }
    }
}

@Preview
@Composable
fun FormPagePreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        FormPage()
    }
}
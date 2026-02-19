package com.edorex.mobile.composeForm

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.benlu.composeform.fields.*
import ch.benlu.composeform.formatters.dateLong
import ch.benlu.composeform.formatters.dateShort
import com.edorex.mobile.composeForm.models.Country
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
                    color = MaterialTheme.colorScheme.background
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
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                Row(modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())) {

                    Column {
                        TextField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Name",
                            form = viewModel.form,
                            fieldState = viewModel.form.name,
                            changed = {
                                // log the name to show that changed is called
                                Log.d("Form", "Name changed: $it")
                            }
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
                            fieldState = viewModel.form.password,
                            changed = {
                                // Cross-field: re-validate the whole form so
                                // password-confirm error updates immediately
                                viewModel.form.validate()
                            }
                        ).Field()

                        PasswordField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Password Confirm",
                            form = viewModel.form,
                            fieldState = viewModel.form.passwordConfirm,
                            changed = {
                                viewModel.form.validate()
                            }
                        ).Field()

                        PickerField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Country",
                            form = viewModel.form,
                            fieldState = viewModel.form.country
                        ).Field()

                        PickerField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Country Not searchable",
                            form = viewModel.form,
                            fieldState = viewModel.form.countryNotSearchable,
                            isSearchable = false
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
                            themeResId = R.style.customDatePickerStyle,
                            formatter = ::dateLong
                        ).Field()

                        SliderField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Rating",
                            form = viewModel.form,
                            fieldState = viewModel.form.rating,
                            valueRange = 0f..10f,
                            steps = 9
                        ).Field()

                        OtpField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            label = "Verification Code",
                            form = viewModel.form,
                            fieldState = viewModel.form.otp,
                            length = 5
                        ).Field()

                        SwitchField(
                            modifier = Modifier.padding(bottom = 8.dp),
                            fieldState = viewModel.form.newsletter,
                            label = "Subscribe to newsletter",
                            form = viewModel.form
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
        color = MaterialTheme.colorScheme.background
    ) {
        FormPage()
    }
}
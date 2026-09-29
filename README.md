# Android Compose Form Library
![https://jitpack.io/#benjamin-luescher/compose-form](https://jitpack.io/v/benjamin-luescher/compose-form.svg)

This library provides an easy-to-use and customizable solution for building forms in Android Jetpack Compose. It includes form fields such as text input, pickers, checkbox, and more, with built-in validators to ensure accurate user input. Data binding is also supported, making it easy to work with form data in your code.

The library uses reflection, to provide more flexibility in your form design. Whether you're building a complex registration form or a simple feedback form, this library has you covered.

![ComposeForm](/images/logo.png "ComposeForm")

## Getting Started
To start using the library in your Android Compose project, follow these steps:
1. Add the JitPack repository to your settings.gradle file
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
}
```
> Note: In older Android projects, the repositories are defined in the root build.gradle file.

2. Add the dependency.

**Using Version Catalog (recommended)** — add to `gradle/libs.versions.toml`:
```toml
[versions]
compose-form = "0.4.0"

[libraries]
compose-form = { group = "com.github.benjamin-luescher", name = "compose-form", version.ref = "compose-form" }
```
Then in your module's `build.gradle.kts`:
```kotlin
implementation(libs.compose.form)
```

**Using build.gradle directly:**
```kotlin
implementation 'com.github.benjamin-luescher:compose-form:0.4.0'
```

## Easy example
In a first example we create a simple form with two text fields. The form will look like this:

![ComposeForm Simple](/screenshots/png/simple-form.png "Simple Form")

1. Create your form class with form field annotations (`@FormField`)
```kotlin
class MainForm : Form() {

    @FormField
    val name = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(NotEmptyValidator())
    )

    @FormField
    val lastName = FieldState(
        state = mutableStateOf<String?>(null)
    )
}
```
2. Create a ViewModel for your form.
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    var form = MainForm()

    fun validate() {
        form.validate(true)
        Log.d("MainViewModel", "Validate (form is valid: ${form.isValid})")
    }
}
```
3. Add the fields in your composable UI.
```kotlin
Column {
    TextField(
        label = "Name",
        form = viewModel.form,
        fieldState = viewModel.form.name,
    ).Field()

    TextField(
        label = "Last Name",
        form = viewModel.form,
        fieldState = viewModel.form.lastName
    ).Field()
}
```

## Extended Form example
We now try to make a more complex form with different validators, date fields, password fields and
searchable pickers. This is how the form will look like:

![ComposeForm Extended](/screenshots/gif/composeform-extended.gif "Extended Form")

1. Create a form class with form fields. Define form fields by the `@FormField` annotation.
```kotlin
// in this example we have a separate data class `Country` for a country picker.
data class Country(
    val code: String,
    val name: String
): PickerValue() {
    override fun searchFilter(query: String): Boolean {
        return this.name.startsWith(query)
    }
}

class MainForm(resourcesProvider: ResourcesProvider) : Form() {

    @FormField
    val name = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(
            NotEmptyValidator(),
            MinLengthValidator(
                minLength = 3,
                errorText = resourcesProvider.getString(R.string.error_min_length)
            )
        )
    )

    @FormField
    val lastName = FieldState(
        state = mutableStateOf<String?>(null)
    )

    @FormField
    val password = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(
            NotEmptyValidator(),
            MinLengthValidator(
                minLength = 8,
                errorText = resourcesProvider.getString(R.string.error_min_length)
            )
        )
    )

    @FormField
    val passwordConfirm = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(
            IsEqualValidator({ password.state.value })
        )
    )

    @FormField
    val email = FieldState(
        state = mutableStateOf<String?>(null),
        validators = mutableListOf(
            EmailValidator()
        )
    )

    @FormField
    val country = FieldState(
        state = mutableStateOf<Country?>(null),
        options = mutableListOf(
            Country(code = "CH", name = "Switzerland"),
            Country(code = "DE", name = "Germany"),
            Country(code = "FR", name = "France"),
            Country(code = "US", name = "United States"),
            Country(code = "ES", name = "Spain"),
            Country(code = "BR", name = "Brazil"),
            Country(code = "CN", name = "China"),
        ),
        optionItemFormatter = { "${it?.name}" },
        validators = mutableListOf(
            NotEmptyValidator()
        )
    )

    @FormField
    val startDate = FieldState(
        state = mutableStateOf<Date?>(null),
        validators = mutableListOf(
            NotEmptyValidator()
        )
    )

    @FormField
    val endDate = FieldState(
        state = mutableStateOf<Date?>(null),
        validators = mutableListOf(
            NotEmptyValidator(),
            DateValidator(
                minDateTime = {startDate.state.value?.time ?: 0},
                errorText = resourcesProvider.getString(R.string.error_date_after_start_date)
            )
        )
    )

    @FormField
    val agreeWithTerms = FieldState(
        state = mutableStateOf<Boolean?>(null),
        validators = mutableListOf(
            IsEqualValidator({ true })
        )
    )
}
```
2. Create a ViewModel for your form.
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    resourcesProvider: ResourcesProvider
): ViewModel() {
    var form = MainForm(resourcesProvider)

    fun validate() {
        form.validate(true)
        Log.d("MainViewModel", "Validate (form is valid: ${form.isValid})")
    }
}
```
3. Add the fields in your composable UI.
```kotlin
Column {
    TextField(
        modifier = Modifier.padding(bottom = 8.dp),
        label = "Name",
        form = viewModel.form,
        fieldState = viewModel.form.name,
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

    TextField(
        modifier = Modifier.padding(bottom = 8.dp),
        label = "Last Name",
        form = viewModel.form,
        fieldState = viewModel.form.lastName,
        isEnabled = false,
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
```

## Validation

### Per-keystroke validation
By default, each keystroke validates only the changed field and recomputes form-level `isValid` from cached results. This is efficient for large forms.

### Full-form validation
Call `form.validate()` to run all validators on all fields. Use this on form submission:
```kotlin
fun submit() {
    form.validate(markAsChanged = true)
    if (form.isValid) {
        val values = form.getRawValues()
        // submit values...
    }
}
```

### Cross-field validation
For fields that depend on each other (e.g., password confirmation), trigger full-form validation in the `changed` callback:
```kotlin
PasswordField(
    label = "Password",
    form = viewModel.form,
    fieldState = viewModel.form.password,
    changed = {
        viewModel.form.validate()
    }
).Field()
```

## Custom Validators
Create a custom validator by extending `Validator<T>`:
```kotlin
class PhoneNumberValidator(errorText: String? = null) : Validator<String?>(
    validate = { value ->
        value != null && value.matches(Regex("^\\+?[0-9]{10,15}$"))
    },
    errorText = errorText ?: "Please enter a valid phone number."
)
```

Use it on any field:
```kotlin
@FormField
val phone = FieldState(
    state = mutableStateOf<String?>(null),
    validators = mutableListOf(
        NotEmptyValidator(),
        PhoneNumberValidator()
    )
)
```

## Custom Fields
If the built-in fields don't fit your needs, create your own by extending `Field<T>`. This gives you full control over the composable UI while still integrating with the form's validation and state management.

Here's an OTP (one-time password) field that renders individual digit boxes with auto-advancing focus:

```kotlin
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
```

Then use it like any built-in field:
```kotlin
@FormField
val otp = FieldState(
    state = mutableStateOf<String?>(null),
    validators = mutableListOf(
        NotEmptyValidator(),
        MinLengthValidator(minLength = 5, errorText = "Enter all 5 digits")
    )
)

// In your composable:
OtpField(
    label = "Verification Code",
    form = viewModel.form,
    fieldState = viewModel.form.otp,
    length = 5
).Field()
```

Key points:
- Call `updateComposableValue()` at the start to sync state
- Check `fieldState.isVisible()` to respect visibility
- Call `onChange(value, form)` when the user changes the value — this updates state, runs validation, and triggers the `changed` callback
- Read `fieldState.hasError()` and `fieldState.errorText` to display errors

## Features
* A variety of form fields to choose from, including text input, password, date picker, picker, checkbox, switch, slider, and more
* Built-in validators to ensure accurate user input
* Data binding for easy management of form data in your code
* Extensible — create custom fields and validators by subclassing `Field<T>` and `Validator<T>`
* Per-keystroke single-field validation for optimal performance
* `getRawValues()` to extract all form data as a `Map<String, Any?>`

## License
This library is licensed under the MIT License.

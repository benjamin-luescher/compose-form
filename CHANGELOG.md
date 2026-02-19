# Changelog

## 0.3.0

### Breaking Changes

- `Field.onChange()` now validates only the changed field instead of all fields. If you relied on full-form re-validation on every keystroke, call `form.validate()` in your `changed` callback.
- `logRawValue()` replaced by `getRawValues()` which returns `Map<String, Any?>`.
- `self()` removed from `Form` — delete `override fun self()` from your subclasses.
- `FieldState.isValid` and `hasChanges` changed from `MutableState<Boolean?>` to `MutableState<Boolean>`.
- `errorText` type changed to `SnapshotStateList<String>`.

### New Features

- **SwitchField** — built-in toggle field using Material 3 `Switch`.
- **SliderField** — built-in range input field using Material 3 `Slider` (previously only in demo app).

### Migration from 0.2.8

1. Remove `override fun self()` from your `Form` subclasses:
```kotlin
// Delete this from your Form subclass
override fun self(): Form = this
```

2. Replace `logRawValue()` with `getRawValues()`:
```kotlin
// Before
form.logRawValue()

// After
val values = form.getRawValues() // returns Map<String, Any?>
```

3. If you depend on cross-field validation on every keystroke (e.g. password confirmation), add `form.validate()` in your `changed` callback:
```kotlin
PasswordField(
    label = "Password",
    form = viewModel.form,
    fieldState = viewModel.form.password,
    changed = { viewModel.form.validate() }
).Field()
```

### Improvements

- Single-field validation on keystroke for better performance on large forms.
- DatePickerDialog is now lazy and properly dismissed on dispose (fixes window leak).
- Removed debug logging from the library.
- Added ~141 unit tests covering `Form`, `FieldState`, and all validators.
- Updated dependencies: SDK 35, core-ktx 1.15.0, lifecycle 2.8.7, activity-compose 1.9.3, hilt 2.51.1.
- Fixed JitPack build by updating `jitpack.yml` to `openjdk17`.

## 0.2.8

- Update version to 0.2.8
- Fix OutlinedTextField multi-line label

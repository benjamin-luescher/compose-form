package com.edorex.mobile.composeForm

import android.util.Log
import androidx.lifecycle.ViewModel
import ch.benlu.composeform.validators.NotEmptyValidator
import com.edorex.mobile.composeForm.di.ResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    resourcesProvider: ResourcesProvider
): ViewModel() {
    var form = MainForm(resourcesProvider)

    fun validate() {
        form.validate(true)
        form.logRawValue()
        Log.d("MainViewModel", "Submit (form is valid: ${form.isValid})")
    }

    fun doSomething() {
        form.name.validators.removeIf { it::class == NotEmptyValidator::class }
        form.name.state.value = "Benji"
    }
}

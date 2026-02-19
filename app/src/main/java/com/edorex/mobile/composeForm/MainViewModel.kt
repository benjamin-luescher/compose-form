package com.edorex.mobile.composeForm

import android.util.Log
import androidx.lifecycle.ViewModel
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
        Log.d("MainViewModel", "Submit (form is valid: ${form.isValid}), values: ${form.getRawValues()}")
    }
}

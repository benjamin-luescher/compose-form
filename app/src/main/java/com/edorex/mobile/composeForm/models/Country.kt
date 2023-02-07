package com.edorex.mobile.composeForm.models

import ch.benlu.composeform.fields.PickerValue

data class Country(
    val code: String,
    val name: String
): PickerValue() {
    override fun searchFilter(query: String): Boolean {
        return this.name.startsWith(query)
    }
}

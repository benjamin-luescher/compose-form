package ch.benlu.composeform

abstract class Validator<T>(
    val validate: (s: T?) -> Boolean,
    val errorText: String
)

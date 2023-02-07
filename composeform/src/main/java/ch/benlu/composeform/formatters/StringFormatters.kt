package ch.benlu.composeform.formatters

fun upperCase(r: String?): String {
    return r?.uppercase() ?: ""
}

fun lowerCase(r: String?): String {
    return r?.lowercase() ?: ""
}

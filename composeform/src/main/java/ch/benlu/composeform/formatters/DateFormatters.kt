package ch.benlu.composeform.formatters

import java.text.DateFormat
import java.util.*

fun dateShort(r: Date?): String {
    return if (r != null) DateFormat.getDateInstance().format(r) else ""
}

fun dateLong(r: Date?): String {
    return if (r != null) DateFormat.getDateInstance(DateFormat.LONG).format(r) else ""
}

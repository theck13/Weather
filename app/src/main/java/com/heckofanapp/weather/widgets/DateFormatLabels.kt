package com.heckofanapp.weather.widgets

/**
 * Converts a date format pattern (e.g. "EEE d MMM") into an example label for the widget
 * date-format picker, substituting each field with a sample value while preserving the
 * separators between them:
 *  - EEE -> "Fri"
 *  - d   -> "2"
 *  - dd  -> "02"
 *  - MM  -> "01"
 *  - MMM -> "Jan"
 */
fun dateFormatLabel(pattern: String): String {
    return Regex("E+|d+|M+").replace(pattern) { match ->
        when (match.value) {
            "EEE" -> "Fri"
            "d" -> "2"
            "dd" -> "02"
            "MM" -> "01"
            "MMM" -> "Jan"
            else -> match.value
        }
    }
}

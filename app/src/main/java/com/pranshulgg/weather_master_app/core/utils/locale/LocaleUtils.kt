package com.pranshulgg.weather_master_app.core.utils.locale

import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val value: String,
)

fun getAppLocalLocales(): List<Language> {
    return listOf(
        Language(
            code = "SYS",
            name = "System",
            nativeName = "Device Language",
            value = "sys",
        ),
        Language(
            code = "AR",
            name = "Arabic (Saudi Arabia)",
            nativeName = "العربية",
            value = "ar-SA",
        ),
        Language(
            code = "AZ",
            name = "Azerbaijani",
            nativeName = "Azərbaycan dili",
            value = "az-AZ",
        ),
        Language(
            code = "BG",
            name = "Bulgarian",
            nativeName = "Български",
            value = "bg-BG",
        ),
        Language(
            code = "ES",
            name = "Catalan",
            nativeName = "Català",
            value = "ca-ES",
        ),
        Language(
            code = "CN",
            name = "Chinese (Simplified)",
            nativeName = "简体中文",
            value = "zh-CN",
        ),
        Language(
            code = "TW",
            name = "Chinese (Traditional)",
            nativeName = "繁體中文",
            value = "zh-TW",
        ),
        Language(
            code = "CZ",
            name = "Czech",
            nativeName = "Čeština",
            value = "cs-CZ",
        ),
        Language(
            code = "NL",
            name = "Dutch",
            nativeName = "Nederlands",
            value = "nl-NL",
        ),
        Language(
            code = "US",
            name = "English",
            nativeName = "English",
            value = "en",
        ),
        Language(
            code = "PH",
            name = "Filipino",
            nativeName = "Filipino",
            value = "fil-PH",
        ),
        Language(
            code = "FI",
            name = "Finnish",
            nativeName = "Suomi",
            value = "fi-FI",
        ),
        Language(
            code = "FR",
            name = "French",
            nativeName = "Français",
            value = "fr-FR",
        ),
        Language(
            code = "DE",
            name = "German",
            nativeName = "Deutsch",
            value = "de-DE",
        ),
        Language(
            code = "GR",
            name = "Greek",
            nativeName = "Ελληνικά",
            value = "el-GR",
        ),
        Language(
            code = "HE",
            name = "Hebrew",
            nativeName = "עברית",
            value = "he-IL",
        ),
        Language(
            code = "HU",
            name = "Hungarian",
            nativeName = "Magyar",
            value = "hu-HU",
        ),
        Language(
            code = "ID",
            name = "Indonesian",
            nativeName = "Bahasa Indonesia",
            value = "id-ID",
        ),
        Language(
            code = "IT",
            name = "Italian",
            nativeName = "Italiano",
            value = "it-IT",
        ),
        Language(
            code = "JP",
            name = "Japanese",
            nativeName = "日本語",
            value = "ja-JP",
        ),
        Language(
            code = "KR",
            name = "Korean",
            nativeName = "한국어",
            value = "ko-KR",
        ),
        Language(
            code = "LT",
            name = "Lithuanian",
            nativeName = "lietuvių kalba",
            value = "lt-LT",
        ),
        Language(
            code = "IR",
            name = "Persian",
            nativeName = "فارسی",
            value = "fa-IR",
        ),
        Language(
            code = "PL",
            name = "Polish",
            nativeName = "Polski",
            value = "pl-PL",
        ),
        Language(
            code = "BR",
            name = "Portuguese (Brazil)",
            nativeName = "Português (Brasil)",
            value = "pt-BR",
        ),
        Language(
            code = "PT",
            name = "Portuguese (Portugal)",
            nativeName = "Português (Portugal)",
            value = "pt-PT",
        ),
        Language(
            code = "RO",
            name = "Romanian",
            nativeName = "Română",
            value = "ro-RO",
        ),
        Language(
            code = "RU",
            name = "Russian",
            nativeName = "Русский",
            value = "ru-RU",
        ),
        Language(
            code = "CS",
            name = "Serbian (Cyrillic)",
            nativeName = "Српски",
            value = "sr-CS",
        ),
        Language(
            code = "SP",
            name = "Serbian (Latin)",
            nativeName = "Srpski",
            value = "sr-SP",
        ),
        Language(
            code = "SI",
            name = "Slovenian",
            nativeName = "Slovenščina",
            value = "sl-SI",
        ),
        Language(
            code = "ES",
            name = "Spanish",
            nativeName = "Español",
            value = "es-ES",
        ),
        Language(
            code = "SE",
            name = "Swedish",
            nativeName = "Svenska",
            value = "sv-SE",
        ),
        Language(
            code = "TH",
            name = "Thai",
            nativeName = "ไทย",
            value = "th-TH",
        ),
        Language(
            code = "TR",
            name = "Turkish",
            nativeName = "Türkçe",
            value = "tr-TR",
        ),
        Language(
            code = "UA",
            name = "Ukrainian",
            nativeName = "Українська",
            value = "uk-UA",
        ),
        Language(
            code = "VN",
            name = "Vietnamese",
            nativeName = "Tiếng Việt",
            value = "vi-VN",
        ),
    )
}

fun getCurrentAppLocale(): Locale {
    val locale = AppCompatDelegate.getApplicationLocales()[0]
    return locale ?: Locale.getDefault()
}

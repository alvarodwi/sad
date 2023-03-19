package me.varoa.sad.core.data.prefs

import androidx.datastore.preferences.core.stringPreferencesKey

object Keys {
    val THEME_KEY = stringPreferencesKey("theme")
    val SESSION_TOKEN_KEY = stringPreferencesKey("session_token")
    val SESSION_NAME_KEY = stringPreferencesKey("session_name")
}

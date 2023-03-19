package me.varoa.sad.core.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.varoa.sad.ui.ext.AppTheme

private val Context.dataStore by preferencesDataStore("prefs")

class DataStoreManager(
  appContext: Context
) {
  private val prefsDataStore = appContext.dataStore

  // theme
  val theme
    get() = prefsDataStore.data.map { prefs ->
      when (prefs[Keys.THEME_KEY]) {
        AppTheme.DARK.name -> AppTheme.DARK
        AppTheme.LIGHT.name -> AppTheme.LIGHT
        else -> AppTheme.SYSTEM
      }
    }

  suspend fun setTheme(flag: AppTheme) {
    prefsDataStore.edit { prefs -> prefs[Keys.THEME_KEY] = flag.name }
  }

  // session
  val sessionToken: Flow<String>
    get() = prefsDataStore.data.map { prefs ->
      prefs[Keys.SESSION_TOKEN_KEY] ?: ""
    }

  val sessionName: Flow<String>
    get() = prefsDataStore.data.map { prefs ->
      prefs[Keys.SESSION_NAME_KEY] ?: ""
    }

  suspend fun addSession(name: String, token: String) {
    prefsDataStore.edit { prefs ->
      prefs[Keys.SESSION_TOKEN_KEY] = "Bearer $token"
      prefs[Keys.SESSION_NAME_KEY] = name
    }
  }

  suspend fun clearSession() {
    prefsDataStore.edit { prefs ->
      prefs[Keys.SESSION_TOKEN_KEY] = ""
      prefs[Keys.SESSION_NAME_KEY] = ""
    }
  }

  val isLoggedIn: Flow<Boolean>
    get() = prefsDataStore.data.map { prefs ->
      (prefs[Keys.SESSION_TOKEN_KEY] ?: "").isNotEmpty()
    }
}
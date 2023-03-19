package me.varoa.sad.ui.widget.preference

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.datastore.preferences.core.stringPreferencesKey
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StringListDialogPreference @JvmOverloads constructor(
  activity: Activity?,
  context: Context,
  attrs:
  AttributeSet? =
    null
) :
  DialogPreference(activity, context, attrs) {
  var entryValues: List<String> = emptyList()
  var entriesRes: List<Int>
    get() = emptyList()
    set(value) {
      entries = value.map { context.getString(it) }
    }
  private var defValue: String = ""
  var entries: List<String> = emptyList()

  override fun onSetInitialValue(defaultValue: Any?) {
    super.onSetInitialValue(defaultValue)
    defValue = defaultValue as? String ?: defValue
  }

  override fun getSummary(): CharSequence? {
    return if (customSummary != null) customSummary!!
    else if (key == null) super.getSummary()
    else {
      val index = entryValues.indexOf(
        runBlocking {
          prefs.getString(stringPreferencesKey(key), defValue)
            .first()
        }
      )
      if (entries.isEmpty() || index == -1) {
        ""
      } else {
        entries[index]
      }
    }
  }

  @SuppressLint("CheckResult")
  override fun dialog(): MaterialDialog {
    return super.dialog()
      .apply {
        val default = entryValues.indexOf(
          runBlocking {
            prefs.getString(stringPreferencesKey(key), defValue)
              .first()
          }
        )
        listItemsSingleChoice(
          items = entries,
          waitForPositiveButton = false,
          initialSelection = default
        ) { _, pos, _ ->
          val value = entryValues[pos]
          if (key != null) {
            runBlocking {
              prefs.setString(stringPreferencesKey(key), value)
            }
          }
          callChangeListener(value)
          this@StringListDialogPreference.summary = this@StringListDialogPreference.summary
          dismiss()
        }
      }
  }
}
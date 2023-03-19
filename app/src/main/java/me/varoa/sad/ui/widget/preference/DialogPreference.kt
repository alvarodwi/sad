package me.varoa.sad.ui.widget.preference

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import dagger.hilt.android.EntryPointAccessors
import me.varoa.sad.di.entrypoint.PrefsEntryPoint

open class DialogPreference @JvmOverloads constructor(
    private val activity: Activity?,
    context: Context,
    attrs: AttributeSet? = null
) :
    Preference(context, attrs) {

    protected val prefs = EntryPointAccessors.fromApplication(
        context,
        PrefsEntryPoint::class.java
    ).prefs()
    private var isShowing = false
    var customSummary: String? = null

    override fun onClick() {
        if (!isShowing) {
            dialog().apply {
                onDismiss { this@DialogPreference.isShowing = false }
            }
                .show()
        }
        isShowing = true
    }

    override fun getSummary(): CharSequence? {
        return customSummary ?: super.getSummary()
    }

    open fun dialog(): MaterialDialog {
        return MaterialDialog(activity ?: context).apply {
            if (title != null) {
                title(text = title.toString())
            }
            negativeButton(android.R.string.cancel)
        }
    }
}

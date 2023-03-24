package me.varoa.sad.ui.screen.settings

import android.app.Activity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.SwitchPreferenceCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import me.varoa.sad.ui.widget.preference.StringListDialogPreference

@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class SettingsDsl

inline fun PreferenceGroup.preference(block: (@SettingsDsl Preference).() -> Unit): Preference {
    return initThenAdd(Preference(context), block)
}

inline fun PreferenceGroup.switchPreference(
    block: (@SettingsDsl SwitchPreferenceCompat).() -> Unit
): SwitchPreferenceCompat {
    return initThenAdd(SwitchPreferenceCompat(context), block)
}

inline fun PreferenceGroup.stringListPreference(
    activity: Activity?,
    block: (
        @SettingsDsl
        StringListDialogPreference
    ).() -> Unit
): StringListDialogPreference {
    return initThenAdd(StringListDialogPreference(activity, context), block)
}

inline fun <P : Preference> PreferenceGroup.initThenAdd(
    p: P,
    block: P.() -> Unit
): P {
    return p.apply {
        block()
        this.isIconSpaceReserved = false
        addPreference(this)
    }
}

inline fun <P : Preference> PreferenceGroup.addThenInit(
    p: P,
    block: P.() -> Unit
): P {
    return p.apply {
        this.isIconSpaceReserved = false
        addPreference(this)
        block()
    }
}

inline fun Preference.onClick(crossinline block: () -> Unit) {
    setOnPreferenceClickListener { block(); true }
}

inline fun Preference.onChange(crossinline block: (Any?) -> Boolean) {
    setOnPreferenceChangeListener { _, newValue -> block(newValue) }
}

var Preference.defaultValue: Any?
    get() = null // set only
    set(value) {
        setDefaultValue(value)
    }

var Preference.titleRes: Int
    get() = 0 // set only
    set(value) {
        setTitle(value)
    }

var Preference.iconRes: Int
    get() = 0 // set only
    set(value) {
        icon = VectorDrawableCompat.create(context.resources, value, context.theme)
    }

var Preference.summaryRes: Int
    get() = 0 // set only
    set(value) {
        setSummary(value)
    }

var Preference.iconTint: Int
    get() = 0 // set only
    set(value) {
        DrawableCompat.setTint(icon ?: return, value)
    }

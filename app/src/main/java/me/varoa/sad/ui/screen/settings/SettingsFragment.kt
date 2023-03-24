package me.varoa.sad.ui.screen.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import me.varoa.sad.R
import me.varoa.sad.core.data.prefs.Keys
import me.varoa.sad.databinding.FragmentSettingsBinding
import me.varoa.sad.ui.base.BaseFragment
import me.varoa.sad.ui.ext.AppTheme
import me.varoa.sad.ui.ext.toggleAppTheme
import me.varoa.sad.ui.ext.viewBinding

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {
    private val binding by viewBinding<FragmentSettingsBinding>()

    private val container get() = binding.settingsContainer
    private val toolbar get() = binding.toolbar

    override fun bindView() {
        toolbar.title = getString(R.string.lbl_settings)
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        childFragmentManager.commit {
            replace(container.id, SettingsContainer())
        }
    }

    class SettingsContainer : PreferenceFragmentCompat() {
        private val mActivity get() = requireActivity()

        override fun onCreatePreferences(
            savedInstanceState: Bundle?,
            rootKey: String?
        ) {
            val screen = preferenceManager.createPreferenceScreen(mActivity)
            preferenceScreen = screen
            setupPreferenceScreen(screen)
        }

        private fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {
            setTitle(R.string.lbl_settings)

            // language
            preference {
                titleRes = R.string.prefs_language
                summary = resources.configuration.locales.get(0).displayName
                onClick {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                }
            }

            // app theme
            stringListPreference(activity) {
                key = Keys.THEME_KEY.name
                titleRes = R.string.prefs_theme
                entriesRes = listOf(
                    R.string.prefs_theme_light,
                    R.string.prefs_theme_dark,
                    R.string.prefs_theme_system
                )
                entryValues = listOf(
                    AppTheme.LIGHT.name,
                    AppTheme.DARK.name,
                    AppTheme.SYSTEM.name
                )
                defaultValue = AppTheme.SYSTEM.name

                onChange {
                    toggleAppTheme(it as String)
                    mActivity.recreate()
                    true
                }
            }
        }
    }
}

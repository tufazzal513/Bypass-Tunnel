package com.v2ray.ang.ui.preference.activity

import android.os.Build
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.appbar.MaterialToolbar
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.helper.MmkvPreferenceDataStore
import com.v2ray.ang.ui.bottomsheet.IndicatorStyleBottomSheet
import com.v2ray.ang.ui.dialog.DpiSliderDialog
import com.v2ray.ang.ui.dialog.ThemeColorDialog
import com.v2ray.ang.util.ThemeManager
import com.v2ray.ang.ui.BaseActivity
import com.v2ray.ang.ui.CheckUpdateActivity
import com.v2ray.ang.ui.preference.CustomBannerPreference

class UiSettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setupToolbar(toolbar, showHomeAsUp = true, title = getString(R.string.title_ui_settings))

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, UiSettingsFragment())
                .commit()
        }
    }

    class UiSettingsFragment : PreferenceFragmentCompat() {

        private val appTheme by lazy { findPreference<Preference>(AppConfig.PREF_APP_THEME) }
        private val dynamicColor by lazy { findPreference<SwitchPreferenceCompat>(AppConfig.PREF_DYNAMIC_COLOR) }
        private val trueBlack by lazy { findPreference<SwitchPreferenceCompat>(AppConfig.PREF_TRUE_BLACK) }
        private val enableBlur by lazy { findPreference<SwitchPreferenceCompat>(AppConfig.PREF_ENABLE_BLUR) }
        private val nightTheme by lazy { findPreference<ListPreference>(AppConfig.PREF_UI_MODE_NIGHT) }
        private val iconShape by lazy { findPreference<ListPreference>(AppConfig.PREF_ICON_SHAPE) }
        private val customDpi by lazy { findPreference<DpiSliderDialog>(AppConfig.PREF_CUSTOM_DPI) }
        private val indicatorStyle by lazy { findPreference<Preference>(AppConfig.PREF_INDICATOR_STYLE) }
        private val navigateCheckUpdate by lazy { findPreference<CustomBannerPreference>(AppConfig.PREF_NAVIGATE_CHECK_UPDATE) }

        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            preferenceManager.preferenceDataStore = MmkvPreferenceDataStore()
            addPreferencesFromResource(R.xml.pref_ui_settings)
            initPreferenceSummaries()
            
            navigateCheckUpdate?.setOnPreferenceClickListener {
                startActivity(android.content.Intent(requireContext(), CheckUpdateActivity::class.java))
                true
            }

            appTheme?.setOnPreferenceClickListener {
                ThemeColorDialog.show(parentFragmentManager)
                true
            }

            indicatorStyle?.setOnPreferenceClickListener {
                IndicatorStyleBottomSheet(requireContext()) {}.show()
                true
            }

            dynamicColor?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
                appTheme?.isEnabled = !enabled
                activity?.recreate()
                true
            }

            trueBlack?.apply {
                val isNightModeActive = ThemeManager.isDarkMode(requireActivity())
                isEnabled = isNightModeActive
                summary = if (!isNightModeActive) {
                    getString(R.string.pref_true_black_only_in_night_mode)
                } else {
                    getString(R.string.summary_pref_true_black)
                }
                setOnPreferenceChangeListener { _, _ ->
                    activity?.recreate()
                    true
                }
            }

            enableBlur?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                MmkvManager.encodeSettings(AppConfig.PREF_ENABLE_BLUR, enabled)
                true
            }

            nightTheme?.setOnPreferenceChangeListener { pref, newValue ->
                val valueStr = newValue.toString()
                (pref as? ListPreference)?.let { lp ->
                    val idx = lp.findIndexOfValue(valueStr)
                    lp.summary = if (idx >= 0) lp.entries[idx] else valueStr
                }
                val newMode = valueStr.toInt()
                val nowNight = isNightModeAfterChange(newMode)
                updateTrueBlackState(nowNight)
                true
            }

            iconShape?.setOnPreferenceChangeListener { pref, newValue ->
                val valueStr = newValue.toString()
                (pref as? ListPreference)?.let { lp ->
                    val idx = lp.findIndexOfValue(valueStr)
                    lp.summary = if (idx >= 0) lp.entries[idx] else valueStr
                }
                requireContext().sendBroadcast(
                    android.content.Intent(AppConfig.BROADCAST_ACTION_ICON_SHAPE_CHANGED).apply {
                        putExtra(AppConfig.PREF_ICON_SHAPE, valueStr.ifEmpty { AppConfig.PREF_ICON_SHAPE_DEFAULT })
                    }
                )
                true
            }
        }

        private fun initPreferenceSummaries() {
            fun traverse(group: androidx.preference.PreferenceGroup) {
                for (i in 0 until group.preferenceCount) {
                    when (val p = group.getPreference(i)) {
                        is androidx.preference.PreferenceGroup -> traverse(p)
                        is ListPreference -> {
                            p.summary = p.entry ?: ""
                            p.setOnPreferenceChangeListener { pref, newValue ->
                                val lp = pref as ListPreference
                                val idx = lp.findIndexOfValue(newValue as? String)
                                lp.summary = (if (idx >= 0) lp.entries[idx] else newValue) as CharSequence?
                                true
                            }
                        }
                        else -> { /* no summary auto-update needed */ }
                    }
                }
            }
            preferenceScreen?.let { traverse(it) }
        }

        override fun onStart() {
            super.onStart()
            val isDynamicColor = MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
            appTheme?.isEnabled = !isDynamicColor

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                dynamicColor?.isEnabled = false
                dynamicColor?.summary = requireContext().getString(R.string.summary_pref_dynamic_color_unavailable)
            }

            val savedDpi = MmkvManager.decodeSettingsInt(AppConfig.PREF_CUSTOM_DPI, 0)
            val systemDpi = resources.displayMetrics.densityDpi
            customDpi?.summary = if (savedDpi > 0) savedDpi.toString() else systemDpi.toString()
        }

        private fun updateTrueBlackState(isNight: Boolean) {
            trueBlack?.isEnabled = isNight
            trueBlack?.summary = if (!isNight) {
                getString(R.string.pref_true_black_only_in_night_mode)
            } else {
                getString(R.string.summary_pref_true_black)
            }
            if (!isNight && trueBlack?.isChecked == true) {
                trueBlack?.isChecked = false
                MmkvManager.encodeSettings(AppConfig.PREF_TRUE_BLACK, false)
            }
        }

        private fun isNightModeAfterChange(mode: Int): Boolean {
            return when (mode) {
                1 -> true
                2 -> false
                else -> ThemeManager.isDarkMode(requireActivity())
            }
        }
    }
}

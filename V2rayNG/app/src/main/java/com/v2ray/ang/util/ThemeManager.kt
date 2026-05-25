package com.v2ray.ang.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.color.utilities.Hct
import com.google.android.material.color.utilities.SchemeTonalSpot
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.handler.MmkvManager

object ThemeManager {

    fun applyTheme(activity: Activity) {

        val isDynamic   = MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
        val useCustom   = MmkvManager.decodeSettingsBool(AppConfig.PREF_USE_CUSTOM_COLOR, false)
        val customColor = MmkvManager.decodeSettingsInt(AppConfig.PREF_CUSTOM_COLOR, 0)
        val isTrueBlack = isDarkMode(activity) && MmkvManager.decodeSettingsBool(AppConfig.PREF_TRUE_BLACK, false)

        when {
            isDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                DynamicColors.applyToActivityIfAvailable(activity)
            }

            useCustom && customColor != 0 -> {
                applyCustomColorTheme(activity, customColor)
            }

            else -> {
                val key = MmkvManager.decodeSettingsString(AppConfig.PREF_APP_THEME) ?: "8"
                applyCustomColorTheme(activity, themeSeedColorFor(activity, key))
            }
        }

        if (isTrueBlack) {
            activity.theme.applyStyle(R.style.ThemeOverlay_App_TrueBlack, true)
        }
    }

    fun applyCustomColorTheme(activity: Activity, @ColorInt seedColor: Int, isTrueBlack: Boolean = false) {
        val optionsBuilder = DynamicColorsOptions.Builder()
            .setContentBasedSource(seedColor)

        if (isTrueBlack) {
            optionsBuilder.setThemeOverlay(R.style.ThemeOverlay_App_TrueBlack)
        }

        DynamicColors.applyToActivityIfAvailable(activity, optionsBuilder.build())
    }

    fun getDynamicScheme(activity: Activity, @ColorInt seedColor: Int): SchemeTonalSpot {
        val hct = Hct.fromInt(seedColor)
        return SchemeTonalSpot(hct, isDarkMode(activity), 0.0)
    }

    fun isDarkMode(activity: Activity): Boolean {
        val uiMode = activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return uiMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun setAndSaveTheme(activity: Activity, key: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, false)
        MmkvManager.encodeSettings(AppConfig.PREF_USE_CUSTOM_COLOR, false)
        MmkvManager.encodeSettings(AppConfig.PREF_APP_THEME, key)
        activity.recreate()
    }

    fun saveCustomColor(activity: Activity, @ColorInt color: Int) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, false)
        MmkvManager.encodeSettings(AppConfig.PREF_USE_CUSTOM_COLOR, true)
        MmkvManager.encodeSettings(AppConfig.PREF_CUSTOM_COLOR, color)
        activity.recreate()
    }
    
    fun clearCustomColor(activity: Activity) {
        MmkvManager.encodeSettings(AppConfig.PREF_USE_CUSTOM_COLOR, false)
        MmkvManager.encodeSettings(AppConfig.PREF_CUSTOM_COLOR, 0)
        activity.recreate()
    }

    @ColorInt
    fun themeSeedColorFor(context: Context, key: String): Int {
        return when (key) {
            "1"  -> 0xFFBA1A1A.toInt()
            "2"  -> 0xFFB94073.toInt()
            "3"  -> 0xFF6750A4.toInt()
            "4"  -> 0xFF7E42A4.toInt()
            "5"  -> 0xFF5355A9.toInt()
            "6"  -> 0xFF335BBC.toInt()
            "7"  -> 0xFF00639B.toInt()
            "8"  -> 0xFF006874.toInt()
            "9"  -> 0xFF006A64.toInt()
            "10" -> 0xFF006D39.toInt()
            "11" -> 0xFF4A672D.toInt()
            "12" -> 0xFF5E6400.toInt()
            "13" -> 0xFF795900.toInt()
            "14" -> 0xFF8C5300.toInt()
            "15" -> 0xFF944A00.toInt()
            "16" -> 0xFF7D524A.toInt()
            "17" -> 0xFF5F6162.toInt()
            "18" -> 0xFF575D7E.toInt()
            else -> 0xFF006874.toInt()
        }
    }
}

fun Context.getColorAttr(@AttrRes resId: Int): Int {
    val typedValue = TypedValue()
    return if (theme.resolveAttribute(resId, typedValue, true)) {
        if (typedValue.resourceId != 0) {
            ContextCompat.getColor(this, typedValue.resourceId)
        } else {
            typedValue.data
        }
    } else {
        Color.TRANSPARENT
    }
}

fun Context.getColorAttr(attrName: String): Int {
    var packageNameToUse = packageName
    var finalAttrName = attrName

    if (attrName.startsWith("android:")) {
        packageNameToUse = "android"
        finalAttrName = attrName.removePrefix("android:")
    }

    val resId = resources.getIdentifier(finalAttrName, "attr", packageNameToUse)
    
    if (resId == 0) return Color.TRANSPARENT

    return getColorAttr(resId)
}

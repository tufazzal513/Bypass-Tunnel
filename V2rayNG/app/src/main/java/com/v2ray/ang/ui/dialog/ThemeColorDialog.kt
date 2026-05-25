package com.v2ray.ang.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.util.ThemeManager
import com.v2ray.ang.util.WindowBlurUtils
import com.v2ray.ang.util.getColorAttr

class ThemeColorDialog : DialogFragment() {

    companion object {
        const val TAG = "ThemeColorDialog"

        val THEME_COLORS = listOf(
            ThemeColor("1",  0xFFBA1A1A.toInt()),
            ThemeColor("2",  0xFFB94073.toInt()),
            ThemeColor("3",  0xFF6750A4.toInt()),
            ThemeColor("4",  0xFF7E42A4.toInt()),
            ThemeColor("5",  0xFF5355A9.toInt()),
            ThemeColor("6",  0xFF335BBC.toInt()),
            ThemeColor("7",  0xFF00639B.toInt()),
            ThemeColor("8",  0xFF006874.toInt()),
            ThemeColor("9",  0xFF006A64.toInt()),
            ThemeColor("10", 0xFF006D39.toInt()),
            ThemeColor("11", 0xFF4A672D.toInt()),
            ThemeColor("12", 0xFF5E6400.toInt()),
            ThemeColor("13", 0xFF795900.toInt()),
            ThemeColor("14", 0xFF8C5300.toInt()),
            ThemeColor("15", 0xFF944A00.toInt()),
            ThemeColor("16", 0xFF7D524A.toInt()),
            ThemeColor("17", 0xFF5F6162.toInt()),
            ThemeColor("18", 0xFF575D7E.toInt()),
        )

        fun show(
            fragmentManager: androidx.fragment.app.FragmentManager,
            onApplied: () -> Unit = {},
        ) {
            val dialog = ThemeColorDialog()
            dialog.onAppliedCallback = onApplied
            dialog.show(fragmentManager, TAG)
        }
    }

    data class ThemeColor(val key: String, @ColorInt val colorInt: Int)

    var onAppliedCallback: () -> Unit = {}

    override fun onStart() {
        super.onStart()
        WindowBlurUtils.applyWindowBlur(dialog?.window)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_theme_color, null)

        val grid = view.findViewById<android.widget.GridLayout>(R.id.grid_theme_colors)

        val useCustom   = MmkvManager.decodeSettingsBool(AppConfig.PREF_USE_CUSTOM_COLOR, false)
        val savedColor  = MmkvManager.decodeSettingsInt(AppConfig.PREF_CUSTOM_COLOR, 0)
        val currentKey  = MmkvManager.decodeSettingsString(AppConfig.PREF_APP_THEME) ?: "2"

        THEME_COLORS.forEach { themeColor ->
            val itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_theme_color, grid, false)

            val circle = itemView.findViewById<ImageView>(R.id.iv_color_circle)
            val check  = itemView.findViewById<ImageView>(R.id.iv_check)

            val isSelected = !useCustom && themeColor.key == currentKey
            
            val rawSeedColor = themeColor.colorInt
            
            val options = DynamicColorsOptions.Builder()
                .setContentBasedSource(rawSeedColor)
                .build()
            
            val wrappedContext = DynamicColors.wrapContextIfAvailable(requireContext(), options)
            val m3PrimaryColor = wrappedContext.getColorAttr(android.R.attr.colorPrimary)

            applyCircleDrawable(circle, m3PrimaryColor, isSelected)
            
            check.visibility = if (isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                activity?.let { act -> ThemeManager.setAndSaveTheme(act, themeColor.key) }
                onAppliedCallback()
                dismiss()
            }

            grid.addView(itemView)
        }

        val customItemView = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_theme_color, grid, false)

        val customCircle = customItemView.findViewById<ImageView>(R.id.iv_color_circle)
        val customIcon   = customItemView.findViewById<ImageView>(R.id.iv_check)

        val isCustomSelected = useCustom && savedColor != 0
        val rawCustomColor   = if (savedColor != 0) savedColor else Color.parseColor("#6750A4")
        
        val customOptions = DynamicColorsOptions.Builder()
            .setContentBasedSource(rawCustomColor)
            .build()
        val wrappedCustomContext = DynamicColors.wrapContextIfAvailable(requireContext(), customOptions)
        val m3CustomPrimary = wrappedCustomContext.getColorAttr(android.R.attr.colorPrimary)

        applyCircleDrawable(customCircle, m3CustomPrimary, isCustomSelected)

        customIcon.setImageResource(R.drawable.ic_pencil)
        customIcon.visibility = View.VISIBLE
        
        val lum = calculateLuminance(m3CustomPrimary)
        customIcon.setColorFilter(if (lum > 0.4f) Color.BLACK else Color.WHITE)

        customItemView.setOnClickListener {
            dismiss()
            CustomColorPickerDialog.show(
                parentFragmentManager,
                currentColor = rawCustomColor,
                onApplied   = onAppliedCallback,
            )
        }

        grid.addView(customItemView)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.pref_theme_color_title)
            .setView(view)
            .setNegativeButton(android.R.string.cancel) { _, _ -> dismiss() }
            .create()
    }

    private fun applyCircleDrawable(view: ImageView, @ColorInt color: Int, selected: Boolean) {
        val drawable = GradientDrawable().apply {
            shape        = GradientDrawable.RECTANGLE
            cornerRadius = if (selected) dpToPx(16).toFloat() else dpToPx(100).toFloat()
            setColor(color)
        }
        view.background = drawable
    }

    private fun calculateLuminance(@ColorInt color: Int): Float {
        val r = Color.red(color)   / 255f
        val g = Color.green(color) / 255f
        val b = Color.blue(color)  / 255f
        return 0.2126f * r + 0.7152f * g + 0.0722f * b
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()
}

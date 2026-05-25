package com.v2ray.ang.ui.bottomsheet

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.v2ray.ang.util.WindowBlurUtils

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onStart() {
        super.onStart()
        val sheetDialog = dialog as? BottomSheetDialog ?: return

        WindowBlurUtils.applyWindowBlur(sheetDialog.window)
        val bottomSheet = sheetDialog.findViewById<android.view.View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        sheetDialog.behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
        }

        ViewCompat.setOnApplyWindowInsetsListener(bottomSheet) { view, insets ->
            val statusBarInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val screenHeight = view.resources.displayMetrics.heightPixels
            val margin = (8 * view.resources.displayMetrics.density).toInt()

            sheetDialog.behavior.maxHeight = screenHeight - statusBarInset - margin

            insets
        }
    }
}

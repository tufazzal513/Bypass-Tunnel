package com.v2ray.ang.ui.preference

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.preference.PreferenceFragmentCompat

abstract class BasePreferenceFragment : PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyBottomInsetToListView()
    }

    private fun applyBottomInsetToListView() {
        val rv = listView ?: return
        
        rv.clipToPadding = false

        ViewCompat.setOnApplyWindowInsetsListener(rv) { v, insets ->
            val bars   = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            
            val bottomInset = maxOf(bars.bottom, cutout.bottom)

            v.updatePadding(bottom = bottomInset)
            insets
        }
    }
}

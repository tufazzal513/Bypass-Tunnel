package com.neko.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.v2ray.ang.AppConfig
import com.v2ray.ang.R
import com.v2ray.ang.handler.MmkvManager

class UwuHeaderIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val styleChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            applyStyle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        applyStyle()
        ContextCompat.registerReceiver(
            context,
            styleChangeReceiver,
            IntentFilter(AppConfig.BROADCAST_ACTION_CATEGORY_STYLE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try { context.unregisterReceiver(styleChangeReceiver) } catch (_: Exception) {}
    }

    fun applyStyle() {
        val style = MmkvManager.decodeSettingsString(AppConfig.PREF_CATEGORY_STYLE, "style1")
        setImageResource(drawableForStyle(style))
    }

    companion object {
        fun drawableForStyle(style: String?): Int = when (style) {
            "miku2"  -> R.drawable.uwu_icon_miku_2
            "teto"  -> R.drawable.uwu_icon_teto
            "teto2"  -> R.drawable.uwu_icon_teto_2
            "neru"  -> R.drawable.uwu_icon_neru
            else      -> R.drawable.uwu_icon_miku
        }
    }
}

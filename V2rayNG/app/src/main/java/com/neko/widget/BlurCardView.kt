package com.neko.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import com.google.android.material.card.MaterialCardView
import com.qmdeve.blurview.widget.BlurView

class BlurCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.materialCardViewStyle
) : MaterialCardView(context, attrs, defStyleAttr) {

    init {
        setCardBackgroundColor(Color.TRANSPARENT)
        cardElevation = 0f
        strokeWidth = 0
        
        val blurView = BlurView(context, attrs).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        
        addView(blurView, 0)
    }
}

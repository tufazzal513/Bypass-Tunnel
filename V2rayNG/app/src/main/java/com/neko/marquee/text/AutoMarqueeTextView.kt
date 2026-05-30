package com.neko.marquee.text

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class AutoMarqueeTextView : AppCompatTextView {
    private var mAggregatedVisible: Boolean = false
    private var mMarqueePosition: Int = 0

    constructor(context: Context) : super(context) {
        mAggregatedVisible = false
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mAggregatedVisible = false
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mAggregatedVisible = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isSelected = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isSelected = false
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        onVisibilityAggregated(isVisibleToUser())
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (isVisible == mAggregatedVisible) return
        mAggregatedVisible = isVisible
        if (mAggregatedVisible) {
            ellipsize = TextUtils.TruncateAt.MARQUEE
            post {
                scrollTo(mMarqueePosition, 0)
                isSelected = true
            }
        } else {
            mMarqueePosition = scrollX
            ellipsize = TextUtils.TruncateAt.END
            isSelected = false
        }
    }

    fun View.isVisibleToUser(): Boolean {
        return this.visibility == View.VISIBLE
    }
}
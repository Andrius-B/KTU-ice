package com.ice.ktuice.ui.main.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Created by Andrius on 3/4/2018.
 * A ViewPager with the scroll-between views funcitonality being optional
 */
class NoScrollViewPager(context: Context, attributeSet: AttributeSet): ViewPager(context, attributeSet) {

    private var isPagingEnabled = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isPagingEnabled && super.onTouchEvent(ev)
    }

    override fun performClick(): Boolean {
        return isPagingEnabled &&super.performClick()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isPagingEnabled && super.onInterceptTouchEvent(ev)
    }

}
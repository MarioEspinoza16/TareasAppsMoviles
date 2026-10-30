package com.marioespinoza.mibalance.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import org.osmdroid.views.MapView

/** Keeps map gestures inside the map while retaining an accessibility click action. */
class ExpenseMapView(context: Context, attributes: AttributeSet?) : MapView(context, attributes) {
    override fun onTouchEvent(event: MotionEvent): Boolean {
        parent?.requestDisallowInterceptTouchEvent(true)
        if (event.action == MotionEvent.ACTION_UP) performClick()
        return super.onTouchEvent(event)
    }
    override fun performClick(): Boolean { super.performClick(); return true }
}

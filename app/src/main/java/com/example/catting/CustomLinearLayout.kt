package com.example.catting

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class CustomLinearLayout:LinearLayout {
    constructor(context: Context): super(context) { }
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) { }
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int): super(context,attributeSet,defStyleAttr) { }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}
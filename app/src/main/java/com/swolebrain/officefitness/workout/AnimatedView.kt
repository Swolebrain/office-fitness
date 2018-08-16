package com.swolebrain.officefitness.workout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView
import com.swolebrain.officefitness.R

class AnimatedView(c: Context, attrs: AttributeSet, defStyle: Int) : ImageView(c, attrs, defStyle){
    private var ticks = 0
    private var ticksPerUpdate = 15
    private var drawable = R.drawable.badge_workout_complete
    init {
        val xmlProvidedSize = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "textSize")
        if (xmlProvidedSize !== null) this.ticksPerUpdate = xmlProvidedSize.toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        ticks++
        if (ticks >= ticksPerUpdate){
            ticks = 0
//            this.setImageDrawable()
        }
        super.onDraw(canvas)
    }
}
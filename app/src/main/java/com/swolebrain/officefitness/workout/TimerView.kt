package com.swolebrain.officefitness.workout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView

class TimerView(c: Context, attrs: AttributeSet) : TextView(c, attrs){
    private var startTime: Long = System.currentTimeMillis()
    override fun onDraw(canvas: Canvas?) {
        var currentTime : Long = System.currentTimeMillis() - startTime
        var currentTimeSeconds : Long = currentTime/1000
        var seconds : String =  ("0" + (currentTimeSeconds%60).toString()).takeLast(2)
        text = (currentTimeSeconds/60).toString() + ":" + seconds + "." + ( (currentTime % 1000)/100 ).toString()
        super.onDraw(canvas)
        invalidate()
    }

    fun resetCounter() {
        startTime = System.currentTimeMillis()
    }
}
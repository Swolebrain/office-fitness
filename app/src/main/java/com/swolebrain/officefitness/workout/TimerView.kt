package com.swolebrain.officefitness.workout

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.TextView
import com.swolebrain.officefitness.DrawerMenuActivity

class TimerView(c: Context, attrs: AttributeSet) : TextView(c, attrs){
    private var startTime: Long = System.currentTimeMillis()
    var durationSeconds: Int = 3
    private var drawerMenuActivity : DrawerMenuActivity? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        getActivity()
    }

    override fun onDraw(canvas: Canvas?) {
        var currentTime : Long = System.currentTimeMillis() - startTime
        var currentTimeSeconds : Long = when (durationSeconds){
            0 -> currentTime/1000
            else -> durationSeconds - currentTime/1000 - 1
        }

        if (durationSeconds != 0 && currentTimeSeconds <= 0L && drawerMenuActivity != null){
            drawerMenuActivity?.selectFragment(WorkoutCTAFragment())
        }

        val minutes : String = (currentTimeSeconds/60).toString()
        val seconds : String =  ("0" + (currentTimeSeconds%60).toString()).takeLast(2)
        val ms: String = when (durationSeconds){
            0 -> ( (currentTime % 1000)/100 ).toString().take(1)
            else -> ( (1000 - (currentTime % 1000))/100 ).toString().take(1)
        }

        text = minutes + ":" + seconds + "." + ms
        super.onDraw(canvas)
        invalidate()
    }

    fun getActivity() : DrawerMenuActivity {
        val dma = drawerMenuActivity
        if (dma != null) return dma

        var c : Context = context
        while (c is ContextWrapper ){
            if (c is DrawerMenuActivity){
                drawerMenuActivity = c;
                return c;
            }
            c = c.baseContext
        }
        throw Exception("Error while trying to get activity")
    }
}
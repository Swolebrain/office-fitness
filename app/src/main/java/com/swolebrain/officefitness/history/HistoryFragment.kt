package com.swolebrain.officefitness.history

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.QuerySnapshot
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.getLast7DaysWorkout
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel
import com.swolebrain.officefitness.repositories.getWorkoutLogs
import kotlinx.android.synthetic.main.fragment_history.*
import java.util.*

/**
 * Created by marjv on 7/6/2018.
 */
class HistoryFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getLast7DaysWorkout()
        getWorkoutLogs().observe(
                this,
                Observer {
                    setContents(it)
                }
        )
        return inflater.inflate(R.layout.fragment_history, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Workout History"
        history_week_graph.viewport.setMaxX(7.5)
        history_week_graph.title = "Reps per Day"
        history_week_graph.gridLabelRenderer.labelFormatter = WeekGraphLabelFormatter()
        history_week_graph.gridLabelRenderer.horizontalAxisTitle = "Day of Week"
        history_week_graph.gridLabelRenderer.numHorizontalLabels = 7
        history_week_graph.gridLabelRenderer.setHorizontalLabelsAngle(-45)
    }



    fun setContents(querySnapshot: QuerySnapshot?){
        var totalActivitySeconds : Long = 0
        var totalReps : Long = 0
        var dataPoints : MutableList<DataPoint> = mutableListOf()
        var repsPerDayMap : MutableMap<Int, Long> = mutableMapOf(
                0 to 0L,
                1 to 0L,
                2 to 0L,
                3 to 0L,
                4 to 0L,
                5 to 0L,
                6 to 0L
        )

        querySnapshot?.forEach {
            totalActivitySeconds += it.get("durationSeconds") as Long
            totalReps += it.get("reps") as Long
            var numDaysAgo : Long = (System.currentTimeMillis() - (it.get("timestamp") as Long)) / (1000L*60*60*24)
            repsPerDayMap[numDaysAgo.toInt()] = repsPerDayMap[numDaysAgo.toInt()]!!.plus(it.get("reps") as Long)
        }

        Log.d("####", repsPerDayMap.toString())

        repsPerDayMap.forEach { k, v ->
            dataPoints.add(DataPoint(6.0 - k, v.toDouble()))
        }
        dataPoints.reverse()

        history_week_graph.removeAllSeries()
        history_week_graph.addSeries(
                LineGraphSeries<DataPoint>(dataPoints.toTypedArray())
        )

        tv_lastweek_reps.text = totalReps.toString()
        tv_lastweek_activity.text = WorkoutProgressViewModel.workoutProgress.getTimeSpent(totalActivitySeconds)
        history_overlay.visibility = View.GONE
        history_progress_bar.visibility = View.GONE
    }


    class WeekGraphLabelFormatter : DefaultLabelFormatter(){
        override fun formatLabel(value: Double, isValueX: Boolean): String {
            if (!isValueX)
                return super.formatLabel(value, isValueX)

            val labels = generateDateMap(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
            if (value.toInt() < labels.size && value.toInt() >= 0)
                return labels[value.toInt()]

            return ""
        }

        private fun generateDateMap(dayOfWeek: Int): List<String> {
            val currentDay = dayOfWeek - 1
            val labels =  listOf("Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun")
            val shift = labels.take(currentDay)
            return labels.takeLast(7-currentDay) + shift
        }
    }
}

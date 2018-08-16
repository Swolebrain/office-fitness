package com.swolebrain.officefitness.history

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.QuerySnapshot
import com.jjoe64.graphview.series.DataPoint
import com.swolebrain.officefitness.DrawerMenuActivity
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.getLast7DaysWorkout
import com.swolebrain.officefitness.repositories.WorkoutLog
import com.swolebrain.officefitness.repositories.getWorkoutLogs
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Created by marjv on 7/6/2018.
 */
class HistoryFragment : Fragment() {
    companion object {
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
    }

    fun reset(){
        totalActivitySeconds = 0
        totalReps = 0
        dataPoints = mutableListOf()
        repsPerDayMap = mutableMapOf(
                0 to 0L,
                1 to 0L,
                2 to 0L,
                3 to 0L,
                4 to 0L,
                5 to 0L,
                6 to 0L
        )
    }

    var workoutLogs = mutableListOf<WorkoutLog>()
    lateinit var historyRVAdapter : HistoryRVAdapter

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
        rv_history.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        val drawerMenuActivity : DrawerMenuActivity = activity as DrawerMenuActivity
        historyRVAdapter = HistoryRVAdapter(drawerMenuActivity, workoutLogs)
        rv_history.adapter = historyRVAdapter
        activity?.title = "Workout History"
    }



    private fun setContents(querySnapshot: QuerySnapshot?){
        val tonightMidnightTime = getUTCMidnightTimeMillis()
        if (dataPoints.size > 0) reset()
        querySnapshot?.forEach workoutLoop@{
            val timeStampGMT =  it.get("timestamp") as Long
            val log = WorkoutLog(it.get("durationSeconds") as Long, it.get("exercise") as String, it.get("reps") as Long, it.get("sets") as Long, timeStampGMT)
            totalActivitySeconds += log.durationSeconds
            totalReps += log.reps
            var numDaysAgo : Long = (tonightMidnightTime - log.timeStamp) / (1000L*60*60*24)
            Log.d("####", """Reps: ${log.reps}
                |NumDaysAgo: ${numDaysAgo.toString(10)}
                | db time stamp: ${it.get("timestamp")}
                | currentTimeStamp: ${System.currentTimeMillis()} offset ${getGMTOffset()}
                | tonightMidnightTime: $tonightMidnightTime
                | numDaysAgo: ${numDaysAgo}
            """.trimMargin())
            var dayKey = numDaysAgo.toInt()
            if (dayKey < 0 || dayKey >= 7) return@workoutLoop
            repsPerDayMap[dayKey] = repsPerDayMap[dayKey]!!.plus(log.reps)
            workoutLogs.add(log)
        }

        repsPerDayMap.forEach { k, v ->
            dataPoints.add(DataPoint(6.0 - k, v.toDouble()))
        }
        dataPoints.reverse()

        historyRVAdapter.notifyDataSetChanged()

        history_overlay.visibility = View.GONE
        history_progress_bar.visibility = View.GONE
    }


}
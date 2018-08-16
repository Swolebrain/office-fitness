package com.swolebrain.officefitness.repositories

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.QuerySnapshot
import com.jjoe64.graphview.series.DataPoint

private val workoutLogs :  MutableLiveData<QuerySnapshot> = MutableLiveData()

fun getWorkoutLogs(): MutableLiveData<QuerySnapshot> {
    return workoutLogs
}

fun loadWorkoutLogs(q: QuerySnapshot){
    workoutLogs.value = q
}

data class WorkoutLog(
        var durationSeconds: Long = 0,
        var exercise: String = "",
        var reps: Long = 0,
        var sets : Long = 0,
        var timeStamp: Long = 0
    )
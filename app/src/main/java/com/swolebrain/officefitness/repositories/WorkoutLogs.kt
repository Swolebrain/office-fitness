package com.swolebrain.officefitness.repositories

import android.arch.lifecycle.MutableLiveData
import com.google.firebase.firestore.QuerySnapshot

private val workoutLogs :  MutableLiveData<QuerySnapshot> = MutableLiveData()

fun getWorkoutLogs(): MutableLiveData<QuerySnapshot> {
    return workoutLogs
}

fun loadWorkoutLogs(q: QuerySnapshot){
    workoutLogs.value = q
}
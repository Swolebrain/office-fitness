package com.swolebrain.officefitness

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.swolebrain.officefitness.repositories.ExerciseViewModel
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel
import com.swolebrain.officefitness.repositories.loadWorkoutLogs

const val RC_SIGN_IN : Int = 123
val providers = listOf(
        AuthUI.IdpConfig.FacebookBuilder().setPermissions(mutableListOf("email", "public_profile")).build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.EmailBuilder().build()
//            AuthUI.IdpConfig.TwitterBuilder().build(),
)

fun recordCompletedWorkout(secondsElapsed: Long){
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return
    if (WorkoutProgressViewModel.workoutProgress.repsCompleted <= 0) return

    val workoutLogsRef = db.collection("users")
            .document(currentUserAuth.uid)
            .collection("workoutlogs")

    workoutLogsRef.add(mutableMapOf(
            "timestamp" to System.currentTimeMillis(),
            "reps" to WorkoutProgressViewModel.workoutProgress.repsCompleted,
            "sets" to WorkoutProgressViewModel.workoutProgress.setsCompleted,
            "exercise" to ExerciseViewModel.workoutConfig.exerciseName,
            "durationSeconds" to secondsElapsed
    ))
}

fun getLast7DaysWorkout(){
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return

    val workoutLogsRef = db.collection("users")
            .document(currentUserAuth.uid)
            .collection("workoutlogs")

    workoutLogsRef.whereGreaterThanOrEqualTo("timestamp", System.currentTimeMillis() - 7 * 24 * 60 *60 * 1000)
            .get()
            .addOnCompleteListener{
                if (!it.isSuccessful){
                    Log.d("####", "Error retrieving firebase workouts")
                    return@addOnCompleteListener
                }
                Log.d("####", "Retrieved workout logs from firebase")
                loadWorkoutLogs(it.result)

            }
}

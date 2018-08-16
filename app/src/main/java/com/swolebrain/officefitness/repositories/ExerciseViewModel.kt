package com.swolebrain.officefitness.repositories

import android.util.Log
import com.swolebrain.officefitness.R

val animationsCorrespondingToExercises = mapOf(
        "Air Squats" to R.drawable.anim_squat_male,
        "Calf Raises" to R.drawable.anim_calf_raises_male,
        "Pushups" to -1,
        "Butt Kicks" to -1,
        "High Knees" to -1,
        "Chair Crunches" to -1,
        "Burpees" to -1
)

data class ExerciseViewModel(
        var exerciseName: String,
        var timeInterval: Int,
        var repetitions: Int,
        var exerciseCTA:String = exerciseName){

    var exerciseAnimationResource = 1
    get(){
//        Log.d("####", "Exercise Name:" + exerciseName)
        return if (animationsCorrespondingToExercises[exerciseName] != null) animationsCorrespondingToExercises[exerciseName]!! else -1
    }

    companion object {
        var workoutConfig = ExerciseViewModel("", -1, -1)
        private set
        val exerciseNames = listOf(
                "Air Squats",
                "Calf Raises",
                "Pushups",
                "Butt Kicks",
                "High Knees",
                "Chair Crunches",
                "Burpees"
        )

        val exerciseIntervals = mapOf(
                "10s" to 10, "20s" to 20, "30s" to 30, "40s" to 40, "50s" to 50, "60s" to 60,
                "1.5min" to 90, "2min" to 120, "2.5min" to 150, "3min" to 180, "3.5min" to 210,
                "4min" to 240, "4.5min" to 270, "5min" to 300
        )

        val exerciseReps = IntRange(1,20).map{ it.toString() }
    }
}


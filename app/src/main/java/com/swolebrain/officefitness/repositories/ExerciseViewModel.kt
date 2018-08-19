package com.swolebrain.officefitness.repositories

import com.swolebrain.officefitness.R

val animationsCorrespondingToExercisesMale = mapOf(
        "Air Squats" to R.drawable.anim_squat_male,
        "Calf Raises" to R.drawable.anim_calf_raises_male,
        "Pushups" to R.drawable.anim_pushup_male,
        "Butt Kicks" to -1,
        "High Knees" to -1,
        "Chair Crunches" to -1,
        "Burpees" to -1
)
val animationsCorrespondingToExercisesFemale = mapOf(
        "Air Squats" to R.drawable.anim_squat_female,
        "Calf Raises" to R.drawable.anim_calf_raises_female,
        "Pushups" to R.drawable.anim_pushup_female,
        "Butt Kicks" to R.drawable.anim_butt_kicks_female,
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
        val userProfile = getUserProfileData().value
        var gender = "male"
        if (userProfile != null && userProfile.gender == "female") gender = "female"
        return when (gender) {
            "female" -> if (animationsCorrespondingToExercisesFemale[exerciseName] != null) animationsCorrespondingToExercisesFemale[exerciseName]!! else -1
            else -> if (animationsCorrespondingToExercisesMale[exerciseName] != null) animationsCorrespondingToExercisesMale[exerciseName]!! else -1
        }
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
                "4min" to 240, "4.5min" to 270, "5min" to 300, "6min" to 360, "7min" to 420,
                "8min" to 480, "9min" to 540, "10min" to 600
        )

        val exerciseReps = IntRange(1,20).map{ it.toString() }
    }
}


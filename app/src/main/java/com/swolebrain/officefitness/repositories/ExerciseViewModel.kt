package com.swolebrain.officefitness.repositories

data class ExerciseViewModel(
        var exerciseName: String,
        var timeInterval: Int?,
        var repetitions: Int?,
        var exerciseCTA:String = exerciseName){

    companion object {
        var workoutConfig = ExerciseViewModel("", -1, -1)
        private set

    }
}
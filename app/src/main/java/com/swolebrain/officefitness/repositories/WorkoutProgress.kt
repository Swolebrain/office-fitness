package com.swolebrain.officefitness.repositories

data class WorkoutProgressViewModel(var repsCompleted:Int, var setsCompleted: Int,
                                    var startTime: Long = System.currentTimeMillis()){
    companion object {
        var workoutProgress = WorkoutProgressViewModel(0, 0)
        private set
    }
}
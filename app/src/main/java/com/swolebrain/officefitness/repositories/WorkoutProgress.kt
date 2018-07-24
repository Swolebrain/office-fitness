package com.swolebrain.officefitness.repositories

data class WorkoutProgressViewModel(var repsCompleted:Int, var setsCompleted: Int,
                                    var startTime: Long = System.currentTimeMillis()){

    fun getTimeSpent(elapsedTime:Long): String {
        val elapsedSeconds : String = ("0" + (elapsedTime % 60)).takeLast(2)
        val elapsedMinutes : String = ("0" + ((elapsedTime / 60) % 60)).takeLast(2)
        val elapsedHours: String = "" + (elapsedTime / 3600)
        return "$elapsedHours:$elapsedMinutes:$elapsedSeconds"
    }

    companion object {
        var workoutProgress = WorkoutProgressViewModel(0, 0)
        private set
    }
}
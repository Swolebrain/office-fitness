package com.swolebrain.officefitness.workout

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.DrawerMenuActivity
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.recordCompletedWorkout
import com.swolebrain.officefitness.repositories.ExerciseViewModel
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel.Companion.workoutProgress
import kotlinx.android.synthetic.main.fragment_workout_results.*


class WorkoutResultsFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_workout_results, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mainActivity = activity as DrawerMenuActivity
        mainActivity.title = "Workout Results"
        tv_exercise_name.text = ExerciseViewModel.workoutConfig.exerciseName
        tv_reps_performed.text = WorkoutProgressViewModel.workoutProgress.repsCompleted.toString()
        tv_sets_performed.text = WorkoutProgressViewModel.workoutProgress.setsCompleted.toString()
        val elapsedTime : Long = (System.currentTimeMillis() - WorkoutProgressViewModel.workoutProgress.startTime)/1000
        val elapsedTimeStr = workoutProgress.getTimeSpent(elapsedTime)
        tv_time_elapsed.text = elapsedTimeStr
        recordCompletedWorkout(elapsedTime)
    }
}
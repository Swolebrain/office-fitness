package com.swolebrain.officefitness.workout

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.repositories.ExerciseViewModel
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel
import kotlinx.android.synthetic.main.fragment_workout_results.*


class WorkoutResultsFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_workout_results, container, false)

    override fun onResume() {
        tv_exercise_name.text = ExerciseViewModel.workoutConfig.exerciseName
        tv_reps_performed.text = WorkoutProgressViewModel.workoutProgress.repsCompleted.toString()
        Log.d("Vic", WorkoutProgressViewModel.workoutProgress.startTime.toString())
        tv_time_elapsed.text = ((System.currentTimeMillis() - WorkoutProgressViewModel.workoutProgress.startTime)/1000).toString() + " Seconds"
        super.onResume()
    }
}
package com.swolebrain.officefitness.workout

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.DrawerMenuActivity
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.repositories.ExerciseViewModel
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel
import kotlinx.android.synthetic.main.fragment_workout_progress.*

/**
 * Created by marjv on 7/6/2018.
 */
class WorkoutProgressFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        WorkoutProgressViewModel.workoutProgress.startTime = System.currentTimeMillis()
        return inflater.inflate(R.layout.fragment_workout_progress, container, false)
    }

    override fun onResume() {
        super.onResume()
        activity?.title = ExerciseViewModel.workoutConfig.exerciseName
        btn_finish_workout.setOnClickListener { e ->
            val drawerActivity = activity as DrawerMenuActivity
            drawerActivity?.selectFragment(WorkoutResultsFragment())
        }
    }
}
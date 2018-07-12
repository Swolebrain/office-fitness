package com.swolebrain.officefitness.workout

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.repositories.ExerciseViewModel
import kotlinx.android.synthetic.main.fragment_start_workout.*


/**
 * Created by marjv on 7/6/2018.
 */

public class StartWorkoutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_start_workout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Office Fitness"
        configurePickers()
    }

    fun configurePickers() {
        val selectedEx = ExerciseViewModel.workoutConfig;
        picker_select_exercise.minValue = 0
        picker_select_exercise.maxValue = 4
        picker_select_exercise.displayedValues = exerciseNames.toTypedArray()
        picker_select_exercise.value = if (selectedEx.exerciseName.length > 0) exerciseNames.indexOf(selectedEx.exerciseName) else 0

        picker_select_interval.minValue = 0
        picker_select_interval.maxValue = 11
//        val intervalValue = if (selectedEx.timeInterval >= 0) exerciseIntervals.values.toTypedArray().indexOf(selectedEx.timeInterval) else 7
//        picker_select_interval.value = exerciseIntervals.filter { (k,v) -> return v == intervalValue }
        picker_select_interval.value = 7
        picker_select_interval.displayedValues = exerciseIntervals.keys.toTypedArray()

        picker_select_reps.minValue = 0
        picker_select_reps.maxValue = 19
        picker_select_reps.value = if (selectedEx.repetitions != -1 ) exerciseReps.map{it.toInt()}.indexOf(selectedEx.repetitions) else 4
        picker_select_reps.displayedValues = exerciseReps.toTypedArray()
    }

    companion object {
        val exerciseNames = listOf(
                "Air Squats",
                "Calf Raises",
                "Burpees",
                "Pushups",
                "Other"
        )

        val exerciseIntervals = mapOf(
                "10s" to 10, "20s" to 20, "30s" to 30, "40s" to 40, "50s" to 50, "60s" to 60,
                "1.5min" to 90, "2min" to 120, "2.5min" to 150, "3min" to 180, "3.5min" to 210,
                "4min" to 240, "4.5min" to 270, "5min" to 300
        )

        val exerciseReps = IntRange(1,20).map{ it.toString() }
    }
}
package com.swolebrain.officefitness.workout

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.repositories.ExerciseViewModel
import com.swolebrain.officefitness.repositories.ExerciseViewModel.Companion.exerciseIntervals
import com.swolebrain.officefitness.repositories.ExerciseViewModel.Companion.exerciseNames
import com.swolebrain.officefitness.repositories.ExerciseViewModel.Companion.exerciseReps
import kotlinx.android.synthetic.main.fragment_start_workout.*


/**
 * Created by marjv on 7/6/2018.
 */

class WorkoutConfigurationFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_start_workout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Office Fitness"
        configurePickers()
    }

    fun configurePickers() {
        val selectedEx = ExerciseViewModel.workoutConfig
        //EXERCISE PICKER
        //min value, max value, and labels
        picker_select_exercise.minValue = 0
        picker_select_exercise.maxValue = ExerciseViewModel.exerciseNames.size -1
        picker_select_exercise.displayedValues = exerciseNames.toTypedArray()
        //sync picker with data model
        if (selectedEx.exerciseName.isNotEmpty()){
            picker_select_exercise.value =  exerciseNames.indexOf(selectedEx.exerciseName)
        }
        else {
            picker_select_exercise.value =  0
            ExerciseViewModel.workoutConfig.exerciseName = exerciseNames[0]
        }
        //value channge listener
        picker_select_exercise.setOnValueChangedListener{ _: NumberPicker, _: Int, newVal : Int ->
            ExerciseViewModel.workoutConfig.exerciseName = exerciseNames[newVal]

        }

        //TIME INTERVAL
        //min value, max value, and labels
        picker_select_interval.minValue = 0
        val timeIntervalLabels = exerciseIntervals.keys.toTypedArray()
        picker_select_interval.maxValue = timeIntervalLabels.size - 1
        picker_select_interval.displayedValues = timeIntervalLabels
        //sync picker with data model
        val previousSelectedInterval : Int = ExerciseViewModel.workoutConfig.timeInterval
        when(previousSelectedInterval){
            -1 -> {
                picker_select_interval.value = 7
                ExerciseViewModel.workoutConfig.timeInterval = 120
            }
            else -> {
                val selectedKey : String? = exerciseIntervals.entries.associateBy({ it.value}) {it.key}[previousSelectedInterval]
                exerciseIntervals.keys.forEachIndexed { index, key ->
                    if (key == selectedKey) picker_select_interval.value = index
                }
            }
        }

        //value change listener
        picker_select_interval.setOnValueChangedListener{ np : NumberPicker, oldVal : Int, newVal : Int ->
            val selectedKey : String = exerciseIntervals.keys.toTypedArray()[newVal]
            ExerciseViewModel.workoutConfig.timeInterval= when(exerciseIntervals[selectedKey]){
                null -> 0
                else -> exerciseIntervals[selectedKey] as Int
            }
        }

        //REP SELECTION
        //min value, max value, and labels
        picker_select_reps.minValue = 0
        picker_select_reps.maxValue = 19
        picker_select_reps.displayedValues = exerciseReps.toTypedArray()
        //sync picker with data model
        if (selectedEx.repetitions == -1){
            picker_select_reps.value = 4
            ExerciseViewModel.workoutConfig.repetitions = 5
        }
        else {
            picker_select_reps.value = exerciseReps.map{it.toInt()}.indexOf(selectedEx.repetitions)
        }

        picker_select_reps.setOnValueChangedListener{ np: NumberPicker, oldVal: Int, newVal: Int ->
            ExerciseViewModel.workoutConfig.repetitions = newVal + 1
        }
    }
}
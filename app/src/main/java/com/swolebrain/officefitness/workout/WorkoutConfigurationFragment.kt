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
        //min value, max value, and labels
        picker_select_exercise.minValue = 0
        picker_select_exercise.maxValue = 4
        picker_select_exercise.displayedValues = exerciseNames.toTypedArray()
        //sync picker with data model
        if (selectedEx.exerciseName.length > 0){
            picker_select_exercise.value =  exerciseNames.indexOf(selectedEx.exerciseName)
        }
        else {
            picker_select_exercise.value =  0
            ExerciseViewModel.workoutConfig.exerciseName = exerciseNames[0]
        }
        //value chnange listener
        picker_select_exercise.setOnValueChangedListener({ np : NumberPicker, oldVal : Int, newVal : Int ->
            ExerciseViewModel.workoutConfig.exerciseName = exerciseNames[newVal]
            Log.d("####PICKER CHANGED", ""+oldVal +" " +newVal + " - " + exerciseNames[newVal] + " - " + ExerciseViewModel.workoutConfig.exerciseName)
        })

        //min value, max value, and labels
        picker_select_interval.minValue = 0
        picker_select_interval.maxValue = 11
        picker_select_interval.displayedValues = exerciseIntervals.keys.toTypedArray()
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
        picker_select_interval.setOnValueChangedListener({ np : NumberPicker, oldVal : Int, newVal : Int ->
            val selectedKey : String = exerciseIntervals.keys.toTypedArray()[newVal]
            ExerciseViewModel.workoutConfig.timeInterval= when(exerciseIntervals[selectedKey]){
                null -> 0
                else -> exerciseIntervals[selectedKey] as Int
            }
        })

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

        picker_select_reps.setOnValueChangedListener({ np: NumberPicker, oldVal: Int, newVal: Int ->
            ExerciseViewModel.workoutConfig.repetitions = newVal + 1
        })
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
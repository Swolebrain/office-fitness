package com.swolebrain.officefitness.workout

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.DrawerMenuActivity
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.repositories.ExerciseViewModel
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel
import kotlinx.android.synthetic.main.drawer_menu_main_layout.*
import kotlinx.android.synthetic.main.fragment_workout_cta.*

/**
 * Created by marjv on 7/6/2018.
 */
class WorkoutCTAFragment : Fragment() {
    private lateinit var gradientAnimation : AnimationDrawable
    private lateinit var exerciseInstructionAnimation : AnimationDrawable
    private lateinit var vibrator : Vibrator
    private val vibratorPattern: LongArray = longArrayOf(0L, 200L, 200L, 200L, 200L, 800L, 400L)
    private val vibrationAmplitudes : IntArray = intArrayOf(0, 255, 0, 255, 0, 255, 0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainActivity : DrawerMenuActivity = activity as DrawerMenuActivity
        mainActivity.toolbar.visibility = View.GONE
        return inflater.inflate(R.layout.fragment_workout_cta, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_timer.durationSeconds = 0
        val mainActivity = activity as DrawerMenuActivity

        btn_skip_set.setOnClickListener {e ->
            mainActivity.selectFragment(WorkoutProgressFragment())
        }
        btn_reps_done.setOnClickListener { e ->
            mainActivity.recordCompletedSet()
        }

        gradientAnimation = this.container_workout_cta.background as AnimationDrawable
        gradientAnimation.setEnterFadeDuration(400)
        gradientAnimation.setExitFadeDuration(400)

        vibrator = mainActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

//        Log.d("####", "Resource ID: " + ExerciseViewModel.workoutConfig.exerciseAnimationResource!! + " - should be: " + R.drawable.anim_squat_male + " - exercise: " + ExerciseViewModel.workoutConfig.exerciseName)
        val selectedExerciseAnimation = ExerciseViewModel.workoutConfig.exerciseAnimationResource!!
        if (selectedExerciseAnimation == -1){
            iv_exercise_instruction_animation.visibility = View.GONE
        }
        else {
            exerciseInstructionAnimation = ContextCompat.getDrawable(mainActivity, selectedExerciseAnimation) as AnimationDrawable
            iv_exercise_instruction_animation.setImageDrawable(exerciseInstructionAnimation)
            iv_exercise_instruction_animation.post{
                exerciseInstructionAnimation.start()
            }
            iv_exercise_instruction_animation.visibility = View.VISIBLE
        }
    }


    override fun onResume() {
        super.onResume()
        val act = activity as DrawerMenuActivity
        if (act != null) act.title = ""
        tv_reps_reminder.text = "" + ExerciseViewModel.workoutConfig.repetitions + " reps"
        tv_workout_cta.text = ExerciseViewModel.workoutConfig.exerciseName
        tv_reps_reminder.invalidate()
        tv_workout_cta.invalidate()
        gradientAnimation?.start()
        startVibration()
        tv_current_set_num.text = "SET " + (WorkoutProgressViewModel.workoutProgress.setsCompleted + 1)
    }

    override fun onPause() {
        super.onPause()
        val mainActivity : DrawerMenuActivity = activity as DrawerMenuActivity
        mainActivity.toolbar.visibility = View.GONE
        if (::exerciseInstructionAnimation.isInitialized) exerciseInstructionAnimation?.stop()
        gradientAnimation?.stop()
        endVibration()
    }

    private fun startVibration(){
        if (vibrator == null || !vibrator.hasVibrator()) return;
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> vibrateAndroidO()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> vibrateLollipop()
            else -> vibrator.vibrate(vibratorPattern, 0)
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun vibrateLollipop(){
        val attrs : AudioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        vibrator.vibrate(vibratorPattern, 0, attrs)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun vibrateAndroidO(){
        vibrator.vibrate(VibrationEffect.createWaveform(
                this.vibratorPattern,
                this.vibrationAmplitudes,
                0
        ))
    }

    private fun endVibration(){
        if (vibrator == null || !vibrator.hasVibrator()) return;
        vibrator.cancel()
    }

}
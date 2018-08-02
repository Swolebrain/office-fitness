package com.swolebrain.officefitness.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.DrawerMenuActivity
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.workout.WorkoutConfigurationFragment
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Created by marjv on 7/6/2018.
 */
public class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Office Fitness"
        val mainActivity = activity as DrawerMenuActivity
        btn_log_in.setOnClickListener{
//            mainActivity.startLogin()
        }
        btn_start_workout.setOnClickListener{
            mainActivity.selectFragment(WorkoutConfigurationFragment())
        }
    }
}
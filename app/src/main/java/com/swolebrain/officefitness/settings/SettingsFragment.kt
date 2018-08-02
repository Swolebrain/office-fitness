package com.swolebrain.officefitness.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.R

/**
 * Created by marjv on 7/6/2018.
 */
public class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Settings"
    }
}


//TODO: big feature - badges
//TODO: settings page
//TODO: gamification ideas: badges, competition against coworkers, levels for lifetime reps
//TODO: #current reps with progress towards max(100, daily_avg) in a bar
//TODO: rep progress towards next rank
//TODO: signal to user that workout starts by resting
//TODO: home screen

//TODO: feed of recent workouts in history page
//TODO: delete workout from feed

//TODO: button to skip rest period
//TODO: implement ability to specify "clan" name
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

//TODO: implement ability to specify "clan" name
//TODO: big feature - badges
//TODO: split up graphs by exercise type
//TODO: settings page


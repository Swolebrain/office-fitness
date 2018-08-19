package com.swolebrain.officefitness.settings

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.loadProfileDataFromFireBase
import com.swolebrain.officefitness.repositories.UserProfile
import com.swolebrain.officefitness.repositories.getUserProfileData
import com.swolebrain.officefitness.saveProfileValue
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by marjv on 7/6/2018.
 */
class SettingsFragment : Fragment() {
    private lateinit var settingsAdapter : SettingsRVAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loadProfileDataFromFireBase()
        getUserProfileData().observe(
                this,
                Observer {
                    setProfileContents(it)
                }
        )
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Settings"
        rv_settings.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        settingsAdapter = SettingsRVAdapter(context!!, this, listOf())
        rv_settings.adapter = settingsAdapter
    }

    fun commitChanges(fieldName:String, value:String, operation: String?){
        Log.d("####", "Commit Changes function")
        when (fieldName){
            "displayName", "gender" -> {
                saveProfileValue(fieldName, value)
            }
            else -> {
                Log.d("####", "commitChanges operation not supported")
            }
        }
    }

    fun setProfileContents(profile: UserProfile?) {
        Log.d("####--", profile.toString())
        settingsAdapter.setProfile(profile)
        settings_overlay.visibility = View.GONE
        settings_progress_bar.visibility = View.GONE
    }
}


//TODO: big feature - badges
//TODO: settings page
//TODO: gamification ideas: badges, competition against coworkers, levels for lifetime reps
//TODO: #current reps with progress towards max(100, daily_avg) in a bar
//TODO: rep progress towards next rank
//TODO: home screen

//TODO: style feed of recent workouts in history page
//TODO: delete workout from feed

//TODO: implement ability to specify "clan" name
package com.swolebrain.officefitness.settings

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.tasks.Task
import com.swolebrain.officefitness.*
import com.swolebrain.officefitness.repositories.UserProfile
import com.swolebrain.officefitness.repositories.getUserProfileData
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by marjv on 7/6/2018.
 */
class SettingsFragment : Fragment() {
    private lateinit var settingsAdapter : SettingsRVAdapter
    private lateinit var profileObserver : Observer<UserProfile>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loadProfileDataFromFireBase()
        profileObserver = Observer {
            setProfileContents(it)
            Log.d("####", "##########!!!!!!!~~~~~~~~~~~LOADED PROFILE")
        }
        getUserProfileData().observe(this, profileObserver)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onResume() {
        super.onResume()
        loadProfileDataFromFireBase()
    }

    override fun onDestroy() {
        super.onDestroy()
        getUserProfileData().removeObserver(profileObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Settings"
        rv_settings.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        settingsAdapter = SettingsRVAdapter(context!!, this)
        rv_settings.adapter = settingsAdapter
    }

    fun commitChanges(fieldName:String, value:String, operation: String?) : Task<Void>?{
        Log.d("####", "Commit Changes function")
        when (fieldName){
            "displayName", "gender" -> {
                saveProfileValue(fieldName, value)
            }
            "clans" -> {
                if (operation == "add") return saveClan(value)?.addOnSuccessListener { loadProfileDataFromFireBase() }
                if (operation == "remove") return removeClan(value)?.addOnSuccessListener { loadProfileDataFromFireBase() }
            }
            else -> {
                Log.d("####", "commitChanges operation not supported")
            }
        }
        return null
    }

    fun setProfileContents(profile: UserProfile?) {
        settingsAdapter.setProfile(profile)
        settings_overlay.visibility = View.GONE
        settings_progress_bar.visibility = View.GONE
    }

    override fun onPause() {
        val imm: InputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var v: View? = this.view?.rootView
        if (v == null) v = View(activity)

        imm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
        super.onPause()
    }
}


//TODO: big feature - badges
//TODO: clans functionality
//TODO: push notifications to remind you to train
//TODO: fancy 3-2-1 countdown before exercise cta
//TODO: gamification ideas: badges, competition against coworkers, levels for lifetime reps
//TODO: #current reps with progress towards max(100, daily_avg) in a bar
//TODO: rep progress towards next rank
//TODO: home screen

//TODO: style feed of recent workouts in history page
//TODO: delete workout from feed

//TODO: implement ability to specify "clan" name
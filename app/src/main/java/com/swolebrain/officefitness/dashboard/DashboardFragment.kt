package com.swolebrain.officefitness.dashboard


import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.QuerySnapshot
import com.swolebrain.officefitness.DrawerMenuActivity
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.common.computeActivityIndexThisWeek
import com.swolebrain.officefitness.common.getUTCMidnightTimeMillis
import com.swolebrain.officefitness.getLast7DaysWorkout
import com.swolebrain.officefitness.loadProfileDataFromFireBase
import com.swolebrain.officefitness.repositories.UserProfile
import com.swolebrain.officefitness.repositories.getUserProfileData
import com.swolebrain.officefitness.repositories.getWorkoutLogs
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment(){
    private lateinit var alertBuilder : AlertDialog.Builder
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getLast7DaysWorkout()
        loadProfileDataFromFireBase()

        getWorkoutLogs().observe(
                this,
                Observer {
                    setWorkoutContents(it)
                }
        )
        getUserProfileData().observe(
                this,
                Observer {
                    setActivityIndex(it)
                }
        )

        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_about_app.setOnClickListener { showDialog() }
        val drawerMenuActivity : DrawerMenuActivity = activity as DrawerMenuActivity
        drawerMenuActivity.title = "Dashboard"
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showDialog(){
        if (!::alertBuilder.isInitialized){
            alertBuilder = AlertDialog.Builder(activity)
            alertBuilder.setMessage(label_description.text)
                    .setTitle("About OfficeFitness")
        }
        alertBuilder.create().show()
    }

    private fun setWorkoutContents(querySnapshot: QuerySnapshot?) {
        if (querySnapshot == null) return
        val midnightTonight = getUTCMidnightTimeMillis()
        var repsToday = 0L
        querySnapshot.forEach eachWorkout@{
            val timeStampGMT =  it.get("timestamp") as Long
            if (midnightTonight - timeStampGMT > 1000*60*60*24) return@eachWorkout
            repsToday += it.get("reps") as Long
        }
        tv_reps_today.text = "" + repsToday
    }

    private fun setActivityIndex(userProfile: UserProfile?) {
        if (userProfile == null) return;
        tv_lifetime_activity_points.text = "" + userProfile.allTimeActivity
        tv_past_week_activity_points.text = "" + (computeActivityIndexThisWeek(userProfile.activityIndexHistory, userProfile.activityIndexLastUpdated) )
    }

}


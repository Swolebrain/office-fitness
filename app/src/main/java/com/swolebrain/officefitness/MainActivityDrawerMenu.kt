package com.swolebrain.officefitness

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.swolebrain.officefitness.history.HistoryFragment
import com.swolebrain.officefitness.leaderboard.LeaderBoardFragment
import com.swolebrain.officefitness.repositories.ExerciseViewModel
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel
import com.swolebrain.officefitness.settings.SettingsFragment
import com.swolebrain.officefitness.workout.WorkoutConfigurationFragment
import com.swolebrain.officefitness.workout.WorkoutProgressFragment
import kotlinx.android.synthetic.main.activity_drawer_menu.*
import kotlinx.android.synthetic.main.fragment_start_workout.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_drawer_menu.view.*
import kotlinx.android.synthetic.main.drawer_menu_main_layout.*


class DrawerMenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var exerciseViewModel = ExerciseViewModel.workoutConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFormat(PixelFormat.RGBA_8888)
        setContentView(R.layout.activity_drawer_menu)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Launching workout...", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            launchWorkout()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        drawer_layout.nav_view
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        supportFragmentManager?.addOnBackStackChangedListener(onBackStackChanged)

        selectFragment(WorkoutConfigurationFragment())

        showHideLoggedInButtons()
    }

    override fun onPostResume() {
        super.onPostResume()
        showHideLoggedInButtons()
    }


    private val onBackStackChanged =  {
        val currentFragment: Fragment = supportFragmentManager.findFragmentByTag("visible_fragment")
        when (currentFragment) {
            is WorkoutConfigurationFragment -> nav_view.setCheckedItem(R.id.nav_start_workout)
            is HistoryFragment -> nav_view.setCheckedItem(R.id.nav_history)
            is LeaderBoardFragment -> nav_view.setCheckedItem(R.id.nav_leader_board)
            is SettingsFragment -> nav_view.setCheckedItem(R.id.nav_settings)
        }

        if (currentFragment is WorkoutConfigurationFragment) fab.visibility = View.VISIBLE
        else fab.visibility = View.GONE
    }

    private fun showHideLoggedInButtons() {
        val currentUSer = FirebaseAuth.getInstance().currentUser
        val logOutItem = nav_view.menu?.findItem(R.id.nav_log_out)
        val logInItem = nav_view.menu?.findItem(R.id.nav_log_in)
        val history = nav_view.menu?.findItem(R.id.nav_history)
        val leaderboard = nav_view.menu?.findItem(R.id.nav_leader_board)
        if (currentUSer == null){
            logOutItem?.isVisible = false
            logInItem?.isVisible = true
            history?.isVisible = false
            leaderboard?.isVisible = false
        }
        else {
            logInItem?.isVisible = false
            logOutItem?.isVisible = true
            history?.isVisible = true
            leaderboard?.isVisible = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager?.removeOnBackStackChangedListener(onBackStackChanged)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.drawer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment : Fragment

        when (item.itemId) {
            R.id.nav_start_workout -> {
                fragment = WorkoutConfigurationFragment()
            }
            R.id.nav_history -> {
                fragment = HistoryFragment()
            }
            R.id.nav_leader_board -> {
                fragment = LeaderBoardFragment()
            }
            R.id.nav_settings -> {
                fragment = SettingsFragment()
            }
            R.id.nav_log_out -> {
                val act  = this;
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener{
                            act.startActivity(
                                    Intent(act, LoginPromptActivity::class.java)
                            )
                        }
                return true
            }
            R.id.nav_log_in -> {
                this.startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setLogo(R.drawable.icon_white)
                                .setTheme(R.style.NunitoTheme)
                                .build(),
                        RC_SIGN_IN
                )
                return true
            }
            else -> {
                return false
            }
        }

        selectFragment(fragment)

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun launchWorkout(){
        WorkoutProgressViewModel.workoutProgress.startTime = System.currentTimeMillis()
        WorkoutProgressViewModel.workoutProgress.repsCompleted = 0
        WorkoutProgressViewModel.workoutProgress.setsCompleted = 0
        Log.d("vic-LAUNCHING WORKOUT", ExerciseViewModel.exerciseNames[picker_select_exercise.value])
        selectFragment(WorkoutProgressFragment())
    }

    fun selectFragment(fragment:Fragment){
        val ft = supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.drawer_menu_fragment_holder, fragment, "visible_fragment")
        ft?.addToBackStack(null)
        ft?.commit()
        if (fragment is WorkoutConfigurationFragment) fab.visibility = View.VISIBLE
        else fab.visibility = View.GONE
    }

    fun recordCompletedSet(){
        Log.d("RECORDCOMPLETED", "###############################################################################################################################################################################################################")
//        if (ExerciseViewModel.workoutConfig.repetitions == -1) return
        WorkoutProgressViewModel.workoutProgress.repsCompleted += ExerciseViewModel.workoutConfig.repetitions
        WorkoutProgressViewModel.workoutProgress.setsCompleted += 1
        selectFragment(WorkoutProgressFragment())
    }




}

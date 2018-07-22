package com.swolebrain.officefitness

import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
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
import com.swolebrain.officefitness.workout.StartWorkoutFragment
import com.swolebrain.officefitness.workout.WorkoutProgressFragment
import kotlinx.android.synthetic.main.activity_drawer_menu.*
import kotlinx.android.synthetic.main.app_bar_drawer_menu.*
import kotlinx.android.synthetic.main.fragment_start_workout.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.swolebrain.officefitness.settings.HomeFragment
import kotlinx.android.synthetic.main.content_drawer_menu.*


class DrawerMenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var exerciseViewModel = ExerciseViewModel.workoutConfig
    private val RC_SIGN_IN : Int = 123
    val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build()
//            AuthUI.IdpConfig.PhoneBuilder().build(),
//            AuthUI.IdpConfig.GoogleBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build(),
    )

    private val onBackStackChanged =  {
        var currentFragment: Fragment = supportFragmentManager.findFragmentByTag("visible_fragment")
        if (currentFragment is StartWorkoutFragment) nav_view.setCheckedItem(R.id.nav_start_workout)
        if (currentFragment is HistoryFragment) nav_view.setCheckedItem(R.id.nav_history)
        if (currentFragment is LeaderBoardFragment) nav_view.setCheckedItem(R.id.nav_leader_board)
        if (currentFragment is SettingsFragment) nav_view.setCheckedItem(R.id.nav_settings)
        if (currentFragment is StartWorkoutFragment) fab.visibility = View.VISIBLE
        else fab.visibility = View.GONE
    }


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
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        supportFragmentManager?.addOnBackStackChangedListener(onBackStackChanged)

        //handle selecting first fragment
        val user = FirebaseAuth.getInstance().currentUser
        Log.d("####", "CurrentUser: "+ user?.uid)
        if (user == null){
            selectFragment(HomeFragment())
        }
        else {
            selectFragment(StartWorkoutFragment())
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
                fragment = StartWorkoutFragment()
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
            else -> {
                fragment = StartWorkoutFragment() //OJO CON ESTO
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
        Log.d("vic-LAUNCHING WORKOUT", StartWorkoutFragment.exerciseNames[picker_select_exercise.value])
        selectFragment(WorkoutProgressFragment())
    }

    fun selectFragment(fragment:Fragment){
        val ft = supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.content_frame, fragment, "visible_fragment")
        ft?.addToBackStack(null)
        ft?.commit()
        if (fragment is StartWorkoutFragment) fab.visibility = View.VISIBLE
        else fab.visibility = View.GONE
    }

    fun recordCompletedSet(){
        Log.d("RECORDCOMPLETED", "###############################################################################################################################################################################################################")
//        if (ExerciseViewModel.workoutConfig.repetitions == -1) return
        WorkoutProgressViewModel.workoutProgress.repsCompleted += ExerciseViewModel.workoutConfig.repetitions
        WorkoutProgressViewModel.workoutProgress.setsCompleted += 1
        selectFragment(WorkoutProgressFragment())
    }


    fun startLogin(){
        this.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher)
                        .setTheme(R.style.ThemeOverlay_AppCompat_Dark)
                        .build(),
                RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != RC_SIGN_IN) return

        val response = if (IdpResponse.fromResultIntent(data) != null) IdpResponse.fromResultIntent(data) else return
        //returned if they hit back button

        if (resultCode != Activity.RESULT_OK) {
            val error = response?.error?.errorCode
            Snackbar.make(this.content_frame, "Error - "+ error, Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
        }

        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        Log.d("####", user.email + " " + user.getIdToken(true))

        selectFragment(StartWorkoutFragment())
    }

}

package com.swolebrain.officefitness

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Window
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login_prompt.*
import kotlinx.android.synthetic.main.content_drawer_menu.*

class LoginPromptActivity : AppCompatActivity() {
    private val RC_SIGN_IN : Int = 123
    val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build()
//            AuthUI.IdpConfig.PhoneBuilder().build(),
//            AuthUI.IdpConfig.GoogleBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build(),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login_prompt)

        //handle user already logged in
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            startActivity(
                    Intent(this, DrawerMenuActivity::class.java)
            )
            return
        }

        btn_log_in.setOnClickListener{
            startLogin()
        }
        btn_start_workout.setOnClickListener{
            startActivity(
                    Intent(this, DrawerMenuActivity::class.java)
            )
        }
    }

    private fun startLogin(){
        this.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.icon_white)
                        .setTheme(R.style.CustomFirebaseUITheme)
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
            return
        }

        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        Log.d("####", user.email + " " + user.getIdToken(true))

        startActivity(Intent(this, DrawerMenuActivity::class.java))
    }
}

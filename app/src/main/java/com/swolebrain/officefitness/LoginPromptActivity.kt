package com.swolebrain.officefitness

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login_prompt.*
import kotlinx.android.synthetic.main.drawer_menu_main_layout.*

class LoginPromptActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login_prompt)

        //handle user already logged in
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            for (prop in user::class.java.fields){
                Log.d("####", "${prop.name}: ${prop.get(user)}")
            }
            Log.d("####", user.uid)
            Log.d("####", user.displayName)
            Log.d("####", user.email)
            Log.d("####", user.getIdToken(true).toString())
            saveUserData(user)
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
                        .setLogo(R.drawable.hex_icon_256)
                        .setTheme(R.style.NunitoTheme)
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
            Snackbar.make(this.drawer_menu_fragment_holder, "Error - "+ error, Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
            return
        }

        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        saveUserData(user)
        Log.d("####", user.email + " " + user.getIdToken(true))

        startActivity(Intent(this, DrawerMenuActivity::class.java))
    }

    private fun saveUserData(user: FirebaseUser){
        FirebaseFirestore.getInstance().collection("users").document(user.uid).update(mutableMapOf(
                "userName" to user.displayName as String,
                "pictureUrl" to user.photoUrl.toString()
        ) as Map<String, String>)
    }
}



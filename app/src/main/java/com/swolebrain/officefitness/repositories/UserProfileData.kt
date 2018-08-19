package com.swolebrain.officefitness.repositories

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

private val userProfile : MutableLiveData<UserProfile> = MutableLiveData()

fun getUserProfileData() = userProfile

fun loadUserProfileData(userDoc:DocumentSnapshot){
    var displayName = userDoc.get("displayName")
    if (displayName != null) displayName = displayName as String
    else displayName = ""

    var gender = userDoc.get("gender")
    if (gender != null) gender = gender as String
    else gender = "male"

    var clans = userDoc.get("clans")
    if (clans != null) clans = clans as List<String>
    else clans = listOf<String>()

    userProfile.value = UserProfile(displayName, gender, clans)
}

data class UserProfile(
        val displayName : String,
        val gender: String,
        val clansList : List<String>
)
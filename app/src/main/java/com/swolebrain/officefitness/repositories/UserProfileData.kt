package com.swolebrain.officefitness.repositories

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

private val userProfile : MutableLiveData<UserProfile> = MutableLiveData()

fun getUserProfileData() = userProfile

fun loadUserProfileData(userDoc:DocumentSnapshot){
    userProfile.value = buildUserProfileFromDocument(userDoc)
}

fun buildUserProfileFromDocument(userDoc: DocumentSnapshot): UserProfile{
    var displayName = userDoc.get("displayName")
    if (displayName != null) displayName = displayName as String
    else displayName = ""

    var gender = userDoc.get("gender")
    if (gender != null) gender = gender as String
    else gender = "male"

    var clans = userDoc.get("clans")
    if (clans != null) clans = clans as List<String>
    else clans = listOf<String>()


    var userName = userDoc.get("userName")
    if(userName != null) userName = userName as String
    else userName = ""

    var activityIndexLastUpdated = userDoc.get("activityIndexLastUpdated")
    if (activityIndexLastUpdated != null) activityIndexLastUpdated = activityIndexLastUpdated as Long
    else activityIndexLastUpdated = 0L

    var allTimeActivity = userDoc.get("allTimeActivity")
    allTimeActivity = when (allTimeActivity){
        null -> 0
        is Number -> allTimeActivity.toInt()
        else -> throw Exception("Illegal parameter type for all time activity when reading from firebase")
    }

    var activityIndexHistory = userDoc.get("activityIndexHistory")
    if (activityIndexHistory != null) activityIndexHistory = activityIndexHistory as List<Number>
    else activityIndexHistory = listOf<Number>(0,0,0,0,0,0,0)

    return UserProfile(displayName, userName ,gender, clans, activityIndexLastUpdated, allTimeActivity, activityIndexHistory)
}

data class UserProfile(
        val displayName : String = "",
        val userName : String ="",
        val gender: String,
        val clansList : List<String>,
        val activityIndexLastUpdated: Long,
        val allTimeActivity: Number,
        val activityIndexHistory: List<Number> = listOf()
)
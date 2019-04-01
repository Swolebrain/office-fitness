package com.swolebrain.officefitness.repositories

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.swolebrain.officefitness.common.computeActivityIndexThisWeek

private val leaderBoards = MutableLiveData<MutableMap<String, List<UserRanking>>>()
val clanMemberLists: MutableMap<String, Set<String>> = mutableMapOf()


fun getLeaderBoards(): MutableLiveData<MutableMap<String, List<UserRanking>>> = leaderBoards

fun processUserIntoLeaderBoard(clan: String, userSnapshot: DocumentSnapshot, memberId: String){
    if (clanMemberLists.containsKey(clan) && clanMemberLists[clan]!!.contains(memberId)){
        return
    }
    if (userSnapshot.get("allTimeActivity") == null) return
    var newVal = leaderBoards.value
    if (newVal == null) newVal = mutableMapOf()
    if (newVal!![clan] == null) newVal!![clan] = mutableListOf()

    var displayName = userSnapshot.get("displayName")
    if (displayName == null) displayName = userSnapshot.get("userName")

    val activityIndexHistory = userSnapshot.get("activityIndexHistory")
    val activityIndexLastUpdated = userSnapshot.get("activityIndexLastUpdated")
    if (activityIndexHistory == null || activityIndexLastUpdated == null || displayName == null) return;
    newVal!![clan] = listOf(
            UserRanking(
                    displayName as String,
                    computeActivityIndexThisWeek(
                            activityIndexHistory as List<Int>,
                            activityIndexLastUpdated as Long
                    ),
                    userSnapshot.get("pictureUrl") as String
            )
    ) + newVal!![clan]!!
    //sort leader board
    newVal!![clan] = newVal!![clan]!!.sortedByDescending{ it.activityIndex }
    leaderBoards.value = newVal


    if (clanMemberLists[clan] == null) clanMemberLists[clan] = setOf()
    clanMemberLists[clan] = clanMemberLists[clan]!! + setOf(memberId)
}

fun loadListOfUsers(clan: String, users: QuerySnapshot?){
    var leaderBoardData = leaderBoards.value
    if (leaderBoardData == null) leaderBoardData = mutableMapOf()
    val usersList = users?.documents?.map{
        var displayName = it.get("displayName")
        if (displayName == null ||  displayName.toString().isBlank()) displayName = it.get("userName")
        val activityIndexHistory = it.get("activityIndexHistory")
        val activityIndexLastUpdated = it.get("activityIndexLastUpdated")
        if (displayName == null || activityIndexHistory == null || activityIndexLastUpdated == null) return@map UserRanking("", 0, "")
        var allTimeActivity = it.get("allTimeActivity") ?: 0L
        if (allTimeActivity is Long ) allTimeActivity = allTimeActivity.toInt()
        else if (allTimeActivity is Double) allTimeActivity = allTimeActivity.toInt()
        else if (allTimeActivity is Float) allTimeActivity = allTimeActivity.toInt()
        else if (allTimeActivity is Number) allTimeActivity = allTimeActivity.toInt()
        else throw Exception("Wrong data type when reading all time activity")
        val userActivity = if (clan == "All Time") allTimeActivity else computeActivityIndexThisWeek(activityIndexHistory as List<Int>, activityIndexLastUpdated as Long)

        UserRanking(
            displayName as String,
            userActivity,
            it.get("pictureUrl") as String
        )
    }?.filter { it.activityIndex > 0 }

    if (usersList == null || usersList.isEmpty()) return

    leaderBoardData[clan] = usersList
    leaderBoards.value = leaderBoardData
}

fun resetLeaderBoards(){
    clanMemberLists.clear()
    leaderBoards.value = mutableMapOf()
}


data class UserRanking(val name: String, val activityIndex: Int, val pictureUrl: String, var rank: Int = 1)
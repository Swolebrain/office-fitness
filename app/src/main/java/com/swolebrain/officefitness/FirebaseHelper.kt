package com.swolebrain.officefitness

import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.Scopes
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.swolebrain.officefitness.common.getUTCTime7DaysAgo
import com.swolebrain.officefitness.repositories.*


const val RC_SIGN_IN : Int = 123
val providers = listOf(
        AuthUI.IdpConfig.FacebookBuilder().setPermissions(mutableListOf("email", "public_profile")).build(),
        AuthUI.IdpConfig.GoogleBuilder().setScopes(listOf(Scopes.PROFILE)).build(),
        AuthUI.IdpConfig.EmailBuilder().build()
//            AuthUI.IdpConfig.TwitterBuilder().build(),
)

/**
 * WORKOUT LOGS
 */
fun recordCompletedWorkout(secondsElapsed: Long){
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return
    if (WorkoutProgressViewModel.workoutProgress.repsCompleted <= 0) return

    val workoutLogsRef = db.collection("users")
            .document(currentUserAuth.uid)
            .collection("workoutlogs")

    workoutLogsRef.add(mutableMapOf(
            "timestamp" to System.currentTimeMillis(),
            "reps" to WorkoutProgressViewModel.workoutProgress.repsCompleted,
            "sets" to WorkoutProgressViewModel.workoutProgress.setsCompleted,
            "exercise" to ExerciseViewModel.workoutConfig.exerciseName,
            "durationSeconds" to secondsElapsed
    ))
}

fun getLast7DaysWorkout(){
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return

    val workoutLogsRef = db.collection("users")
            .document(currentUserAuth.uid)
            .collection("workoutlogs")

    workoutLogsRef.whereGreaterThanOrEqualTo("timestamp", getUTCTime7DaysAgo())
            .get()
            .addOnCompleteListener{
                if (!it.isSuccessful){
                    Log.d("####", "Error retrieving firebase workouts")
                    Log.d("####", it.exception.toString())
                    return@addOnCompleteListener
                }
                Log.d("####", "Retrieved workout logs from firebase")
                loadWorkoutLogs(it.result)
            }
}

fun deleteWorkoutById(workoutId: String){
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return

    db.collection("users")
            .document(currentUserAuth.uid)
            .collection("workoutlogs")
            .document(workoutId)
            .delete()
            .addOnSuccessListener { getLast7DaysWorkout() }
}

/**
 *  PROFILE
 */
fun saveProfileValue(fieldName:String, value:String){
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return
    Log.d("####", "Saving profile value ${fieldName}...")

    val userRef = db.collection("users")
            .document(currentUserAuth.uid)

    userRef.set(mapOf(fieldName to value), SetOptions.merge())
            .addOnSuccessListener { Log.d("####", "Data written to firebase - ${fieldName} : ${value}") }
}

fun saveClan(clanName: String) : Task<Void>?{
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return null
    Log.d("####", "Saving new clan to profile...")

    val userRef = db.collection("users")
            .document(currentUserAuth.uid)

    return userRef.update("clans", FieldValue.arrayUnion(clanName))
}

fun removeClan(clanName: String): Task<Void>? {
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return null
    Log.d("####", "deleting clan from profile...")

    val userRef = db.collection("users")
            .document(currentUserAuth.uid)

    return userRef.update("clans", FieldValue.arrayRemove(clanName))
}


fun loadProfileDataFromFireBase(): Task<DocumentSnapshot>? {
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return null
    val userRef = db.collection("users")
            .document(currentUserAuth.uid)

    return userRef.get().addOnCompleteListener {
        if (!it.isSuccessful){
            Log.d("####", "Error retrieving firebase profile")
            return@addOnCompleteListener
        }
        loadUserProfileData(it.result )
    }
}


/**
 * LEADER BOARD
*/

fun loadLeaderBoards(){
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return

    val userProfile = getUserProfileData().value
    if (userProfile == null)
        loadProfileDataFromFireBase()!!
                .addOnSuccessListener { loadLeaderBoardsForUser() }
    else loadLeaderBoardsForUser()
}

fun loadLeaderBoardsForUser(){
    val db = FirebaseFirestore.getInstance()
    val userProfile = getUserProfileData().value
    val clansList: List<String>? = userProfile?.clansList
    if (clansList == null || clansList.isEmpty()) return

    clansList.forEach{ clanName ->
        db.collection("clans").document(clanName).get()
                .addOnSuccessListener { clanSnapshot ->
                    loadUsersInClan(clanName, clanSnapshot, db)
                }
    }
}

fun loadUsersInClan(clanName: String, clanDoc: DocumentSnapshot, db: FirebaseFirestore){
    if (!clanDoc.exists() || !clanDoc.contains("members")) return
    var memberIDs = clanDoc.get("members")
    if (memberIDs === null ) return
    memberIDs = memberIDs as List<String>
    if ( memberIDs.isEmpty()) return
    memberIDs.forEach { memberId ->
        db.collection("users").document(memberId).get()
                .addOnSuccessListener { documentSnapshot ->
//                    Log.d("####", "Loaded user doc snapshot into $clanName - " + documentSnapshot.data.toString() )
                    processUserIntoLeaderBoard(clanName, documentSnapshot, memberId)
                }
    }
}

fun loadAllTimeRankings(){
    val currentUserAuth = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()
    if (db == null || currentUserAuth == null) return

    db.collection("users")
            .orderBy("allTimeActivity", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { loadListOfUsers("All Time", it) }

}
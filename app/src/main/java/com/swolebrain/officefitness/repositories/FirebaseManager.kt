package com.swolebrain.officefitness.repositories

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseManager {

    fun addReps () {

    }

    companion object {
        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    }
}
package com.swolebrain.officefitness.history

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.QuerySnapshot
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.getLast7DaysWorkout
import com.swolebrain.officefitness.repositories.getWorkoutLogs
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Created by marjv on 7/6/2018.
 */
class HistoryFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getLast7DaysWorkout()
        getWorkoutLogs().observe(
                this,
                Observer {
                    setContents(it)
                }
        )
        return inflater.inflate(R.layout.fragment_history, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Workout History"
    }

    fun setContents(querySnapshot: QuerySnapshot?){
        var content : String = ""
        querySnapshot?.forEach {
            content += it.data
            content += "\n"
        }
        textView2.text = content
    }
}
package com.swolebrain.officefitness.leaderboard

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.loadAllTimeRankings
import com.swolebrain.officefitness.loadLeaderBoards
import com.swolebrain.officefitness.repositories.UserRanking
import com.swolebrain.officefitness.repositories.getLeaderBoards
import com.swolebrain.officefitness.repositories.resetLeaderBoards
import kotlinx.android.synthetic.main.fragment_leader_board.*

/**
 * Created by marjv on 7/6/2018.
 */
public class LeaderBoardFragment : Fragment() {
    private lateinit var leaderBoardAdapter: LeaderBoardRVAdapter
    private lateinit var leaderBoardObserver: Observer<MutableMap<String, List<UserRanking>>>
    private val rankings = mutableMapOf<String, List<UserRanking>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        leaderBoardAdapter = LeaderBoardRVAdapter(context!!, this, rankings)
        leaderBoardObserver = Observer{
            setContents(it)
        }
        return inflater.inflate(R.layout.fragment_leader_board, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Leader Board"
        rv_leader_board.layoutManager = LinearLayoutManager(context)
        rv_leader_board.adapter = leaderBoardAdapter
        pb_leader_board.visibility = View.VISIBLE
        leader_board_overlay.visibility = View.VISIBLE

        getLeaderBoards().observe(
                this,
                this.leaderBoardObserver
        )
    }

    override fun onResume() {
        if (pb_leader_board.visibility == View.GONE) pb_leader_board.visibility = View.VISIBLE
        if (leader_board_overlay.visibility == View.GONE) leader_board_overlay.visibility = View.VISIBLE
        resetLeaderBoards()
        rankings.clear()
        loadLeaderBoards()
        loadAllTimeRankings()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        getLeaderBoards().removeObserver(this.leaderBoardObserver)
    }

    private fun setContents(clanMap: MutableMap<String, List<UserRanking>>?) {
        if (clanMap == null) return

        for ( (clan, memberList) in clanMap)
            leaderBoardAdapter.pushLeaderBoard(clan, memberList)

        pb_leader_board.visibility = View.GONE
        leader_board_overlay.visibility = View.GONE
    }


}

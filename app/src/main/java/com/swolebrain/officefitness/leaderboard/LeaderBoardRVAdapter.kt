package com.swolebrain.officefitness.leaderboard

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.repositories.UserRanking
import kotlinx.android.synthetic.main.fragment_leader_board_rv_ranking_row.view.*
import kotlinx.android.synthetic.main.fragment_leader_board_rv_rankings_title.view.*

open class BaseLBViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
class ClanHeadingViewHolder(itemView: View): BaseLBViewHolder(itemView)
class ClanRowViewHolder(itemView: View): BaseLBViewHolder(itemView)


class LeaderBoardRVAdapter(val c: Context, val fragment: LeaderBoardFragment, val rankings: MutableMap<String, List<UserRanking>>): RecyclerView.Adapter<BaseLBViewHolder>(){
    private var rows = listOf<Any>()
    private val inflater = LayoutInflater.from(c)!!

    companion object {
        const val BOARD_IMG_HEADER = 0
        const val HEADER = 1
        const val RANKING_ROW = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseLBViewHolder {
        return when(viewType){
            BOARD_IMG_HEADER -> BaseLBViewHolder(inflater.inflate(R.layout.fragment_leader_board_rv_img_title, parent, false))
            HEADER -> ClanHeadingViewHolder(inflater.inflate(R.layout.fragment_leader_board_rv_rankings_title, parent, false))
            RANKING_ROW -> ClanRowViewHolder(inflater.inflate(R.layout.fragment_leader_board_rv_ranking_row, parent, false))
            else -> throw IllegalArgumentException("Illegal view type")
        }
    }


    override fun onBindViewHolder(holder: BaseLBViewHolder, position: Int) {
        with (holder.itemView){
            when (holder){
                is ClanRowViewHolder -> {
                    with (rows[position-1] as UserRanking){
                        tv_clan_member_rank.text = "" + this.rank
                        tv_clan_member_name.text = this.name
                        tv_clan_member_activity_points.text = "" + this.activityIndex

                    }
                }
                is ClanHeadingViewHolder -> {
                    tv_rankings_title.text = rows[position-1] as String
                }
            }
            if (position == 1) {
                val params = layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1F, c.resources.displayMetrics).toInt()
            }
            else if (position == itemCount-1){
                val params = layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48F, c.resources.displayMetrics).toInt()
            }
            else {
                val params = layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0F, c.resources.displayMetrics).toInt()
            }
        }
    }

    override fun getItemCount(): Int = rows.size + 1

    override fun getItemViewType(position: Int): Int{
        if (position == 0) return BOARD_IMG_HEADER
        return if (rows[position-1] is String) HEADER else RANKING_ROW
    }

    fun pushLeaderBoard(clanName: String, rankingsList: List<UserRanking>){
//        Log.d("####", "pushLeaderBoard called in adapter")
        if (rankingsList == null || rankingsList.isEmpty()) return
        rankings[clanName] = rankingsList
                .sortedByDescending { it.activityIndex }
                .mapIndexed { index, userRanking ->
                    userRanking.rank = index + 1
                    userRanking
                }


        val containsAllClans = rankings.containsKey("All Clans")
        var labels = rankings.keys.filter{ it != "All Clans" }.sortedBy { it }
        if (containsAllClans) labels = listOf("All Clans") + labels

        rows = listOf()
        labels.forEach {
            rows += listOf(it)
            rankings[it]?.forEach{ rows += listOf(it) }
        }
        notifyDataSetChanged()
    }

}




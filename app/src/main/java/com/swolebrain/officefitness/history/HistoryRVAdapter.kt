package com.swolebrain.officefitness.history

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.swolebrain.officefitness.R
import com.swolebrain.officefitness.repositories.WorkoutLog
import com.swolebrain.officefitness.repositories.WorkoutProgressViewModel
import kotlinx.android.synthetic.main.fragment_history_rv_graph.view.*
import kotlinx.android.synthetic.main.fragment_history_rv_workout_row.view.*
import kotlinx.android.synthetic.main.fragment_history_rvline1.view.*
import java.util.*

/*
    IMPORTANT: LIST IS OFFSET BY 2, MISSING THE FIRST 2 ELEMENTS
 */
@Suppress("PrivatePropertyName")
class HistoryRVAdapter(private val c: Context, private val workoutLogs: MutableList<WorkoutLog>) : RecyclerView.Adapter<HistoryRVAdapter.BaseViewHolder>() {
    private val VIEW_TYPE_TEXT_WIDGET = 0
    private val VIEW_TYPE_GRAPH = 1
    private val VIEW_TYPE_WORKOUT_HEADER = 2
    private val VIEW_TYPE_WORKOUT_ENTRY = 3
    private val logOffset = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater : LayoutInflater = LayoutInflater.from(c)
        when (viewType){
            VIEW_TYPE_TEXT_WIDGET -> return TextWidgetViewHolder(
                    inflater.inflate(R.layout.fragment_history_rvline1, parent, false)
            )
            VIEW_TYPE_GRAPH -> {
                val holder = GraphViewHolder(
                        inflater.inflate(R.layout.fragment_history_rv_graph, parent, false)
                )
                with (holder.itemView){
                    history_week_graph.viewport.setMaxX(7.5)
                    history_week_graph.title = "Reps per Day"
                    history_week_graph.gridLabelRenderer.labelFormatter = WeekGraphLabelFormatter()
                    history_week_graph.gridLabelRenderer.horizontalAxisTitle = "Day of Week"
                    history_week_graph.gridLabelRenderer.numHorizontalLabels = 7
                    history_week_graph.gridLabelRenderer.setHorizontalLabelsAngle(-45)
                    history_week_graph.removeAllSeries()
                    history_week_graph.addSeries(
                            LineGraphSeries<DataPoint>(HistoryFragment.dataPoints.toTypedArray())
                    )
                }
                return holder
            }
            VIEW_TYPE_WORKOUT_HEADER -> return WorkoutHeaderViewHolder(
                    inflater.inflate(R.layout.fragment_history_rv_workout_title, parent, false)
            )
            VIEW_TYPE_WORKOUT_ENTRY -> return WorkoutItemViewHolder(
                    inflater.inflate(R.layout.fragment_history_rv_workout_row, parent, false)
            )
            else -> throw InstantiationException("Incorrect View Type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        with (holder.itemView){
            when (holder){
                is TextWidgetViewHolder -> {
                    tv_lastweek_activity.text = WorkoutProgressViewModel.workoutProgress.getTimeSpent(HistoryFragment.totalActivitySeconds)
                    tv_lastweek_reps.text = HistoryFragment.totalReps.toString()
                }
                is GraphViewHolder -> {
                    history_week_graph.viewport.setMaxX(7.5)
                    history_week_graph.title = "Reps per Day"
                    history_week_graph.gridLabelRenderer.labelFormatter = WeekGraphLabelFormatter()
                    history_week_graph.gridLabelRenderer.horizontalAxisTitle = "Day of Week"
                    history_week_graph.gridLabelRenderer.numHorizontalLabels = 7
                    history_week_graph.gridLabelRenderer.setHorizontalLabelsAngle(-45)
                    history_week_graph.removeAllSeries()
                    history_week_graph.addSeries(
                            LineGraphSeries<DataPoint>(HistoryFragment.dataPoints.toTypedArray())
                    )
                }
                is WorkoutItemViewHolder -> {
                    val log : WorkoutLog = workoutLogs[workoutLogs.size - 1 - position + logOffset]
                    history_tv_exercise_name.text = log.exercise
                    history_tv_reps.text = log.reps.toString()
                    history_tv_sets.text = log.sets.toString()
                    history_tv_exercise_date.text = historyFormatDate(log.timeStamp)
                }
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when  (position){
            0 -> VIEW_TYPE_TEXT_WIDGET
            1 -> VIEW_TYPE_GRAPH
            2 -> VIEW_TYPE_WORKOUT_HEADER
            else -> VIEW_TYPE_WORKOUT_ENTRY
        }
    }

    override fun getItemCount(): Int = workoutLogs.size+logOffset



    open class BaseViewHolder(v: View) : RecyclerView.ViewHolder(v)
    class TextWidgetViewHolder(v: View) : BaseViewHolder(v)
    class GraphViewHolder(v: View) : BaseViewHolder(v)
    class WorkoutHeaderViewHolder(v: View) : BaseViewHolder(v)
    class WorkoutItemViewHolder(v: View) : BaseViewHolder(v)
}

class WeekGraphLabelFormatter : DefaultLabelFormatter(){
    override fun formatLabel(value: Double, isValueX: Boolean): String {
        if (!isValueX)
            return super.formatLabel(value, isValueX)

        val labels = generateDateMap(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
        if (value.toInt() < labels.size && value.toInt() >= 0)
            return labels[value.toInt()]

        return ""
    }

    private fun generateDateMap(dayOfWeek: Int): List<String> {
        val currentDay = dayOfWeek - 1
        val labels =  listOf("Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun")
        val shift = labels.take(currentDay)
        return labels.takeLast(7-currentDay) + shift
    }
}
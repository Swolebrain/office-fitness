package com.swolebrain.officefitness.common

fun computeActivityIndexThisWeek(activityHistory: List<Number>, lastUpdate: Long) : Int{
    val oneDay = 1000 *60 *60 *24
    var numDaysAgo = ((System.currentTimeMillis() - lastUpdate)/oneDay).toInt()
    if (numDaysAgo < 0) numDaysAgo = 0
    if (numDaysAgo >= 7) return 0
    return activityHistory.subList(numDaysAgo, activityHistory.size)
            .map{ it.toInt() }
            .reduce { acc, i -> acc + i }
}

fun computeActivityIndexToday(){

}
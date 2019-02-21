package com.ice.ktuice.al.gradeTable.gradeTableModels

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.getStackTraceString

/**
 * Created by Andrius on 1/27/2018.
 * This is a date container class, containing the week string and a numerical
 * estimation, for sorting and comparison
 */
class WeekModel(val week:String){
    val weekValue: Float
    init {
            weekValue = parseWeekToValue(week)
    }

    override fun equals(other: Any?): Boolean {
        return other is WeekModel && this.week == other.week && this.weekValue == other.weekValue
    }

    /* Generated hashCode override */
    override fun hashCode(): Int {
        var result = week.hashCode()
        result = 31 * result + weekValue.hashCode()
        return result
    }

    companion object: AnkoLogger{
        /**
         * Takes in a string representation of a week and converts it to an exact (Double) value.
         * Examples:
         *      "17" => 17
         *      "4-6" => 5  (average)
         *      "17-20" => 18.5
         * @param week - a string representation of a week
         */
        fun parseWeekToValue(week: String):Float{
            var retVal = 0f
            var partCounter = 0
            val numbers = week.split("-", ignoreCase = true)
            for(elem in numbers){
                try{
                    retVal += elem.toFloat()
                    partCounter++
                }catch (e: NumberFormatException){
                    info(e.getStackTraceString())
                }
            }
            if(partCounter == 0) return 0f
            return retVal / partCounter.toFloat()
        }
    }
}
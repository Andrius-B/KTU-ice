package com.ice.ktuice.models.timetableModels

import com.ice.ktuice.models.TimetableModel
import java.text.SimpleDateFormat
import java.util.*

class TimetableWeek(val weekName: String, val weekStartDateString: String){
    val weekStartDate: Date

    init {
        weekStartDate = TimetableModel.dateFormat.parse(weekStartDateString)
    }

    override fun equals(other: Any?): Boolean {
        if(other !is TimetableWeek) return  false
        return weekName==other.weekName && weekStartDateString==other.weekStartDateString
    }

    override fun hashCode(): Int {
        var result = weekName.hashCode()
        result = 31 * result + weekStartDateString.hashCode()
        result = 31 * result + weekStartDate.hashCode()
        return result
    }
}
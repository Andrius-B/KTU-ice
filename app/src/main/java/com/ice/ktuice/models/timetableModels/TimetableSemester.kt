package com.ice.ktuice.models.timetableModels

import com.ice.ktuice.models.TimetableModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * A small data class to store semesters fetched from the
 * timetable page
 */
class TimetableSemester(val semesterName: String, val semesterStartDateString:String){
    val semesterStartDate: Date

     init {
         semesterStartDate = TimetableModel.dateFormat.parse(semesterStartDateString)
    }
}
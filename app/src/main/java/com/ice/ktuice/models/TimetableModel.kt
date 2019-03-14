package com.ice.ktuice.models

import com.ice.ktuice.models.timetableModels.TimetableSemester
import com.ice.ktuice.models.timetableModels.TimetableWeek
import java.text.SimpleDateFormat
import java.util.*

/**
 * A model to describe the scraped contents of
 * the lecture timetable page
 *
 * currentWeek and currentSemester
 * are items in the allSemesters and allWeeks lists
 *
 * and the allWeeks list contains all the weeks for the current (default) semester
 */
class TimetableModel(val currentSemester: TimetableSemester,
                     val currentWeek: TimetableWeek,
                     val allSemesters: List<TimetableSemester>,
                     val allWeeks: List<TimetableWeek>,
                     val upcomingTests: Map<TimetableWeek, List<String>>){
    companion object {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.US)
    }
}
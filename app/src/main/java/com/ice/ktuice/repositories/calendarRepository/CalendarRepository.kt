package com.ice.ktuice.repositories.calendarRepository

import com.ice.ktuice.models.lectureCalendarModels.CalendarModel

/**
 * Created by Andrius on 3/10/2018.
 * Repository to store the lectures for a student
 */
interface CalendarRepository {

    fun getByStudCode(studCode: String): CalendarModel?

    /**
     * Uses the currently logged in student to store the calendar
     */
    fun createOrUpdate(calendarModel: CalendarModel)
}
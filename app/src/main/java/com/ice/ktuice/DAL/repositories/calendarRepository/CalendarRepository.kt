package com.ice.ktuice.DAL.repositories.calendarRepository

import com.ice.ktuice.models.lectureCalendarModels.CalendarModel

/**
 * Created by Andrius on 3/10/2018.
 */
interface CalendarRepository {
    fun getByStudCode(studCode: String): CalendarModel?

    fun createOrUpdate(calendarModel: CalendarModel)
}
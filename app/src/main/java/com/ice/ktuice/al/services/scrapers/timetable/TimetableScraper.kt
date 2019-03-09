package com.ice.ktuice.al.services.scrapers.timetable

import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.responses.TimetableResponseModel
import java.util.*

interface TimetableScraper {
    /**
     * @param login - the authentication required to fetch the timetable
     * @param fetchUpcomingTestsFor -  a list of dates, for which weeks to fetch the upcoming tests
     */
    fun getTimetable(login: LoginModel, fetchUpcomingTestsFor: List<Date>? = listOf()): TimetableResponseModel
}
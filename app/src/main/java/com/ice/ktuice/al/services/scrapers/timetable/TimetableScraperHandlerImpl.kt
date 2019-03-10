package com.ice.ktuice.al.services.scrapers.timetable

import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.responses.TimetableResponseModel
import java.util.*

class TimetableScraperHandlerImpl: TimetableScraper {
    override fun getTimetable(login: LoginModel, fetchUpcomingTestsFor: List<Date>?): TimetableResponseModel {
        return TimetableHandler.getTimetable(login, fetchUpcomingTestsFor)
    }
}
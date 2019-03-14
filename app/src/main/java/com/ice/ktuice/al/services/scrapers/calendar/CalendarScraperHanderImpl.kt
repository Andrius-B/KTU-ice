package com.ice.ktuice.al.services.scrapers.calendar

import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel

class CalendarScraperHanderImpl: CalendarScraper {
    override fun getCalendar(login: LoginModel): CalendarModel {
        return CalendarHandler.getCalendar(login)
    }
}
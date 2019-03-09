package com.ice.ktuice.al.services.scrapers.calendar

import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel

interface CalendarScraper {
    fun getCalendar(login: LoginModel): CalendarModel
}
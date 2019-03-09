package com.ice.ktuice.al.services.scrapers.calendar

import biweekly.Biweekly
import biweekly.ICalendar
import com.ice.ktuice.models.lectureCalendarModels.CalendarEvent
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel
import com.ice.ktuice.models.LoginModel
import org.jsoup.Connection
import org.jsoup.Jsoup
/**
 * Created by Andrius on 2/23/2018.
 * Fetches the upcoming events (lectures) from KTU AIS,
 * NOTE: This service does not require authentication, other than the student vidko
 */
class CalendarHandler{
    companion object {
        fun getCalendar(login: LoginModel): CalendarModel{
            val url = String.format("https://uais.cr.ktu.lt/ktuis/tv_rprt2.ical1?p=%s&t=basic.ics", login.studentId)
            val response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute()
            val ical = Biweekly.parse(response.bodyStream()).first()
            val calendarModel = ical.toCalendarModel()
            calendarModel.studCode = login.studentId
            calendarModel.eventList.sortBy { it.dateStart }
            return calendarModel
        }

        private fun ICalendar.toCalendarModel(): CalendarModel {
            val calendar = CalendarModel()
            this.events.forEach{
                val e = CalendarEvent()
                /*
                Each event only has a single category, which is a string value and one of these (or similar):
                "Red Category", "Blue Category", "Green Category"
                 */
                e.categories = it.categories.first().values.first()
                e.dateEnd = it.dateEnd.value
                e.dateStamp = it.dateTimeStamp.value
                e.dateStart = it.dateStart.value
                e.description = it.description.value
                e.location = it.location.value
                e.summary = it.summary.value
                calendar.eventList.add(e)
            }
            return calendar
        }
    }
}
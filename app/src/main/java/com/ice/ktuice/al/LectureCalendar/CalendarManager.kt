package com.ice.ktuice.al.LectureCalendar

import android.graphics.Color
import com.alamkanak.weekview.WeekViewEvent
import com.ice.ktuice.models.LoginModel
import com.ice.ktuice.models.lectureCalendarModels.CalendarEvent
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel
import com.ice.ktuice.scraperService.handlers.CalendarHandler
import com.ice.ktuice.al.LectureCalendar.CalendarListItemModel.ItemType.*
import java.text.DateFormat
import java.util.*


/**
 * Created by Andrius on 2/26/2018.
 */
class CalendarManager {
    private val headerDateFormat = DateFormat.getDateInstance()

    fun getCalendarEventsModelFromWeb(login: LoginModel): List<WeekViewEvent>{
        val calendar = CalendarHandler.getCalendar(login)
        val events = mutableListOf<WeekViewEvent>()
        calendar.eventList.forEach {
            val event = WeekViewEvent()
            // TODO change color of the events
            event.startTime = convertDateToCalendar(it.dateStart)
            event.endTime = convertDateToCalendar(it.dateEnd)
            event.color = Color.CYAN
            event.name = it.summary
            event.location = it.location
            events.add(event)
        }
        return events
    }

    companion object {
        fun convertDateToCalendar(d: Date): Calendar{
            val cal = Calendar.getInstance()
            cal.time = d
            return cal
        }
    }
}
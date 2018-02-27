package com.ice.ktuice.al.LectureCalendar

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

    fun getCalendarEventsModelFromWeb(login: LoginModel): List<CalendarListItemModel>{
        val calendar = CalendarHandler.getCalendar(login)
        val eventList = mutableListOf<CalendarListItemModel>()
        calendar.eventList.forEachIndexed{
            index, it ->
            if(index > 0){
                val eventToInsert = getEventBetween(calendar.eventList[index-1], it)
                if(eventToInsert != null) eventList.add(eventToInsert)
                eventList.add(CalendarListItemModel(it))
            }
        }
        return eventList
    }

    fun getEventBetween(former: CalendarEvent?, latter: CalendarEvent?): CalendarListItemModel? {
        if(former == null || latter == null) return null
        var ret: CalendarListItemModel? = null

        if(!datesOnTheSameDay(former.dateEnd, latter.dateStart)){
            /**
             * the first event ends on a different day, then the latter starts,
             * insert a header with the date between them
             */
            ret = CalendarListItemModel(CalendarEvent())
            ret.dateStart = latter.dateStart
            ret.dateEnd = latter.dateStart
            ret.type = Header
            ret.text = headerDateFormat.format(latter.dateStart)
        }else if(latter.dateEnd.time - former.dateStart.time < 1000*60*60*8){ // if dates are on the same
            /**
             * if both events start at the same time and the difference in start time is less than 8hrs (a working day)
             * provide an item for showing the brake
             */
            ret = CalendarListItemModel(CalendarEvent())
            ret.dateStart = former.dateEnd
            ret.dateEnd = latter.dateStart
            ret.type = Break
        }
        return ret
    }

    fun datesOnTheSameDay(d1: Date, d2: Date): Boolean{
        val cal1 = Calendar.getInstance()
        cal1.time = d1
        val cal2 = Calendar.getInstance()
        cal2.time = d2

        val cal1Day = headerDateFormat.format(d1)
        val cal2Day = headerDateFormat.format(d2)
        return cal1Day == cal2Day
    }
}
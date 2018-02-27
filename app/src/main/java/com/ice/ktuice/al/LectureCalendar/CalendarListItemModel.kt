package com.ice.ktuice.al.LectureCalendar

import android.content.Context
import android.util.EventLog
import com.ice.ktuice.models.lectureCalendarModels.CalendarEvent

/**
 * Created by Andrius on 2/26/2018.
 * The data class for the calendar event adapter.
 * It might contain a Calendar event, but it sometimes does not - this should
 * be treated carefully, and mostly resoled by the type variable
 */
class CalendarListItemModel(private var ce: CalendarEvent? = null){
    /**
     * Length property give a hint of how long the event is, while being a refrence for
     * the displayed items height
     */
    var length = 0
    var type= ItemType.Event
    var text = ""

    enum class ItemType { Event, Header, Break, Dummy }

    /**
     * Realm currently does not support polymorphism, so this just references the original object.
     * Also keep in mind, that this throws , if the item does not have an event associated, such as the headers.
     */
    var dateEnd
        get() = ce!!.dateEnd
        set(value){ ce!!.dateEnd = value }
    fun getEndTimeString() = ce!!.getEndTimeString()

    var dateStart
        get() = ce!!.dateStart
        set(value){ ce!!.dateStart = value }
    fun getStartTimeString() = ce!!.getStartTimeString()

    var dateStamp
        get() = ce!!.dateStamp
        set(value){ ce!!.dateStamp = value }

    var categories
        get() = ce!!.categories
        set(value){ ce!!.categories = value }

    var description
        get() = ce!!.description
        set(value) { ce!!.description = value }

    var location
        get() = ce!!.location
        set(value) { ce!!.location = value }
    fun getLocationString() = ce!!.getLocationString()

    var summary
        get() = ce!!.summary
        set(value){ ce!!.summary = value }

    fun getCategoryColor(c: Context) = ce!!.getCategoryColor(c)
}
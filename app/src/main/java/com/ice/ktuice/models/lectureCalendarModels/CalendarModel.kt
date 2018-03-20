package com.ice.ktuice.models.lectureCalendarModels

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Andrius on 2/23/2018.
 */
open class CalendarModel: RealmObject() {
    @PrimaryKey
    open var studCode: String = ""
    open var eventList: RealmList<CalendarEvent> = RealmList()

    override fun toString(): String {
        eventList.sortBy { it.dateStart }
        var content = String.format("Calendar for student: %s\n\r", studCode)
        eventList.forEach {
            content += String.format("%s\n\r", it.toString())
        }
        return content
    }
}
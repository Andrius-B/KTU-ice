package com.ice.ktuice.ui.main.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alamkanak.weekview.WeekViewEvent
import com.ice.ktuice.R
import com.ice.ktuice.models.lectureCalendarModels.CalendarModel
import com.ice.ktuice.al.services.userService.UserService
import kotlinx.android.synthetic.main.fragment_timetable.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import com.ice.ktuice.al.LectureCalendar.CalendarManager
import io.realm.Realm
import io.realm.RealmChangeListener

/**
 * Created by Andrius on 2/24/2018.
 */
class FragmentTimeTable: Fragment(), KoinComponent {

    private val calendarManager = CalendarManager()

    private val events = mutableListOf<WeekViewEvent>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_timetable, container, false)
        return view
    }

    private var initialLoadChangeListener: RealmChangeListener<Realm>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set an action when any event is clicked.
        week_view.setOnEventClickListener{event, eventRect ->  println("Click on event!")}

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        week_view.setMonthChangeListener { newYear, newMonth ->
            run{
                println("set date set:$newYear $newMonth")
            }
            events
        }

        // Set long press listener for events.
        week_view.setEventLongPressListener{event, eventRect ->  println("Long click on event!")}

        //this is what the developers of the lib use, but i think its a little non responsive if scrolling slowly
        week_view.xScrollingSpeed = -1 * (Math.log(2.5) / Math.log(1.0 / (1 + week_view.numberOfVisibleDays))).toFloat()


        /**
         * Display all the upcoming events
         */
        val calendarSubject = calendarManager.getCalendarModel(this.activity!!)
        calendarSubject.subscribe{
            println("Changed calendar valid:"+it.isValid)
            println("--------------------------------")
            println("________CALENDAR UPDATED________")
            updateWeekViewToCalendar(it)
        }

    }


    private fun updateWeekViewToCalendar(calendar: CalendarModel){
        println("Updating the calendar view!")
        val eventList = weekViewEventsFromCalendar(calendar)
        events.clear()
        events.addAll(eventList)
        week_view.notifyDatasetChanged()
    }

    private fun weekViewEventsFromCalendar(calendar: CalendarModel): List<WeekViewEvent>{
        val list = mutableListOf<WeekViewEvent>()
        calendar.eventList.forEach {
            val event = WeekViewEvent()
            event.startTime = CalendarManager.convertDateToCalendar(it.dateStart)
            event.endTime = CalendarManager.convertDateToCalendar(it.dateEnd)
            event.color = it.getCategoryColor(this.activity!!)
            event.name = it.summary
            event.location = it.getLocationString()
            list.add(event)
        }
        return list
    }
}